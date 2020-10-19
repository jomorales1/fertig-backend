package com.fertigApp.backend.repository;

import com.fertigApp.backend.model.Evento;
import com.fertigApp.backend.model.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepository extends CrudRepository<Evento, Integer> {
    Iterable<Evento> findByUsuarioE(Usuario usuario);
}
