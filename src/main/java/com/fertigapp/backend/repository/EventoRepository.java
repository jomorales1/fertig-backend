package com.fertigapp.backend.repository;

import com.fertigapp.backend.model.Evento;
import com.fertigapp.backend.model.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepository extends CrudRepository<Evento, Integer> {
    Iterable<Evento> findByUsuarioE(Usuario usuario);
}
