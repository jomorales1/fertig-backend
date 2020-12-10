package com.fertigapp.backend.services;

import com.fertigapp.backend.model.FirebaseNotificationToken;
import com.fertigapp.backend.model.Usuario;
import com.fertigapp.backend.repository.FirebaseNTRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FirebaseNTService {

    private final FirebaseNTRepository firebaseNTRepository;

    public FirebaseNTService(FirebaseNTRepository firebaseNTRepository) {
        this.firebaseNTRepository = firebaseNTRepository;
    }

    public Optional<FirebaseNotificationToken> findById(String id) {
        return this.firebaseNTRepository.findById(id);
    }

    public Iterable<FirebaseNotificationToken> findAllByUsuario(Usuario usuario) {
        return this.firebaseNTRepository.findAllByUsuarioF(usuario);
    }

    public FirebaseNotificationToken save(FirebaseNotificationToken firebaseNotificationToken) {
        return this.firebaseNTRepository.save(firebaseNotificationToken);
    }

    public void deleteById(String id) {
        this.firebaseNTRepository.deleteById(id);
    }

}
