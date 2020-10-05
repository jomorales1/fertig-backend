package com.fertigApp.backend.controller;

import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.repository.UsuarioRepository;
import com.fertigApp.backend.requestModels.RequestUsuario;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import java.rmi.server.ExportException;
import java.util.Collections;
import java.util.List;

/*
 * Clase responsable de manejar request de tipo POST con el fin de verificar
 * el token provisionado por el servidor de autenticación de Google.
 * */
@RestController
public class GoogleController {

    // Client ID asociada a la api de autenticación de Google.
    private final String clienId = "756516316743-ocrumr7v1j2kvtlcr2ubdkkih4d3trgo.apps.googleusercontent.com";

    // Repositorio responsable del manejo de la tabla "usuario" en la DB.
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Método POST para la verificación del token obtenido de la API de autenticación de Google.
    @PostMapping(path="/googleAuth/")
    public UsernamePasswordAuthenticationToken GoogleAuthentication(@RequestParam String tokenString){
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(clienId)).build();

        UsernamePasswordAuthenticationToken token = null;
        try {
            GoogleIdToken googleToken = verifier.verify(tokenString);
            if(googleToken != null){
                Payload payLoad = googleToken.getPayload();

                String userId = payLoad.getSubject();
                System.out.println(userId);

                String email = payLoad.getEmail();
                if(!usuarioRepository.findByCorreo(email).iterator().hasNext()){
                    String name = email.substring(0, email.indexOf("@"));
                    String userName = name;
                    while(usuarioRepository.findByUsuario(userName) != null){
                        userName = name;
                        userName += String.valueOf(Math.abs(Math.random()));
                    }

                    Usuario user = new Usuario();
                    user.setCorreo(email);
                    user.setUsuario(userName);
                    user.setNombre((String) payLoad.get("name"));
                    user.setPassword(userName);
                    usuarioRepository.save(user);
                }

                token = new UsernamePasswordAuthenticationToken(email, "user");
            } else {
                System.out.println("Error de token de google");
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }

        return token;
    }
}
