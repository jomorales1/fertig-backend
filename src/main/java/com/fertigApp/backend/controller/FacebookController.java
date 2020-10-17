package com.fertigApp.backend.controller;

import com.fertigApp.backend.auth.jwt.JwtUtil;
import com.fertigApp.backend.auth.services.UserDetailsServiceImpl;
import com.fertigApp.backend.model.Completada;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.payload.response.JwtResponse;
import com.fertigApp.backend.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final Logger LOGGER= LoggerFactory.getLogger(Completada.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostMapping(path="/login/oauth2/code/facebook")
    public ResponseEntity<?>  facebook(@RequestParam String Token) {
        Facebook facebook = new FacebookTemplate(Token);
        final String[] fields = {"email", "picture", "name"};
        User facebookUser = facebook.fetchObject("me", User.class, fields);
        System.out.println(facebookUser.getEmail());
        System.out.println(facebookUser.getName());

        String facebookEmail = facebookUser.getEmail();
        if (usuarioRepository.existsByCorreo(facebookEmail)) {
            Usuario user = usuarioRepository.findByCorreo(facebookEmail);

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

                return ResponseEntity.ok(new JwtResponse(nToken,
                        userDetails.getUsername(),
                        user.getCorreo(),
                        roles));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cuenta usada por otra persona sin estar vinculada con Facebook :/");
            }
        } else {

            Usuario user = new Usuario();
            user.setCorreo(facebookEmail);
            user.setNombre(facebookUser.getName());
            user.setPassword(new String(""));
            user.setFacebook(true);
            user.setGoogle(false);

            String userName = facebookEmail.substring(0, facebookEmail.indexOf("@"));
            String comparator = userName;
            while (usuarioRepository.existsById(comparator)) {
                comparator = userName;
                comparator += String.valueOf((int) (Math.random()));
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
    }
}
