package com.fertigApp.backend.controller;

import com.fertigApp.backend.model.FirebaseNotificationToken;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.payload.response.MessageResponse;
import com.fertigApp.backend.requestModels.PushNotificationRequest;
import com.fertigApp.backend.requestModels.RequestFirebaseToken;
import com.fertigApp.backend.services.FirebaseNTService;
import com.fertigApp.backend.services.PushNotificationService;
import com.fertigApp.backend.services.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class NotificationController {

    private final FirebaseNTService firebaseNTService;

    private final UsuarioService usuarioService;

    public NotificationController(FirebaseNTService firebaseNTService, UsuarioService usuarioService) {
        this.firebaseNTService = firebaseNTService;
        this.usuarioService = usuarioService;
    }

    @GetMapping(path = "/notification/tokens")
    public ResponseEntity<List<String>> getAllTokensByUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(new Usuario());
        List<String> tokens = new ArrayList<>();
        List<FirebaseNotificationToken> notificationTokens = (List<FirebaseNotificationToken>) this.firebaseNTService.findAllByUsuario(usuario);
        for (FirebaseNotificationToken token : notificationTokens) {
            tokens.add(token.getToken());
        }
        return ResponseEntity.ok(tokens);
    }

    @PostMapping(path = "/notification/add-token")
    public ResponseEntity<MessageResponse> addFirebaseToken(@RequestBody RequestFirebaseToken firebaseToken) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(new Usuario());
        FirebaseNotificationToken notificationToken = new FirebaseNotificationToken();
        notificationToken.setUsuarioF(usuario);
        notificationToken.setToken(firebaseToken.getToken());
        this.firebaseNTService.save(notificationToken);
        return ResponseEntity.ok(new MessageResponse("Firebase token registrado con éxito"));
    }

    @DeleteMapping(path = "/notification/delete/{id}")
    public ResponseEntity<MessageResponse> deleteFirebaseToken(@PathVariable String id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<FirebaseNotificationToken> tokenOptional = this.firebaseNTService.findById(id);
        if (tokenOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("El token no esta registrado"));
        }
        FirebaseNotificationToken notificationToken = tokenOptional.get();
        if (!notificationToken.getUsuarioF().getUsuario().equals(userDetails.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("El token no pertenece al usuario"));
        }
        this.firebaseNTService.deleteById(notificationToken.getToken());
        return ResponseEntity.ok(new MessageResponse("El token fue eliminado"));
    }

}
