package com.fertigApp.backend.repository;

import com.fertigApp.backend.model.Completada;
import com.fertigApp.backend.model.Rutina;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CompletadaRepository extends CrudRepository<Completada, Integer> {
    Iterable<Completada> findByRutinaC(Rutina rutina);

    @Transactional
    void deleteAllByRutinaC(Rutina rutinaC);
}
