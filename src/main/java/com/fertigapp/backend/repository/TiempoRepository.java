package com.fertigapp.backend.repository;

import com.fertigapp.backend.model.IdTiempo;
import com.fertigapp.backend.model.Tarea;
import com.fertigapp.backend.model.Tiempo;
import com.fertigapp.backend.model.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Repository
public interface TiempoRepository extends CrudRepository<Tiempo, Integer> {

    @Query(value = "select t from Tiempo t where t.id.tareaDeUsuario.usuario = :user and t.id.tareaDeUsuario.tarea = :task")
    Iterable<Tiempo> findAllByUsuarioAndTarea(@Param("user") Usuario user, @Param("task") Tarea task);

    @Transactional
    void deleteById(IdTiempo id);

    @Query("select sum(t.invertido) from Tiempo t where t.id.fecha >= :inicio and t.id.fecha < :fin and t.id.tareaDeUsuario.usuario = :usuario")
    Integer countTiempoTareasBetween(OffsetDateTime inicio, OffsetDateTime fin, Usuario usuario);

}
