package com.fertigapp.backend.repository;

import com.fertigapp.backend.model.Tarea;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TareaRepository extends CrudRepository<Tarea, Integer> {
}
