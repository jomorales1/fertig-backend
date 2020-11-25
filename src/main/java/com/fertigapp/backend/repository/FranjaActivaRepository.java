package com.fertigapp.backend.repository;

import com.fertigapp.backend.model.FranjaActiva;
import com.fertigapp.backend.model.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface FranjaActivaRepository extends CrudRepository<FranjaActiva, Integer> {
    Iterable<FranjaActiva> findAllByUsuarioFL(Usuario usuario);
    Optional<FranjaActiva> findAllByUsuarioFLAndDay(Usuario usuario, Integer day);

    @Transactional
    void deleteAllByUsuarioFL(Usuario usuario);
}
