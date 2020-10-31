package com.fertigApp.backend.controller;

import com.fertigApp.backend.auth.services.UserDetailsImpl;
import com.fertigApp.backend.model.Completada;
import com.fertigApp.backend.model.Tarea;
import com.fertigApp.backend.model.TareaDeUsuario;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.payload.response.MessageResponse;
import com.fertigApp.backend.requestModels.RequestTarea;
import com.fertigApp.backend.services.TareaDeUsuarioService;
import com.fertigApp.backend.services.TareaService;
import com.fertigApp.backend.services.UsuarioService;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private final TareaService tareaService;

    // Repositorio responsable del manejo de la tabla "usuario" en la DB.
    private final UsuarioService usuarioService;

    private final TareaDeUsuarioService tareaDeUsuarioService;

    public TareaController(TareaService tareaService, UsuarioService usuarioService, TareaDeUsuarioService tareaDeUsuarioService) {
        this.tareaService = tareaService;
        this.usuarioService = usuarioService;
        this.tareaDeUsuarioService = tareaDeUsuarioService;
    }

    // Método GET para obtener todas las entidades de tipo "Tarea" almacenadas en la DB.
    @GetMapping(path="/tasks")
    public @ResponseBody Iterable<Tarea> getAllTareas() {
        return this.tareaService.findAll();
    }

    @GetMapping(path="/tasks/getTasks")
    public Iterable<Tarea> getAllTareasByUsuario() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = this.usuarioService.findById(userDetails.getUsername()).get();
        return this.tareaDeUsuarioService.findTareasByUsuario(usuario);
    }

    // Método GET para obtener una entidad de tipo "tarea" por medio de su ID.
    @GetMapping(path="/tasks/getTask/{id}")
    public Tarea getTarea(@PathVariable Integer id) {
        Optional<Tarea> optTarea = this.tareaService.findById(id);
        return (optTarea.orElse(null));
    }

    @PutMapping(path="/tasks/updateTask/{id}")
    public ResponseEntity<Tarea> replaceTarea(@PathVariable Integer id, @RequestBody RequestTarea task) {
        Object principal =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Logger.getGlobal().log(Level.INFO,principal.toString());
        UserDetails userDetails = (UserDetails) principal;

        Optional<Tarea> optionalTarea = tareaService.findById(id);
        Optional<Usuario> optionalUsuario = usuarioService.findByUsuario(userDetails.getUsername());
        if(optionalTarea.isPresent() && optionalUsuario.isPresent()){
            Tarea tarea = optionalTarea.get();
            tarea.setNombre(task.getNombre());
            tarea.setDescripcion(task.getDescripcion());
            tarea.setPrioridad(task.getPrioridad());
            tarea.setEtiqueta(task.getEtiqueta());
            tarea.setEstimacion(task.getEstimacion());
            tarea.setFechaInicio(task.getFechaInicio());
            tarea.setFechaFin(task.getFechaFin());
            tarea.setNivel(task.getNivel());
            tarea.setHecha(task.getHecha());
            this.tareaService.save(tarea);
            LOGGER.info("Task updated");
            return ResponseEntity.ok().body(tarea);
        } else {
            LOGGER.info("Task not found");
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PatchMapping(path="/tasks/checkTask/{id}")
    public ResponseEntity<MessageResponse> checkTarea(@PathVariable Integer id) {
        Optional<Tarea> optionalTarea = tareaService.findById(id);
        if(optionalTarea.isPresent()){
            Tarea tarea = optionalTarea.get();
            UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            tarea.setHecha(!tarea.getHecha());
            this.tareaService.save(tarea);
            return ResponseEntity.ok().body(null);
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Error:Tarea inexistente"));
        }
    }

    // Método POST para agregar un registro en la tabla "tarea" de la DB.
    @PostMapping(path="/tasks/addTask")
    public @ResponseBody ResponseEntity<Void> addNewTarea(@RequestBody RequestTarea requestTarea) {
        TareaDeUsuario tareaDeUsuario = new TareaDeUsuario();
        Tarea tarea= new Tarea();
        Object principal =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Logger.getGlobal().log(Level.INFO,principal.toString());
        UserDetails userDetails = (UserDetails) principal;
        tarea.setDescripcion(requestTarea.getDescripcion());
        tarea.setEstimacion(requestTarea.getEstimacion());
        tarea.setEtiqueta(requestTarea.getEtiqueta());
        tarea.setFechaFin(requestTarea.getFechaFin());
        tarea.setFechaInicio(requestTarea.getFechaInicio());
        tarea.setHecha(requestTarea.getHecha());
        tarea.setNivel(requestTarea.getNivel());
        tarea.setNombre(requestTarea.getNombre());
        tarea.setPrioridad(requestTarea.getPrioridad());
        tarea.setRecordatorio(requestTarea.getRecordatorio());
        if (this.usuarioService.findById(userDetails.getUsername()).isPresent()) {
            tareaDeUsuario.setUsuario(this.usuarioService.findById(userDetails.getUsername()).get());
        }
        tareaDeUsuario.setTarea(this.tareaService.save(tarea));
        tareaDeUsuario.setAdmin(true);
        this.tareaDeUsuarioService.save(tareaDeUsuario);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // TODO: Probar Cascade
    // Método DELETE para borrar un registro de la tabla "tarea" en la DB.
    @DeleteMapping(path="/tasks/deleteTask/{id}")
    public ResponseEntity<Void> deleteTarea(@PathVariable Integer id) {
        if (!this.tareaService.findById(id).isPresent())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Tarea parent = this.tareaService.findById(id).get();
        ArrayList<Tarea> primerNivel = (ArrayList<Tarea>) this.tareaService.findAllByPadre(parent);
        for (Tarea t1 : primerNivel) {
            ArrayList<Tarea> segundoNivel = (ArrayList<Tarea>) this.tareaService.findAllByPadre(t1);
            for (Tarea t2 : segundoNivel) {
                this.tareaService.deleteById(t2.getId());
            }
            this.tareaService.deleteById(t1.getId());
        }
        this.tareaService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // TODO: Verificar que el usuario sea un colaborador
    @GetMapping(path = "/tasks/getOwners/{id}")
    public ResponseEntity<List<Usuario>> getTaskOwners(@PathVariable Integer id) {
        if (!this.tareaService.findById(id).isPresent())
            return ResponseEntity.badRequest().body(null);
        Tarea tarea = this.tareaService.findById(id).get();
        ArrayList<TareaDeUsuario> tareaDeUsuarios = (ArrayList<TareaDeUsuario>) this.tareaDeUsuarioService.findAllByTarea(tarea);
        ArrayList<Usuario> owners = new ArrayList<>();
        for (TareaDeUsuario tareaDeUsuario : tareaDeUsuarios) {
            owners.add(tareaDeUsuario.getUsuario());
        }
        return ResponseEntity.ok(owners);
    }

    // Método para añadir colaborador

    // TODO: Verificar que sea un administrador y verificar que el usuario a agregar sea un colaborador
    @PostMapping(path = "/tasks/addOwner/{id}/{username}")
    public ResponseEntity<Void> addTaskOwner(@PathVariable Integer id, @PathVariable String username) {
        if (!this.tareaService.findById(id).isPresent())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (!this.usuarioService.findById(username).isPresent())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Usuario usuario = this.usuarioService.findById(username).get();
        Tarea tarea = this.tareaService.findById(id).get();
        TareaDeUsuario tareaDeUsuario = new TareaDeUsuario();
        tareaDeUsuario.setUsuario(usuario);
        tareaDeUsuario.setTarea(tarea);
        tareaDeUsuario.setAdmin(true);
        this.tareaDeUsuarioService.save(tareaDeUsuario);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/tasks/addSubTask/{id}")
    public ResponseEntity<Void> addSubTask(@PathVariable Integer id, @RequestBody RequestTarea subTask) {
        if (!this.tareaService.findById(id).isPresent())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Tarea tarea = this.tareaService.findById(id).get();
        if (tarea.getNivel() > 2) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Tarea subtarea = new Tarea();
        subtarea.setDescripcion(subTask.getDescripcion());
        subtarea.setEstimacion(subTask.getEstimacion());
        subtarea.setEtiqueta(subTask.getEtiqueta());
        subtarea.setFechaFin(subTask.getFechaFin());
        subtarea.setFechaInicio(subTask.getFechaInicio());
        subtarea.setHecha(subTask.getHecha());
        subtarea.setNivel(tarea.getNivel() + 1);
        subtarea.setNombre(subTask.getNombre());
        subtarea.setPrioridad(subTask.getPrioridad());
        subtarea.setRecordatorio(subTask.getRecordatorio());
        subtarea.setPadre(tarea);
        tarea.addSubtarea(subtarea);
        this.tareaService.save(tarea);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
