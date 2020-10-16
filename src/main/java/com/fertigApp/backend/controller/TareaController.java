package com.fertigApp.backend.controller;

import com.fertigApp.backend.model.Completada;
import com.fertigApp.backend.model.Tarea;
import com.fertigApp.backend.repository.TareaRepository;
import com.fertigApp.backend.repository.UsuarioRepository;
import com.fertigApp.backend.requestModels.RequestTarea;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Clase responsable de manejar request de tipo GET, POST, PUT y DELETE para
 * la entidad "Tarea".
 * */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class TareaController {

    private static final org.slf4j.Logger LOGGER= LoggerFactory.getLogger(Completada.class);

    // Repositorio responsable del manejo de la tabla "tarea" en la DB.
    private final TareaRepository tareaRepository;

    // Repositorio responsable del manejo de la tabla "usuario" en la DB.
    private final UsuarioRepository usuarioRepository;

    public TareaController(TareaRepository tareaRepository, UsuarioRepository usuarioRepository) {
        this.tareaRepository = tareaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Método GET para obtener todas las entidades de tipo "Tarea" almacenadas en la DB.
    @GetMapping(path="/tasks")
    public @ResponseBody Iterable<Tarea> getAllTareas() {
        return this.tareaRepository.findAll();
    }


    // Método GET para obtener todas las tareas de un usuario específico.
    @GetMapping(path="/tasks/getTasks")
    public Iterable<Tarea> getAllTareasByUsuario() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(usuarioRepository.findById(userDetails.getUsername()).isPresent())
            return usuarioRepository.findById(userDetails.getUsername()).get().getTareas();
        return null;

    }

    // Método GET para obtener una entidad de tipo "tarea" por medio de su ID.
    @GetMapping(path="/tasks/getTask/{id}")
    public Tarea getTarea(@PathVariable Integer id) {
        return (this.tareaRepository.findById(id).isPresent() ? this.tareaRepository.findById(id).get() : null);
    }

    @PutMapping(path="/tasks/updateTask/{id}")
    public Tarea replaceTarea(@PathVariable Integer id, @RequestBody RequestTarea task) {
        Object principal =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Logger.getGlobal().log(Level.INFO,principal.toString());
        UserDetails userDetails = (UserDetails) principal;
        return this.tareaRepository.findById(id)
                .map(tarea -> {
                    if(usuarioRepository.findByUsuario(userDetails.getUsername()).isEmpty()){
                        LOGGER.info("User not found");
                        return null;
                    }
                    tarea.setUsuario(usuarioRepository.findByUsuario(userDetails.getUsername()).get());
                    tarea.setNombre(task.getNombre());
                    tarea.setDescripcion(task.getDescripcion());
                    tarea.setPrioridad(task.getPrioridad());
                    tarea.setEtiqueta(task.getEtiqueta());
                    tarea.setEstimacion(task.getEstimacion());
                    tarea.setFechaInicio(task.getFechaInicio());
                    tarea.setFechaFin(task.getFechaFin());
                    tarea.setNivel(task.getNivel());
                    tarea.setHecha(task.getHecha());
                    this.tareaRepository.save(tarea);
                    LOGGER.info("Task updated");
                    return tarea;
                })
                .orElseGet(() -> {
                    LOGGER.info("Task not found");
                    return null;
                });
    }

    // Método POST para agregar un registro en la tabla "tarea" de la DB.
    @PostMapping(path="/tasks/addTask")
    public @ResponseBody ResponseEntity<Void> addNewTarea(@RequestBody RequestTarea requestTarea) {
        Tarea tarea= new Tarea();
        Object principal =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Logger.getGlobal().log(Level.INFO,principal.toString());
        UserDetails userDetails = (UserDetails) principal;
        if(usuarioRepository.findById(userDetails.getUsername()).isPresent()){
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
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // Método DELETE para borrar un registro de la tabla "tarea" en la DB.
    @DeleteMapping(path="/tasks/deleteTask/{id}")
    //@RequestParam
    public void deleteTarea(@PathVariable Integer id) {
        this.tareaRepository.deleteById(id);
    }
}
