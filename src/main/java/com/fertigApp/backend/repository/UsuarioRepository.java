package com.fertigApp.backend.repository;

import com.fertigApp.backend.model.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, String> {
    Usuario findByCorreo(String correo);
    Optional<Usuario> findByUsuario(String usuario);
    @Query("FROM Usuario WHERE usuario LIKE concat('%',:usuario,'%') ")
    Iterable<Usuario>findAllByUsuario(String usuario);
    boolean existsByCorreo(String correo);
    Boolean existsByUsuario(String username);
}
