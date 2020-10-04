package com.fertigApp.backend.repository;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.repository.CrudRepository;
import com.fertigApp.backend.model.Usuario;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, String> {
    Iterable<Usuario> findByCorreo(String correo);
    Usuario findByUsuario(String usuario);
    boolean existByCorreo(String correo);
}
