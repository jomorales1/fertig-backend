package com.fertigapp.backend.services;

import com.fertigapp.backend.model.IdTareaUsuario;
import com.fertigapp.backend.model.Tarea;
import com.fertigapp.backend.model.TareaDeUsuario;
import com.fertigapp.backend.model.Usuario;
import com.fertigapp.backend.repository.TareaDeUsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TareaDeUsuarioService {

    private final TareaDeUsuarioRepository tareaDeUsuarioRepository;

    public TareaDeUsuarioService(TareaDeUsuarioRepository tareaDeUsuarioRepository) {
        this.tareaDeUsuarioRepository = tareaDeUsuarioRepository;
    }

    public Iterable<TareaDeUsuario> findAll(){
        return tareaDeUsuarioRepository.findAll();
    }

    public Optional<TareaDeUsuario> findById(Integer id){
        return tareaDeUsuarioRepository.findById(id);
    }

    public Iterable<Tarea> findTareasByUsuario(Usuario usuario) {
        return tareaDeUsuarioRepository.findTareasByUsuario(usuario);
    }

    public Iterable<Tarea> findTareasPendientesByUsuario(Usuario usuario) {
        return tareaDeUsuarioRepository.findTareasPendientesByUsuario(usuario);
    }

    public Iterable<TareaDeUsuario> findAllByTarea(Tarea tarea) {
        return tareaDeUsuarioRepository.findAllByTarea(tarea);
    }

    public Optional<TareaDeUsuario> findByUsuarioAndTarea(Usuario usuario, Tarea tarea) {
        return this.tareaDeUsuarioRepository.findByUsuarioAndTarea(usuario, tarea);
    }

    public TareaDeUsuario save(TareaDeUsuario tarea){
        return tareaDeUsuarioRepository.save(tarea);
    }

    public void deleteById(IdTareaUsuario id){
        tareaDeUsuarioRepository.deleteById(id);
    }

    public void deleteAllByUsuario(Usuario usuario) {
        this.tareaDeUsuarioRepository.deleteAllByUsuario(usuario);
    }

    public void deleteAllByTarea(Tarea tarea) {
        this.tareaDeUsuarioRepository.deleteAllByTarea(tarea);
    }
}
