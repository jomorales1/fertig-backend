package com.fertigApp.backend.repository;

import com.fertigApp.backend.model.TareaDeUsuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TareaDeUsuarioRepository extends CrudRepository<TareaDeUsuario, Integer> {
}
