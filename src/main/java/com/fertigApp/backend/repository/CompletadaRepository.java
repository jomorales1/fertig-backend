package com.fertigApp.backend.repository;

import com.fertigApp.backend.model.Completada;
import com.fertigApp.backend.model.Rutina;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface CompletadaRepository extends CrudRepository<Completada, Integer> {
    Iterable<Completada> findByRutinaC(Rutina rutina);

//    @Query("select c from Completada c where c.rutinaC = :rutina and c.hecha = false")
//    Optional<Completada> findHechaByRutina(@Param("rutina") Rutina rutina);

    Iterable<Completada> findByRutinaCAndHecha(Rutina rutina, Boolean hecha);

    @Query("select c.fecha from Completada c where c.rutinaC = :rutina and c.hecha = true")
    Iterable<LocalDateTime> findFechasCompletadasByRutina(@Param("rutina") Rutina rutina);

    @Query("select max(c.fechaAjustada) from Completada c where c.rutinaC = :rutina and c.hecha = true")
    LocalDateTime findMaxAjustadaCompletadasByRutina(@Param("rutina") Rutina rutina);

    @Transactional
    void deleteAllByRutinaC(Rutina rutinaC);
}
