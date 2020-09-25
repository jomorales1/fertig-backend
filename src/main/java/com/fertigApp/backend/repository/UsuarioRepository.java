package com.fertigApp.backend.repository;

import org.springframework.data.repository.CrudRepository;

import com.fertigApp.backend.model.Usuario;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface UsuarioRepository extends CrudRepository<Usuario, String> {

}
