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
                System.out.println("Error de token de google");
                return "Error con el token de google";
            }
        } catch(Exception ex){
            ex.printStackTrace();
            System.out.println(ex.getMessage());
            return "Error verificando el token de google";
        }
    }
}
