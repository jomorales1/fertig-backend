package com.fertigApp.backend.repository;

import com.fertigApp.backend.model.Tarea;
import com.fertigApp.backend.model.TareaDeUsuario;
import com.fertigApp.backend.model.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TareaDeUsuarioRepository extends CrudRepository<TareaDeUsuario, Integer> {
    @Query(value = "select t from Tarea t where t in (select tu.tarea from TareaDeUsuario tu where tu.usuario = :user)")
    Iterable<Tarea> findTareasByUsuario(@Param("user") Usuario user);
    Iterable<TareaDeUsuario> findAllByTarea(Tarea tarea);
}
