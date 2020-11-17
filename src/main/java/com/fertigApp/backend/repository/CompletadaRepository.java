package com.fertigApp.backend.repository;

import com.fertigApp.backend.model.Completada;
import com.fertigApp.backend.model.Rutina;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Repository
public interface CompletadaRepository extends CrudRepository<Completada, Integer> {
    Iterable<Completada> findByRutinaC(Rutina rutina);

    Completada findTopByRutinaCAndHecha(Rutina rutina, Boolean hecha);

    @Query("select c.fecha from Completada c where c.rutinaC = :rutina and c.hecha = true")
    Iterable<OffsetDateTime> findFechasCompletadasByRutina(@Param("rutina") Rutina rutina);

    @Query("select max(c.fecha) from Completada c where c.rutinaC = :rutina and c.hecha = false")
    OffsetDateTime findFechaNoCompletadaByRutina(@Param("rutina") Rutina rutina);

    @Query("select max(c.fecha) from Completada c where c.rutinaC = :rutina and c.hecha = true")
    OffsetDateTime findMaxFechaCompletadaByRutina(@Param("rutina") Rutina rutina);

    @Transactional
    void deleteAllByRutinaC(Rutina rutinaC);

    void deleteById(Integer id);

    @Query("select c from Completada c where c.rutinaC = :rutina and c.fecha = (select max(cc.fecha) from Completada cc where cc.rutinaC = :rutina and cc.hecha = true) and c.hecha = true")
    Completada findMaxCompletada(@Param("rutina") Rutina rutina);
}
