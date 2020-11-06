package com.fertigApp.backend.repository;

import com.fertigApp.backend.model.Sonido;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SonidoRepository extends CrudRepository<Sonido, String> {

}
