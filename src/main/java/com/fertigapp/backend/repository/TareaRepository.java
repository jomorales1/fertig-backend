package com.fertigapp.backend.repository;

import com.fertigapp.backend.model.Tarea;
import com.fertigapp.backend.model.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface TareaRepository extends CrudRepository<Tarea, Integer> {
    Iterable<Tarea> findAllByPadre(Tarea padre);

    @Query("select count(t) from Tarea t where t.fechaFin >= :inicio and t.fechaFin < :fin and t.hecha = true and t.id in (select tu.tarea from TareaDeUsuario tu where tu.usuario = :usuario)")
    Integer countTareasBetween(OffsetDateTime inicio, OffsetDateTime fin, Usuario usuario);
}