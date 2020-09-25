package com.fertigapp.backend;

import com.fertigapp.backend.Evento;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepository extends CrudRepository<Evento, Integer> {
}
