package com.fertigApp.backend.repository;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.repository.CrudRepository;
import com.fertigApp.backend.model.Usuario;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, String> {
    Usuario findByCorreo(String correo);
    Optional<Usuario> findByUsuario(String usuario);
    boolean existsByCorreo(String correo);
    Boolean existsByUsuario(String username);
}
