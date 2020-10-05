package com.fertigApp.backend.controller;

import com.fertigApp.backend.model.Tarea;
import com.fertigApp.backend.repository.TareaRepository;
import com.fertigApp.backend.repository.UsuarioRepository;
import com.fertigApp.backend.requestModels.RequestTarea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class TareaController {
    @Autowired
    private TareaRepository tareaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping(path="/tasks")
    public @ResponseBody Iterable<Tarea> getAllTareas() {
        return this.tareaRepository.findAll();
    }

    @GetMapping(path="/tasks/getTasks")
    public Iterable<Tarea> getAllTareasByUsuario() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            return usuarioRepository.findById(userDetails.getUsername()).get().getTareas();
        } catch(java.util.NoSuchElementException ex){
            return null;
        }
    }

    @GetMapping(path="/tasks/getTask/{id}")
    public Tarea getTarea(@PathVariable Integer id) {
        return this.tareaRepository.findById(id).get();
    }

    @PutMapping(path="/tasks/updateTask/{id}")
    public Tarea replaceTarea(@PathVariable Integer id, @RequestBody Tarea task) {
        return this.tareaRepository.findById(id)
                .map(tarea -> {
                    tarea = task;
                    this.tareaRepository.save(tarea);
                    return tarea;
                })
                .orElseGet(() -> {
                    this.tareaRepository.save(task);
                    return task;
                });
    }

    @PostMapping(path="/tasks/addTask")
    public @ResponseBody String addNewTarea(@RequestBody RequestTarea requestTarea) {
        Tarea tarea= new Tarea();
        Logger.getGlobal().log(Level.INFO,SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        tarea.setUsuario(usuarioRepository.findById(userDetails.getUsername()).get());
        tarea.setDescripcion(requestTarea.getDescripcion());
        tarea.setEstimacion(requestTarea.getEstimacion());
        tarea.setEtiqueta(requestTarea.getEtiqueta());
        tarea.setFechaFin(requestTarea.getFechaFin());
        tarea.setFechaInicio(requestTarea.getFechaFin());
        tarea.setHecha(requestTarea.getHecha());
        tarea.setNivel(requestTarea.getNivel());
        tarea.setNombre(requestTarea.getNombre());
        tarea.setPrioridad(requestTarea.getPrioridad());
        tarea.setRecordatorio(requestTarea.getRecordatorio());
        this.tareaRepository.save(tarea);
        return "Saved";
    }

    @DeleteMapping(path="/tasks/deleteTask/{id}")
    public void deleteTarea(@RequestParam Integer id) {
        this.tareaRepository.deleteById(id);
    }
}
