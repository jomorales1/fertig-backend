package com.fertigApp.backend.repository;

import com.fertigApp.backend.model.Rutina;
import com.fertigApp.backend.model.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RutinaRepository extends CrudRepository<Rutina, Integer> {
    Iterable<Rutina> findByUsuarioR(Usuario usuario);
}
