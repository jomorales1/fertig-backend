package com.fertigapp.backend.controller;

import com.fertigapp.backend.auth.jwt.JwtUtil;
import com.fertigapp.backend.auth.services.UserDetailsServiceImpl;
import com.fertigapp.backend.model.Usuario;
import com.fertigapp.backend.payload.response.JwtResponse;
import com.fertigapp.backend.services.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class FacebookController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FacebookController.class);

    private final UsuarioService usuarioService;

    private final JwtUtil jwtUtil;

    private final UserDetailsServiceImpl userDetailsService;

    private final Random random;

    public FacebookController(UsuarioService usuarioService, JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) throws NoSuchAlgorithmException {
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.random = SecureRandom.getInstanceStrong();
    }

    @PostMapping(path="/login/oauth2/code/facebook")
    public ResponseEntity<JwtResponse> facebook(@RequestParam String token) {
        Facebook facebook = new FacebookTemplate(token);
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
                        user.getNombre(),
                        user.getCorreo(),
                        roles));
            } else {
                LOGGER.info("Se ha intentado iniciar sesión con Facebook a una cuenta no asociada");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Message","Cuenta usada por otra persona sin estar vinculada con Facebook").body(null);
            }
        } else {

            Usuario user = new Usuario();
            user.setCorreo(facebookEmail);
            user.setNombre(facebookUser.getName());
            user.setPassword("");
            user.setFacebook(true);
            user.setGoogle(false);

            String userName;
            if(facebookEmail.contains("@facebook.com")){
                userName = facebookUser.getName().replace(" ", "");
            } else {
                userName = facebookEmail.substring(0, facebookEmail.indexOf("@"));
            }
            StringBuilder builder = new StringBuilder();
            builder.append(userName);
            while (usuarioService.existsById(builder.toString())) {
                builder = new StringBuilder();
                builder.append(userName);
                builder.append(this.random.nextInt());
            }
            userName = builder.toString();
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
                    user.getNombre(),
                    user.getCorreo(),
                    roles));
        }
    }
}
