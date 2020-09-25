package com.fertigapp.backend.repository;

import com.fertigapp.backend.model.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface UsuarioRepository extends CrudRepository<Usuario, String> {
}
