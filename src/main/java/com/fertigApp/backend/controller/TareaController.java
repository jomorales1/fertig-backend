package com.fertigApp.backend.controller;

import com.fertigApp.backend.model.Rutina;
import com.fertigApp.backend.model.Tarea;
import com.fertigApp.backend.model.TareaDeUsuario;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.payload.response.MessageResponse;
import com.fertigApp.backend.requestModels.RequestTarea;
import com.fertigApp.backend.services.RutinaService;
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

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Tarea.class);

    // Repositorio responsable del manejo de la tabla "tarea" en la DB.
    private final TareaService tareaService;

    // Repositorio responsable del manejo de la tabla "usuario" en la DB.
    private final UsuarioService usuarioService;

    private final RutinaService rutinaService;

    private final TareaDeUsuarioService tareaDeUsuarioService;

    public TareaController(TareaService tareaService, UsuarioService usuarioService, RutinaService rutinaService, TareaDeUsuarioService tareaDeUsuarioService) {
        this.tareaService = tareaService;
        this.usuarioService = usuarioService;
        this.rutinaService = rutinaService;
        this.tareaDeUsuarioService = tareaDeUsuarioService;
    }

    // Método GET para obtener todas las entidades de tipo "Tarea" almacenadas en la DB.
    @GetMapping(path="/tasks")
    public @ResponseBody Iterable<Tarea> getAllTareas() {
        return this.tareaService.findAll();
    }

    @GetMapping(path="/tasks/getTasks")
    public ResponseEntity<List<Tarea>> getAllTareasByUsuario() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(null);
        List<Tarea> tareas = (List<Tarea>) this.tareaDeUsuarioService.findTareasByUsuario(usuario);
        return ResponseEntity.ok(tareas);
    }

    // Método GET para obtener una entidad de tipo "tarea" por medio de su ID.
    @GetMapping(path="/tasks/getTask/{id}")
    public ResponseEntity<Tarea> getTarea(@PathVariable Integer id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!this.tareaService.findById(id).isPresent()) {
            LOGGER.info("Tarea no encontrada");
            return ResponseEntity.badRequest().body(null);
        }
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(null);
        Tarea tarea = this.tareaService.findById(id).get();
        if (!this.tareaDeUsuarioService.findByUsuarioAndTarea(usuario, tarea).isPresent()) {
            LOGGER.info("La tarea no pertenece al usuario");
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(tarea);
    }

    @PutMapping(path="/tasks/updateTask/{id}")
    public ResponseEntity<Tarea> replaceTarea(@PathVariable Integer id, @RequestBody RequestTarea task) {
        Object principal =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Logger.getGlobal().log(Level.INFO,principal.toString());
        UserDetails userDetails = (UserDetails) principal;
        if (!this.tareaService.findById(id).isPresent()) {
            LOGGER.info("Tarea no encontrada");
            return ResponseEntity.badRequest().body(null);
        }
        Optional<Usuario> optionalUsuario = usuarioService.findByUsuario(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(null);
        Tarea tarea = this.tareaService.findById(id).get();
        if (!this.tareaDeUsuarioService.findByUsuarioAndTarea(usuario, tarea).isPresent()) {
            LOGGER.info("La tarea no pertenece al usuario");
            return ResponseEntity.badRequest().body(null);
        }
        tarea.setNombre(task.getNombre());
        tarea.setDescripcion(task.getDescripcion());
        tarea.setPrioridad(task.getPrioridad());
        tarea.setEtiqueta(task.getEtiqueta());
        tarea.setEstimacion(task.getEstimacion());
        tarea.setFechaFin(task.getFechaFin());
        tarea.setHecha(task.getHecha());
        tarea.setRecordatorio(task.getRecordatorio());
        tarea.setTiempoInvertido(task.getTiempoInvertido());
        this.tareaService.save(tarea);
        LOGGER.info("Tarea actualizada");
        return ResponseEntity.ok().body(tarea);
    }

    @PatchMapping(path="/tasks/checkTask/{id}")
    public ResponseEntity<MessageResponse> checkTarea(@PathVariable Integer id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!this.tareaService.findById(id).isPresent()) {
            LOGGER.info("Tarea no encontrada");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: tarea no encontrada"));
        }
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(null);
        Tarea tarea = this.tareaService.findById(id).get();
        if (!this.tareaDeUsuarioService.findByUsuarioAndTarea(usuario, tarea).isPresent()) {
            LOGGER.info("La tarea no pertenece al usuario");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: La tarea no pertenece al usuario"));
        }
        tarea.setHecha(!tarea.getHecha());
        this.tareaService.save(tarea);
        return ResponseEntity.ok().body(new MessageResponse("Tarea chequeada"));
    }

    // Método POST para agregar un registro en la tabla "tarea" de la DB.
    @PostMapping(path="/tasks/addTask")
    public @ResponseBody ResponseEntity<MessageResponse> addNewTarea(@RequestBody RequestTarea requestTarea) {
        Object principal =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Logger.getGlobal().log(Level.INFO,principal.toString());
        UserDetails userDetails = (UserDetails) principal;
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(null);
        Tarea tarea = new Tarea();
        tarea.setDescripcion(requestTarea.getDescripcion());
        tarea.setEstimacion(requestTarea.getEstimacion());
        tarea.setEtiqueta(requestTarea.getEtiqueta());
        tarea.setFechaFin(requestTarea.getFechaFin());
        tarea.setHecha(requestTarea.getHecha());
        tarea.setNivel(1);
        tarea.setNombre(requestTarea.getNombre());
        tarea.setPrioridad(requestTarea.getPrioridad());
        tarea.setRecordatorio(requestTarea.getRecordatorio());
        tarea.setTiempoInvertido(0);
        TareaDeUsuario tareaDeUsuario = new TareaDeUsuario();
        tareaDeUsuario.setUsuario(usuario);
        tareaDeUsuario.setTarea(this.tareaService.save(tarea));
        tareaDeUsuario.setAdmin(true);
        this.tareaDeUsuarioService.save(tareaDeUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Tarea creada"));
    }

    // Método DELETE para borrar un registro de la tabla "tarea" en la DB.
    @DeleteMapping(path="/tasks/deleteTask/{id}")
    public ResponseEntity<MessageResponse> deleteTarea(@PathVariable Integer id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!this.tareaService.findById(id).isPresent()) {
            LOGGER.info("Tarea no encontrada");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: tarea no encontrada"));
        }
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(null);
        Tarea tarea = this.tareaService.findById(id).get();
        if (!this.tareaDeUsuarioService.findByUsuarioAndTarea(usuario,tarea).isPresent()) {
            LOGGER.info("La tarea no pertenece al usuario");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: la tarea no pertenece al usuario"));
        }
        if (!this.tareaDeUsuarioService.findByUsuarioAndTarea(usuario,tarea).get().isAdmin()) {
            LOGGER.info("El usuario no es un administrador de la tarea");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: el usuario no es un administrador de la tarea"));
        }
        this.tareaDeUsuarioService.deleteAllByTarea(tarea);
        this.tareaService.deleteById(tarea.getId());
        return ResponseEntity.ok(new MessageResponse("Tarea eliminada"));
    }

    // Método GET para obtener todos los colaboradores de una tarea
    @GetMapping(path = "/tasks/getOwners/{id}")
    public ResponseEntity<List<Usuario>> getTaskOwners(@PathVariable Integer id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!this.tareaService.findById(id).isPresent()) {
            LOGGER.info("Tarea no encontrada");
            return ResponseEntity.badRequest().body(null);
        }
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(null);
        Tarea tarea = this.tareaService.findById(id).get();
        if (!this.tareaDeUsuarioService.findByUsuarioAndTarea(usuario, tarea).isPresent()) {
            LOGGER.info("La tarea no pertenece al usuario");
            return ResponseEntity.badRequest().body(null);
        }
        ArrayList<TareaDeUsuario> tareaDeUsuarios = (ArrayList<TareaDeUsuario>) this.tareaDeUsuarioService.findAllByTarea(tarea);
        ArrayList<Usuario> owners = new ArrayList<>();
        for (TareaDeUsuario tareaDeUsuario : tareaDeUsuarios) {
            owners.add(tareaDeUsuario.getUsuario());
        }
        return ResponseEntity.ok(owners);
    }

    // Método POST para añadir un administrador
    @PostMapping(path = "/tasks/addAdmin/{id}/{username}")
    public ResponseEntity<MessageResponse> addTaskAdmin(@PathVariable Integer id, @PathVariable String username) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!this.tareaService.findById(id).isPresent()) {
            LOGGER.info("Tarea no encontrada");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: tarea no encontrada"));
        }
        if (!this.usuarioService.findById(username).isPresent()) {
            LOGGER.info("Usuario no encontrado");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: usuario no encontrado"));
        }
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario admin = optionalUsuario.orElse(null);
        Usuario usuario = this.usuarioService.findById(username).get();
        Tarea tarea = this.tareaService.findById(id).get();
        if (!this.tareaDeUsuarioService.findByUsuarioAndTarea(admin, tarea).isPresent()) {
            LOGGER.info("La tarea no pertenece al usuario");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: la tarea no pertenece al usuario"));
        }
        if (!this.tareaDeUsuarioService.findByUsuarioAndTarea(usuario, tarea).isPresent()) {
            LOGGER.info("El nuevo usuario no es un colaborador");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: el usuario no es un colaborador"));
        }
        if (!this.tareaDeUsuarioService.findByUsuarioAndTarea(admin, tarea).get().isAdmin()) {
            LOGGER.info("El usuario no es un administrador de la tarea");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: el usuario no es un administrador de la tarea"));
        }
        TareaDeUsuario tareaDeUsuario = this.tareaDeUsuarioService.findByUsuarioAndTarea(usuario, tarea).get();
        tareaDeUsuario.setAdmin(true);
        this.tareaDeUsuarioService.save(tareaDeUsuario);
        return ResponseEntity.ok(new MessageResponse("Administrador agregado"));
    }

    // Método POST para añadir un colaborador a una tarea
    @PostMapping(path = "/tasks/addOwner/{id}/{username}")
    public ResponseEntity<MessageResponse> addTaskOwner(@PathVariable Integer id, @PathVariable String username) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!this.tareaService.findById(id).isPresent()) {
            LOGGER.info("Tarea no encontrada");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: tarea no encontrada"));
        }
        if (!this.usuarioService.findById(username).isPresent()) {
            LOGGER.info("Usuario no encontrado");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: usuario no encontrado"));
        }
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario admin = optionalUsuario.orElse(null);
        Usuario usuario = this.usuarioService.findById(username).get();
        Tarea tarea = this.tareaService.findById(id).get();
        if (!this.tareaDeUsuarioService.findByUsuarioAndTarea(admin, tarea).isPresent()) {
            LOGGER.info("La tarea no pertenece al usuario");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: la tarea no pertenece al usuario"));
        }
        if (!this.tareaDeUsuarioService.findByUsuarioAndTarea(admin, tarea).get().isAdmin()) {
            LOGGER.info("El usuario no es un administrador de la tarea");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: el usuario no es un administrador de la tarea"));
        }
        TareaDeUsuario tareaDeUsuario = new TareaDeUsuario();
        tareaDeUsuario.setUsuario(usuario);
        tareaDeUsuario.setTarea(tarea);
        tareaDeUsuario.setAdmin(false);
        this.tareaDeUsuarioService.save(tareaDeUsuario);
        return ResponseEntity.ok(new MessageResponse("Dueño agregado"));
    }

    @PostMapping(path = "/tasks/addSubTask/{id}")
    public ResponseEntity<MessageResponse> addSubTask(@PathVariable Integer id, @RequestBody RequestTarea subTask) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!this.tareaService.findById(id).isPresent()) {
            LOGGER.info("Tarea no encontrada");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: tarea no encontrada"));
        }
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(null);
        Tarea tarea = this.tareaService.findById(id).get();
        if (tarea.getNivel() > 2) {
            LOGGER.info("Profundidad invalida, nivel máximo debe ser igual a 3");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: profundidad invalida, nivel máximo debe ser igual a 3"));
        }
        if (tarea.getRutinaT() != null) {
            if (!tarea.getRutinaT().getUsuario().getUsuario().equals(usuario.getUsuario())) {
                LOGGER.info("La tarea no pertenece al usuario");
                return ResponseEntity.badRequest().body(new MessageResponse("Error: la tarea no pertenece al usuario"));
            }
        } else {
            Tarea parent = tarea;
            if (tarea.getNivel() == 2) {
                parent = tarea.getPadre();
            } else if (tarea.getNivel() == 3) {
                parent = tarea.getPadre().getPadre();
            }
            if (!this.tareaDeUsuarioService.findByUsuarioAndTarea(usuario, parent).isPresent()) {
                LOGGER.info("La tarea no pertenece al usuario");
                return ResponseEntity.badRequest().body(new MessageResponse("Error: la tarea no pertenece al usuario"));
            }
        }
        Tarea subtarea = new Tarea();
        subtarea.setDescripcion(subTask.getDescripcion());
        subtarea.setEstimacion(subTask.getEstimacion());
        subtarea.setEtiqueta(subTask.getEtiqueta());
        subtarea.setFechaFin(subTask.getFechaFin());
        subtarea.setHecha(subTask.getHecha());
        subtarea.setNivel(tarea.getNivel() + 1);
        subtarea.setNombre(subTask.getNombre());
        subtarea.setPrioridad(subTask.getPrioridad());
        subtarea.setRecordatorio(subTask.getRecordatorio());
        subtarea.setTiempoInvertido(0);
        subtarea.setPadre(tarea);
        tarea.addSubtarea(subtarea);
        this.tareaService.save(tarea);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Subtarea creada"));
    }

    @PutMapping(path = "/tasks/updateSubtask/{id}")
    public ResponseEntity<MessageResponse> updateSubtask(@PathVariable Integer id, @RequestBody RequestTarea requestTarea) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!this.tareaService.findById(id).isPresent()) {
            LOGGER.info("Tarea no encontrada");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: tarea no encontrada"));
        }
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(null);
        Tarea subtask = this.tareaService.findById(id).get();
        Tarea parent;
        if (subtask.getNivel() == 2) {
            parent = subtask.getPadre();
        } else {
            parent = subtask.getPadre().getPadre();
        }
        if (!this.tareaDeUsuarioService.findByUsuarioAndTarea(usuario, parent).isPresent()) {
            LOGGER.info("La tarea no pertenece al usuario");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: la tarea no pertenece al usuario"));
        }
        subtask.setNombre(requestTarea.getNombre());
        subtask.setDescripcion(requestTarea.getDescripcion());
        subtask.setPrioridad(requestTarea.getPrioridad());
        subtask.setEtiqueta(requestTarea.getEtiqueta());
        subtask.setEstimacion(requestTarea.getEstimacion());
        subtask.setFechaFin(requestTarea.getFechaFin());
        subtask.setHecha(requestTarea.getHecha());
        subtask.setRecordatorio(requestTarea.getRecordatorio());
        subtask.setTiempoInvertido(requestTarea.getTiempoInvertido());
        this.tareaService.save(subtask);
        return ResponseEntity.ok(new MessageResponse("Subtarea actualizada"));
    }

    @PutMapping(path = "/tasks/checkSubtask/{id}")
    public ResponseEntity<MessageResponse> checkSubtask(@PathVariable Integer id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!this.tareaService.findById(id).isPresent()) {
            LOGGER.info("Tarea no encontrada");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: tarea no encontrada"));
        }
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(null);
        Tarea subtask = this.tareaService.findById(id).get();
        Tarea parent;
        if (subtask.getNivel() == 2) {
            parent = subtask.getPadre();
        } else {
            parent = subtask.getPadre().getPadre();
        }
        if (!this.tareaDeUsuarioService.findByUsuarioAndTarea(usuario, parent).isPresent()) {
            LOGGER.info("La tarea no pertenece al usuario");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: la tarea no pertenece al usuario"));
        }
        subtask.setHecha(!subtask.getHecha());
        this.tareaService.save(subtask);
        return ResponseEntity.ok(new MessageResponse("Subtarea chequeada"));
    }

    @DeleteMapping(path = "/tasks/deleteSubtask/{id}")
    public ResponseEntity<MessageResponse> deleteSubtask(@PathVariable Integer id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!this.tareaService.findById(id).isPresent()) {
            LOGGER.info("Tarea no encontrada");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: tarea no encontrada"));
        }
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(null);
        Tarea subtask = this.tareaService.findById(id).get();
        Tarea parent;
        if (subtask.getNivel() == 2) {
            parent = subtask.getPadre();
        } else {
            parent = subtask.getPadre().getPadre();
        }
        if (!this.tareaDeUsuarioService.findByUsuarioAndTarea(usuario, parent).isPresent()) {
            LOGGER.info("La tarea no pertenece al usuario");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: la tarea no pertenece al usuario"));
        }
        parent.deleteSubtarea(subtask);
        subtask.setPadre(null);
        this.tareaService.save(subtask);
        this.tareaService.save(parent);
        this.tareaService.deleteById(subtask.getId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new MessageResponse("Subtarea eliminada"));
    }

    @PutMapping(path = "/tasks/increaseTime/{id}/{time}")
    public ResponseEntity<MessageResponse> increaseInvestedTime(@PathVariable Integer id, @PathVariable Integer time) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!this.tareaService.findById(id).isPresent()) {
            LOGGER.info("Tarea no encontrada");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: tarea no encontrada"));
        }
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(null);
        Tarea tarea = this.tareaService.findById(id).get();
        if (!this.tareaDeUsuarioService.findByUsuarioAndTarea(usuario, tarea).isPresent()) {
            LOGGER.info("La tarea no pertenece al usuario");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: la tarea no pertenece al usuario"));
        }
        Integer newTime = tarea.getTiempoInvertido() + time;
        tarea.setTiempoInvertido(newTime);
        this.tareaService.save(tarea);
        return ResponseEntity.ok(new MessageResponse("Tiempo agregado"));
    }

    @PostMapping(path = "/tasks/copyTask/{id}")
    public ResponseEntity<MessageResponse> copyTask(@PathVariable Integer id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!this.tareaService.findById(id).isPresent()) {
            LOGGER.info("Tarea no encontrada");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: tarea no encontrada"));
        }
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(null);
        Tarea tarea = this.tareaService.findById(id).get();
        if (tarea.getNivel() != 1) {
            LOGGER.info("La tarea especificada es una subtarea");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: la tarea especificada es una subtarea"));
        }
        if (this.tareaDeUsuarioService.findByUsuarioAndTarea(usuario, tarea).isPresent()) {
            LOGGER.info("El usuario ya tiene asignada la tarea");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: el usuario ya tiene asignada la tarea"));
        }
        Tarea copy = new Tarea();
        copy.setNombre(tarea.getNombre());
        copy.setDescripcion(tarea.getDescripcion());
        copy.setPrioridad(tarea.getPrioridad());
        copy.setEtiqueta(tarea.getEtiqueta());
        copy.setEstimacion(tarea.getEstimacion());
        copy.setTiempoInvertido(0);
        copy.setFechaFin(tarea.getFechaFin());
        copy.setNivel(tarea.getNivel());
        copy.setRecordatorio(tarea.getRecordatorio());
        this.tareaService.save(copy);
        if (tarea.getSubtareas() != null) {
            for (Tarea subtareaSN : tarea.getSubtareas()) {
                Tarea subtarea = new Tarea();
                subtarea.setNombre(subtareaSN.getNombre());
                subtarea.setDescripcion(subtareaSN.getDescripcion());
                subtarea.setPrioridad(subtareaSN.getPrioridad());
                subtarea.setEtiqueta(subtareaSN.getEtiqueta());
                subtarea.setEstimacion(subtareaSN.getEstimacion());
                subtarea.setTiempoInvertido(0);
                subtarea.setFechaFin(subtareaSN.getFechaFin());
                subtarea.setNivel(subtareaSN.getNivel());
                subtarea.setRecordatorio(subtareaSN.getRecordatorio());
                this.tareaService.save(subtarea);
                if (subtareaSN.getSubtareas() != null) {
                    for (Tarea subtareaTN : subtareaSN.getSubtareas()) {
                        Tarea subtarea1 = new Tarea();
                        subtarea1.setNombre(subtareaTN.getNombre());
                        subtarea1.setDescripcion(subtareaTN.getDescripcion());
                        subtarea1.setPrioridad(subtareaTN.getPrioridad());
                        subtarea1.setEtiqueta(subtareaTN.getEtiqueta());
                        subtarea1.setEstimacion(subtareaTN.getEstimacion());
                        subtarea1.setTiempoInvertido(0);
                        subtarea1.setFechaFin(subtareaTN.getFechaFin());
                        subtarea1.setNivel(subtareaTN.getNivel());
                        subtarea1.setRecordatorio(subtareaTN.getRecordatorio());
                        subtarea1.setPadre(subtarea);
                        subtarea.addSubtarea(subtarea1);
                        subtarea = this.tareaService.save(subtarea);
                    }
                }
                subtarea.setPadre(copy);
                copy.addSubtarea(subtarea);
            }
        }
        TareaDeUsuario tareaDeUsuario = new TareaDeUsuario();
        tareaDeUsuario.setUsuario(usuario);
        tareaDeUsuario.setTarea(this.tareaService.save(copy));
        tareaDeUsuario.setAdmin(true);
        this.tareaDeUsuarioService.save(tareaDeUsuario);
        return ResponseEntity.ok(new MessageResponse("Tarea copiada"));
    }

}
