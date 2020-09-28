package com.fertigApp.backend.repository;

import com.fertigApp.backend.model.Completada;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompletadaRepository extends CrudRepository<Completada, Integer> {
}
