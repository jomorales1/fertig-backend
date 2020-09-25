package com.fertigapp.backend;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompletadaRepository extends CrudRepository<Completada, Integer> {
}
