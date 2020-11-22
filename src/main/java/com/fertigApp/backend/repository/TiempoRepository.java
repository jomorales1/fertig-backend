package com.fertigApp.backend.repository;

import com.fertigApp.backend.model.IdTiempo;
import com.fertigApp.backend.model.Tarea;
import com.fertigApp.backend.model.Tiempo;
import com.fertigApp.backend.model.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TiempoRepository extends CrudRepository<Tiempo, Integer> {

    @Query(value = "select t from Tiempo t where t.id.tareaDeUsuario.usuario = :user and t.id.tareaDeUsuario.tarea = :task")
    Iterable<Tiempo> findAllByUsuarioAndTarea(@Param("user") Usuario user, @Param("task") Tarea task);

    @Transactional
    void deleteById(IdTiempo id);

}