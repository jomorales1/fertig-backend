package com.fertigApp.backend.repository;

import com.fertigApp.backend.model.FirebaseNotificationToken;
import com.fertigApp.backend.model.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FirebaseNTRepository extends CrudRepository<FirebaseNotificationToken, String> {
    Iterable<FirebaseNotificationToken> findAllByUsuarioF(Usuario usuarioF);
}
