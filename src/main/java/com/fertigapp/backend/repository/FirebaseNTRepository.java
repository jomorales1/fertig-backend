package com.fertigapp.backend.repository;

import com.fertigapp.backend.model.FirebaseNotificationToken;
import com.fertigapp.backend.model.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FirebaseNTRepository extends CrudRepository<FirebaseNotificationToken, String> {
    Iterable<FirebaseNotificationToken> findAllByUsuarioF(Usuario usuarioF);
}
