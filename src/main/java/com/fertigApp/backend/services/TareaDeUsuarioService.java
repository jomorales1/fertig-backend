package com.fertigApp.backend.services;

import com.fertigApp.backend.model.Tarea;
import com.fertigApp.backend.model.TareaDeUsuario;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.repository.TareaDeUsuarioRepository;
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

    public Iterable<TareaDeUsuario> findAllByTarea(Tarea tarea) {
        return tareaDeUsuarioRepository.findAllByTarea(tarea);
    }

    public TareaDeUsuario save(TareaDeUsuario tarea){
        return tareaDeUsuarioRepository.save(tarea);
    }

    public void deleteById(Integer id){
        tareaDeUsuarioRepository.deleteById(id);
    }
}
