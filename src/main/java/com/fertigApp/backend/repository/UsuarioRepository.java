package com.fertigApp.backend.repository;

import com.fertigApp.backend.model.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, String> {
    Usuario findByCorreo(String correo);
    Usuario findByUsuario(String usuario);
    boolean existsByCorreo(String correo);
}
