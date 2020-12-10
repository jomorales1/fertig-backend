package com.fertigapp.backend.repository;

import com.fertigapp.backend.model.Sonido;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SonidoRepository extends CrudRepository<Sonido, String> {

}
