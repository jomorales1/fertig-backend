package com.fertigapp.backend.repository;

import com.fertigapp.backend.model.IdTareaUsuario;
import com.fertigapp.backend.model.Tarea;
import com.fertigapp.backend.model.TareaDeUsuario;
import com.fertigapp.backend.model.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface TareaDeUsuarioRepository extends CrudRepository<TareaDeUsuario, Integer> {
    @Query(value = "select t from Tarea t where t in (select tu.tarea from TareaDeUsuario tu where tu.usuario = :user)")
    Iterable<Tarea> findTareasByUsuario(@Param("user") Usuario user);
    @Query(value = "select t from Tarea t where t in (select tu.tarea from TareaDeUsuario tu where tu.usuario = :user and tu.tarea.hecha = false)")
    Iterable<Tarea> findTareasPendientesByUsuario(@Param("user") Usuario user);
    Iterable<TareaDeUsuario> findAllByTarea(Tarea tarea);
    Optional<TareaDeUsuario> findByUsuarioAndTarea(Usuario usuario, Tarea tarea);

    @Transactional
    void deleteById(IdTareaUsuario id);

    @Transactional
    void deleteAllByTarea(Tarea tarea);

    @Transactional
    void deleteAllByUsuario(Usuario usuario);
}
