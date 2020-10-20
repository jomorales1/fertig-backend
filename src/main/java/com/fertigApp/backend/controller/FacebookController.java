package com.fertigApp.backend.controller;

import com.fertigApp.backend.auth.jwt.JwtUtil;
import com.fertigApp.backend.auth.services.UserDetailsServiceImpl;
import com.fertigApp.backend.model.Completada;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.payload.response.JwtResponse;
import com.fertigApp.backend.services.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class FacebookController {

    private static final Logger LOGGER = LoggerFactory.getLogger(Completada.class);

    private final UsuarioService usuarioService;

    private final JwtUtil jwtUtil;

    private final AuthenticationManager authenticationManager;

    private final UserDetailsServiceImpl userDetailsService;

    public FacebookController(UsuarioService usuarioService, JwtUtil jwtUtil, AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService) {
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping(path="/login/oauth2/code/facebook")
    public ResponseEntity<?>  facebook(@RequestParam String Token) {
        Facebook facebook = new FacebookTemplate(Token);
        final String[] fields = {"email", "name"};
        User facebookUser = facebook.fetchObject("me", User.class, fields);

        String facebookEmail = facebookUser.getEmail();
        if(facebookEmail == null) {
            facebookEmail = facebookUser.getId() + "@facebook.com";
        }

        if (usuarioService.existsByCorreo(facebookEmail)) {
            Usuario user = usuarioService.findByCorreo(facebookEmail);

            if (user.isFacebook()) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsuario());

                //se autentica al usuario ante spring para acceder a los recursos
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                String nToken = jwtUtil.generateJwtToken(authentication);

                SecurityContextHolder.getContext().setAuthentication(authentication);

                List<String> roles = userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList());

                LOGGER.info("Un usuario ha iniciado sesión con su cuenta de facebook");
                return ResponseEntity.ok(new JwtResponse(nToken,
                        userDetails.getUsername(),
                        user.getCorreo(),
                        roles));
            } else {
                LOGGER.info("Se ha intentado iniciar sesión con Facebook a una cuenta no asociada");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cuenta usada por otra persona sin estar vinculada con Facebook :/");
            }
        } else {

            Usuario user = new Usuario();
            user.setCorreo(facebookEmail);
            user.setNombre(facebookUser.getName());
            user.setPassword(new String(""));
            user.setFacebook(true);
            user.setGoogle(false);

            String userName;
            if(facebookEmail.contains("@facebook.com")){
                userName = facebookUser.getName().replace(" ", "");
            } else {
                userName = facebookEmail.substring(0, facebookEmail.indexOf("@"));
            }
            String comparator = userName;
            while (usuarioService.existsById(comparator)) {
                comparator = userName;
                comparator += String.valueOf((int) (Math.random()));
            }
            userName = comparator;
            user.setUsuario(userName);

            usuarioService.save(user);

            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsuario());

            //se autentica al usuario ante spring para acceder a los recursos
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            String nToken = jwtUtil.generateJwtToken(authentication);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            LOGGER.info("Se ha registrado un usuario a través de Facebook");
            return ResponseEntity.ok(new JwtResponse(nToken,
                    userDetails.getUsername(),
                    user.getCorreo(),
                    roles));
        }
    }
}
