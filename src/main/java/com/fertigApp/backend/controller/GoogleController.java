package com.fertigApp.backend.controller;

import com.fertigApp.backend.model.Completada;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.repository.UsuarioRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

/*
 * Clase responsable de manejar request de tipo POST con el fin de verificar
 * el token provisionado por el servidor de autenticación de Google.
 * */
@RestController
public class GoogleController {

    private static final Logger LOGGER= LoggerFactory.getLogger(Completada.class);

    // Client ID asociada a la api de autenticación de Google.
    private final String clienId = "756516316743-7fcc8028epqmhnftjeclt9dqo0dk3tls.apps.googleusercontent.com";

    // Repositorio responsable del manejo de la tabla "usuario" en la DB.
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Método POST para la verificación del token obtenido de la API de autenticación de Google.
    @PostMapping(path="/login/oauth2/code/google")
    public String GoogleAuthentication(@RequestParam String Token){
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(clienId)).build();
        try {
            GoogleIdToken googleToken = verifier.verify(Token);
            if(googleToken != null){
                Payload payLoad = googleToken.getPayload();

                String googleEmail = payLoad.getEmail();
                if(usuarioRepository.existsByCorreo(googleEmail)){
                    Usuario user = usuarioRepository.findByCorreo(googleEmail);

                    if(user.isGoogle()){
                        return "Verificación exitosa";
                    } else {
                        return "Cuenta usada por otra persona sin estar vinculada con Google :/";
                    }
                } else {
                    return "No hay ninguna cuenta con ese correo";
                }
            } else {
                LOGGER.info("Error de token de google");
                return "Error con el token de google";
            }
        } catch(Exception ex){
            ex.printStackTrace();
            LOGGER.info(ex.getMessage());
            return "Error verificando el token de google";
        }
    }
}
