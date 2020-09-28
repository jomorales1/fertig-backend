package com.fertigapp.backend.repository;

import com.fertigapp.backend.model.Rutina;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RutinaRepository extends CrudRepository<Rutina, Integer> {
}
