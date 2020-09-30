package com.fertigApp.backend.repository;

import com.fertigApp.backend.model.Tarea;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TareaRepository extends CrudRepository<Tarea, Integer> {

    public List<Tarea> findByUsuarioT(String usuarioT);
}
