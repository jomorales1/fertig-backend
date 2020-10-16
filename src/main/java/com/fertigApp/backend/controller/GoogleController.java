package com.fertigApp.backend.controller;

import com.fertigApp.backend.auth.jwt.JwtUtil;
import com.fertigApp.backend.auth.services.UserDetailsServiceImpl;
import com.fertigApp.backend.model.Completada;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.payload.response.JwtResponse;
import com.fertigApp.backend.repository.UsuarioRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/*
 * Clase responsable de manejar request de tipo POST con el fin de verificar
 * el token provisionado por el servidor de autenticación de Google.
 * */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class GoogleController {

    private static final Logger LOGGER= LoggerFactory.getLogger(Completada.class);

    // Repositorio responsable del manejo de la tabla "usuario" en la DB.
    private final UsuarioRepository usuarioRepository;

    private final JwtUtil jwtUtil;

    final
    AuthenticationManager authenticationManager;

    private final UserDetailsServiceImpl userDetailsService;

    public GoogleController(UsuarioRepository usuarioRepository, JwtUtil jwtUtil, AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService) {
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    // Método POST para la verificación del token obtenido de la API de autenticación de Google.
    @PostMapping(path="/login/oauth2/code/google")
    public ResponseEntity<?> GoogleAuthentication(@RequestParam String Token){

        // Client ID asociada a la api de autenticación de Google.
        String clienId = "756516316743-7fcc8028epqmhnftjeclt9dqo0dk3tls.apps.googleusercontent.com";
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
                .Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(clienId)).build();

        try {
            GoogleIdToken googleToken = verifier.verify(Token);

            if(googleToken != null){
                Payload payLoad = googleToken.getPayload();
                String googleEmail = payLoad.getEmail();

                if(usuarioRepository.existsByCorreo(googleEmail)){
                    Usuario user = usuarioRepository.findByCorreo(googleEmail);

                    if(user.isGoogle()){
                        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsuario());
                        //se autentica al usuario ante spring para acceder a los recursos
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        String nToken = jwtUtil.generateJwtToken(authentication);

                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        List<String> roles = userDetails.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList());

                        return ResponseEntity.ok(new JwtResponse(nToken,
                                userDetails.getUsername(),
                                user.getCorreo(),
                                roles));
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cuenta usada por otra persona sin estar vinculada con Google :/");
                    }
                } else {
                    Usuario user = new Usuario();
                    user.setCorreo(googleEmail);
                    user.setNombre(payLoad.get("name").toString());
                    user.setPassword("");
                    user.setFacebook(false);
                    user.setGoogle(true);

                    String userName = googleEmail.substring(0, googleEmail.indexOf("@"));
                    String comparator = userName;
                    while(usuarioRepository.existsById(comparator)){
                        comparator = userName;
                        comparator += String.valueOf((int)(Math.random()));
                    }
                    userName = comparator;
                    user.setUsuario(userName);

                    usuarioRepository.save(user);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsuario());
                    //se autentica al usuario ante spring para acceder a los recursos
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    String nToken = jwtUtil.generateJwtToken(authentication);

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    List<String> roles = userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList());

                    return ResponseEntity.ok(new JwtResponse(nToken,
                            userDetails.getUsername(),
                            user.getCorreo(),
                            roles));
                }
            } else {
                LOGGER.info("Error de token de google");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error con el token de google");
            }
        } catch(Exception ex){
            ex.printStackTrace();
            LOGGER.info(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error verificando el token de google");
        }
    }
}
