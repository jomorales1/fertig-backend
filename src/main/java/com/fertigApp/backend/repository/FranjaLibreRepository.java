package com.fertigApp.backend.repository;

import com.fertigApp.backend.model.FranjaLibre;
import com.fertigApp.backend.model.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface FranjaLibreRepository extends CrudRepository<FranjaLibre, Integer> {
    Iterable<FranjaLibre> findAllByUsuarioFL(Usuario usuario);
    Iterable<FranjaLibre> findAllByUsuarioFLAAndDay(Usuario usuario, Integer day);

    @Transactional
    void deleteByUsuarioFL(Usuario usuario);
}
