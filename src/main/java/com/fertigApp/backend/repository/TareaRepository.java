package com.fertigApp.backend.repository;

import com.fertigApp.backend.model.Tarea;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TareaRepository extends CrudRepository<Tarea, Integer> {
//    Iterable<Tarea> findByUsuarioT(Usuario usuario);
}
