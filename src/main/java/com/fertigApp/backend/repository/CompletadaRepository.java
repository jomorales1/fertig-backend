package com.fertigApp.backend.repository;

import com.fertigApp.backend.model.Completada;
import com.fertigApp.backend.model.Rutina;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CompletadaRepository extends CrudRepository<Completada, Integer> {
    Iterable<Completada> findByRutinaC(Rutina rutina);

//    @Query("select c from Completada c where c.rutinaC = :rutina and c.hecha = false")
//    Optional<Completada> findHechaByRutina(@Param("rutina") Rutina rutina);

    Iterable<Completada> findByRutinaAndHecha(Rutina rutina, Boolean hecha);

    @Transactional
    void deleteAllByRutinaC(Rutina rutinaC);
}
