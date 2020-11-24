package com.fertigApp.backend.controller;

import com.fertigApp.backend.firebase.NotificationSystem;
import com.fertigApp.backend.model.Tarea;
import com.fertigApp.backend.model.TareaDeUsuario;
import com.fertigApp.backend.model.Tiempo;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.payload.response.MessageResponse;
import com.fertigApp.backend.payload.response.OwnerResponse;
import com.fertigApp.backend.requestModels.RequestTarea;
import com.fertigApp.backend.services.*;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
 * Clase responsable de manejar request de tipo GET, POST, PUT y DELETE para
 * la entidad "Tarea".
 * */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class TareaController {

    private static final String TAR_NO_PERTENECE = "La tarea no pertenece al usuario";
    private static final String TAR_NO_ENCONTRADA = "Tarea no encontrada";
    private static final String US_NO_ADMIN = "El usuario no es un administrador de la tarea";

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Tarea.class);

    // Repositorio responsable del manejo de la tabla "tarea" en la DB.
    private final TareaService tareaService;

    // Repositorio responsable del manejo de la tabla "usuario" en la DB.
    private final UsuarioService usuarioService;

    private final TareaDeUsuarioService tareaDeUsuarioService;

    private final TiempoService tiempoService;

    private final NotificationSystem notificationSystem;

    public TareaController(TareaService tareaService, UsuarioService usuarioService, TareaDeUsuarioService tareaDeUsuarioService, TiempoService tiempoService, NotificationSystem notificationSystem) {
        this.tareaService = tareaService;
        this.usuarioService = usuarioService;
        this.tareaDeUsuarioService = tareaDeUsuarioService;
        this.tiempoService = tiempoService;
        this.notificationSystem = notificationSystem;
    }

    // Método GET para obtener todas las entidades de tipo "Tarea" almacenadas en la DB.
    @GetMapping(path="/tasks")
    public @ResponseBody Iterable<Tarea> getAllTareas() {
        return this.tareaService.findAll();
    }

    @GetMapping(path="/task/tasks")
    public ResponseEntity<List<Tarea>> getAllTareasByUsuario() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(null);
        List<Tarea> tareas = (List<Tarea>) this.tareaDeUsuarioService.findTareasByUsuario(usuario);
        return ResponseEntity.ok(tareas);
    }

    // Método GET para obtener una entidad de tipo "tarea" por medio de su ID.
    @GetMapping(path="/task/{id}")
    public ResponseEntity<Tarea> getTarea(@PathVariable Integer id) {
        Optional<Tarea> optionalTarea = this.tareaService.findById(id);
        if (optionalTarea.isEmpty()) {
            LOGGER.info(TAR_NO_ENCONTRADA);
            return ResponseEntity.badRequest().body(null);
        }
        Tarea tarea = optionalTarea.get();
        return ResponseEntity.ok(tarea);
    }

    @PutMapping(path="/task/update/{id}")
    public ResponseEntity<Tarea> replaceTarea(@PathVariable Integer id, @RequestBody RequestTarea task) {
        Object principal =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        Optional<Tarea> optionalTarea = this.tareaService.findById(id);
        if (optionalTarea.isEmpty()) {
            LOGGER.info(TAR_NO_ENCONTRADA);
            return ResponseEntity.badRequest().body(null);
        }
        Optional<Usuario> optionalUsuario = usuarioService.findByUsuario(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(null);
        Tarea tarea = optionalTarea.get();
        if (this.tareaDeUsuarioService.findByUsuarioAndTarea(usuario, tarea).isEmpty()) {
            LOGGER.info(TAR_NO_PERTENECE);
            return ResponseEntity.badRequest().body(null);
        }
        tarea.setNombre(task.getNombre());
        tarea.setDescripcion(task.getDescripcion());
        tarea.setPrioridad(task.getPrioridad());
        tarea.setEtiqueta(task.getEtiqueta());
        tarea.setEstimacion(task.getEstimacion());
        if(task.getFechaFin() != null) tarea.setFechaFin(task.getFechaFin());
        tarea.setHecha(task.getHecha());
        tarea.setRecordatorio(task.getRecordatorio());
        this.tareaService.save(tarea);
        LOGGER.info("Tarea actualizada");
        this.notificationSystem.cancelScheduledTaskNotification(userDetails.getUsername(), tarea.getId());
        if (tarea.getFechaFin() != null && tarea.getRecordatorio() != null)
        this.notificationSystem.scheduleTaskNotification(userDetails.getUsername(), tarea.getId());
        return ResponseEntity.ok().body(tarea);
    }

    @PatchMapping(path="/task/check/{id}")
    public ResponseEntity<MessageResponse> checkTarea(@PathVariable Integer id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Tarea> optionalTarea = this.tareaService.findById(id);
        if (optionalTarea.isEmpty()) {
            LOGGER.info(TAR_NO_ENCONTRADA);
            return ResponseEntity.badRequest().body(new MessageResponse(TAR_NO_ENCONTRADA));
        }
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(new Usuario());
        Tarea tarea = optionalTarea.get();
        if (this.tareaDeUsuarioService.findByUsuarioAndTarea(usuario, tarea).isEmpty()) {
            LOGGER.info(TAR_NO_PERTENECE);
            return ResponseEntity.badRequest().body(new MessageResponse(TAR_NO_PERTENECE));
        }
        tarea.setHecha(!tarea.getHecha());
        this.tareaService.save(tarea);
        this.notificationSystem.cancelScheduledTaskNotification(userDetails.getUsername(), id);
        return ResponseEntity.ok().body(new MessageResponse("Tarea chequeada"));
    }

    // Método POST para agregar un registro en la tabla "tarea" de la DB.
    @PostMapping(path="/task/add")
    public @ResponseBody ResponseEntity<MessageResponse> addNewTarea(@RequestBody RequestTarea requestTarea) {
        Object principal =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(null);
        Tarea tarea = new Tarea();
        tarea.setDescripcion(requestTarea.getDescripcion());
        tarea.setEstimacion(requestTarea.getEstimacion());
        tarea.setEtiqueta(requestTarea.getEtiqueta());
        if(requestTarea.getFechaFin() != null) tarea.setFechaFin(requestTarea.getFechaFin());
        tarea.setHecha(requestTarea.getHecha());
        tarea.setNivel(1);
        tarea.setNombre(requestTarea.getNombre());
        tarea.setPrioridad(requestTarea.getPrioridad());
        tarea.setRecordatorio(requestTarea.getRecordatorio());
        TareaDeUsuario tareaDeUsuario = new TareaDeUsuario();
        tareaDeUsuario.setUsuario(usuario);
        Tarea savedTask = this.tareaService.save(tarea);
        tareaDeUsuario.setTarea(savedTask);
        tareaDeUsuario.setAdmin(true);
        this.tareaDeUsuarioService.save(tareaDeUsuario);
        if (tarea.getRecordatorio() != null && tarea.getFechaFin() != null) {
            this.notificationSystem.scheduleTaskNotification(userDetails.getUsername(), savedTask.getId());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Tarea creada"));
    }

    // Método DELETE para borrar un registro de la tabla "tarea" en la DB.
    @DeleteMapping(path="/task/delete/{id}")
    public ResponseEntity<MessageResponse> deleteTarea(@PathVariable Integer id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Tarea> optionalTarea = this.tareaService.findById(id);
        if (optionalTarea.isEmpty()) {
            LOGGER.info(TAR_NO_ENCONTRADA);
            return ResponseEntity.badRequest().body(new MessageResponse(TAR_NO_ENCONTRADA));
        }
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(new Usuario());
        Tarea tarea = optionalTarea.get();
        Optional<TareaDeUsuario> optionalTareaDeUsuario = this.tareaDeUsuarioService.findByUsuarioAndTarea(usuario, tarea);
        if (optionalTareaDeUsuario.isEmpty()) {
            LOGGER.info(TAR_NO_PERTENECE);
            return ResponseEntity.badRequest().body(new MessageResponse(TAR_NO_PERTENECE));
        }
        if (!optionalTareaDeUsuario.get().isAdmin()) {
            LOGGER.info(US_NO_ADMIN);
            return ResponseEntity.badRequest().body(new MessageResponse(US_NO_ADMIN));
        }
        List<Tiempo> tiempos = (List<Tiempo>) this.tiempoService.findAllByUsuarioAndTarea(usuario, tarea);
        for (Tiempo tiempo : tiempos) {
            this.tiempoService.deleteById(tiempo.getId());
        }
        this.tareaDeUsuarioService.deleteAllByTarea(tarea);
        this.tareaService.deleteById(tarea.getId());
        this.notificationSystem.cancelScheduledTaskNotification(userDetails.getUsername(), id);
        return ResponseEntity.ok(new MessageResponse("Tarea eliminada"));
    }

    // Método GET para obtener todos los colaboradores de una tarea
    @GetMapping(path = "/task/{id}/owners")
    public ResponseEntity<List<OwnerResponse>> getTaskOwners(@PathVariable Integer id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Tarea> optionalTarea = this.tareaService.findById(id);
        if (optionalTarea.isEmpty()) {
            LOGGER.info(TAR_NO_ENCONTRADA);
            return ResponseEntity.badRequest().body(null);
        }
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(new Usuario());
        Tarea tarea = optionalTarea.get();
        if (this.tareaDeUsuarioService.findByUsuarioAndTarea(usuario, tarea).isEmpty()) {
            LOGGER.info(TAR_NO_PERTENECE);
            return ResponseEntity.badRequest().body(null);
        }
        ArrayList<TareaDeUsuario> tareaDeUsuarios = (ArrayList<TareaDeUsuario>) this.tareaDeUsuarioService.findAllByTarea(tarea);
        ArrayList<OwnerResponse> owners = new ArrayList<>();
        for (TareaDeUsuario tareaDeUsuario : tareaDeUsuarios) {
            OwnerResponse ownerResponse = new OwnerResponse();
            ownerResponse.setUsername(tareaDeUsuario.getUsuario().getUsuario());
            ownerResponse.setName(tareaDeUsuario.getUsuario().getNombre());
            ownerResponse.setAdmin(tareaDeUsuario.isAdmin());
            owners.add(ownerResponse);
        }
        return ResponseEntity.ok(owners);
    }

    // Método POST para añadir un administrador
    @PostMapping(path = "/task/{id}/add-admin/{username}")
    public ResponseEntity<MessageResponse> addTaskAdmin(@PathVariable Integer id, @PathVariable String username) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Tarea> optionalTarea = this.tareaService.findById(id);
        if (optionalTarea.isEmpty()) {
            LOGGER.info(TAR_NO_ENCONTRADA);
            return ResponseEntity.badRequest().body(new MessageResponse(TAR_NO_ENCONTRADA));
        }
        Optional<Usuario> optional = this.usuarioService.findById(username);
        if (optional.isEmpty()) {
            LOGGER.info("Usuario no encontrado");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: usuario no encontrado"));
        }
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario admin = optionalUsuario.orElse(new Usuario());
        Usuario usuario = optional.get();
        Tarea tarea = optionalTarea.get();
        Optional<TareaDeUsuario> optionalTareaDeUsuario = this.tareaDeUsuarioService.findByUsuarioAndTarea(admin, tarea);
        Optional<TareaDeUsuario> optionalTareaDeUsuario1 = this.tareaDeUsuarioService.findByUsuarioAndTarea(usuario, tarea);
        if (optionalTareaDeUsuario.isEmpty()) {
            LOGGER.info(TAR_NO_PERTENECE);
            return ResponseEntity.badRequest().body(new MessageResponse(TAR_NO_PERTENECE));
        }
        if (optionalTareaDeUsuario1.isEmpty()) {
            LOGGER.info("El nuevo usuario no es un colaborador");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: el usuario no es un colaborador"));
        }
        if (!optionalTareaDeUsuario.get().isAdmin()) {
            LOGGER.info(US_NO_ADMIN);
            return ResponseEntity.badRequest().body(new MessageResponse(US_NO_ADMIN));
        }
        TareaDeUsuario tareaDeUsuario = optionalTareaDeUsuario1.get();
        tareaDeUsuario.setAdmin(true);
        this.tareaDeUsuarioService.save(tareaDeUsuario);
        return ResponseEntity.ok(new MessageResponse("Administrador agregado"));
    }

    @PatchMapping(path = "/task/{id}/remove-admin/{username}")
    public ResponseEntity<MessageResponse> removeTaskAdmin(@PathVariable Integer id, @PathVariable String username) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Usuario> optionalAdmin = this.usuarioService.findById(userDetails.getUsername());
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(username);
        Optional<Tarea> optionalTarea = this.tareaService.findById(id);
        if (optionalUsuario.isEmpty()) {
            LOGGER.info("El usuario no existe");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: el usuario especificado no existe"));
        }
        if (optionalTarea.isEmpty()) {
            LOGGER.info(TAR_NO_ENCONTRADA);
            return ResponseEntity.badRequest().body(new MessageResponse(TAR_NO_ENCONTRADA));
        }
        if (username.equals(userDetails.getUsername())) {
            LOGGER.info("Un usuario no puede quitarse asi mismo privilegios de administrador");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: un usuario no puede quitarse asi mismo privilegios de administrador"));
        }
        Usuario admin = optionalAdmin.orElse(new Usuario());
        Usuario usuario = optionalUsuario.get();
        Tarea tarea = optionalTarea.get();
        Optional<TareaDeUsuario> optionalTareaAdmin = this.tareaDeUsuarioService.findByUsuarioAndTarea(admin, tarea);
        Optional<TareaDeUsuario> optionalTareaUsuario = this.tareaDeUsuarioService.findByUsuarioAndTarea(usuario, tarea);
        if (optionalTareaAdmin.isEmpty()) {
            LOGGER.info(TAR_NO_PERTENECE);
            return ResponseEntity.badRequest().body(new MessageResponse(TAR_NO_PERTENECE));
        }
        if (optionalTareaUsuario.isEmpty()) {
            LOGGER.info("El usuario no es un colaborador");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: el usuario no es un colaborador"));
        }
        if (!optionalTareaAdmin.get().isAdmin()) {
            LOGGER.info(US_NO_ADMIN);
            return ResponseEntity.badRequest().body(new MessageResponse(US_NO_ADMIN));
        }
        if (!optionalTareaUsuario.get().isAdmin()) {
            LOGGER.info("El usuario especificado no es administrador");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: el usuario especificado no es administrador"));
        }
        TareaDeUsuario tareaDeUsuario = optionalTareaUsuario.get();
        tareaDeUsuario.setAdmin(false);
        this.tareaDeUsuarioService.save(tareaDeUsuario);
        return ResponseEntity.ok(new MessageResponse("El usuario ya no es un administrador"));
    }

    // Método POST para añadir un colaborador a una tarea
    @PostMapping(path = "/task/{id}/add-owner/{username}")
    public ResponseEntity<MessageResponse> addTaskOwner(@PathVariable Integer id, @PathVariable String username) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Usuario> optional = this.usuarioService.findById(username);
        if (optional.isEmpty()) {
            LOGGER.info("Usuario no encontrado");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: usuario no encontrado"));
        }
        Optional<Tarea> optionalTarea = this.tareaService.findById(id);
        if (optionalTarea.isEmpty()) {
            LOGGER.info(TAR_NO_ENCONTRADA);
            return ResponseEntity.badRequest().body(new MessageResponse(TAR_NO_ENCONTRADA));
        }
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optional.get();
        Usuario admin = optionalUsuario.orElse(new Usuario());
        Tarea tarea = optionalTarea.get();
        Optional<TareaDeUsuario> optionalTareaDeUsuario = this.tareaDeUsuarioService.findByUsuarioAndTarea(admin, tarea);
        if (optionalTareaDeUsuario.isEmpty()) {
            LOGGER.info(TAR_NO_PERTENECE);
            return ResponseEntity.badRequest().body(new MessageResponse(TAR_NO_PERTENECE));
        }
        if (!optionalTareaDeUsuario.get().isAdmin()) {
            LOGGER.info(US_NO_ADMIN);
            return ResponseEntity.badRequest().body(new MessageResponse(US_NO_ADMIN));
        }
        TareaDeUsuario tareaDeUsuario = new TareaDeUsuario();
        tareaDeUsuario.setUsuario(usuario);
        tareaDeUsuario.setTarea(tarea);
        tareaDeUsuario.setAdmin(false);
        this.tareaDeUsuarioService.save(tareaDeUsuario);
        return ResponseEntity.ok(new MessageResponse("Dueño agregado"));
    }

    @DeleteMapping(path = "/task/{id}/delete-owner/{username}")
    public ResponseEntity<MessageResponse> deleteTaskOwner(@PathVariable Integer id, @PathVariable String username) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(username);
        Optional<Tarea> optionalTarea = this.tareaService.findById(id);
        if (optionalTarea.isEmpty()) {
            LOGGER.info(TAR_NO_ENCONTRADA);
            return ResponseEntity.badRequest().body(new MessageResponse(TAR_NO_ENCONTRADA));
        }
        if (optionalUsuario.isEmpty()) {
            LOGGER.info("Usuario no encontrado");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: usuario no encontrado"));
        }
        if (username.equals(userDetails.getUsername())) {
            LOGGER.info("Un usuario no puede eliminarse asi mismo");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: un usuario no puede eliminarse asi mismo"));
        }
        Optional<Usuario> optionalAdmin = this.usuarioService.findById(userDetails.getUsername());
        Usuario admin = optionalAdmin.orElse(new Usuario());
        Usuario usuario = optionalUsuario.get();
        Tarea tarea = optionalTarea.get();
        Optional<TareaDeUsuario> optionalTareaAdmin = this.tareaDeUsuarioService.findByUsuarioAndTarea(admin, tarea);
        if (optionalTareaAdmin.isEmpty()) {
            LOGGER.info(TAR_NO_PERTENECE);
            return ResponseEntity.badRequest().body(new MessageResponse(TAR_NO_PERTENECE));
        }
        if (!optionalTareaAdmin.get().isAdmin()) {
            LOGGER.info(US_NO_ADMIN);
            return ResponseEntity.badRequest().body(new MessageResponse(US_NO_ADMIN));
        }
        Optional<TareaDeUsuario> optionalTareaUsuario = this.tareaDeUsuarioService.findByUsuarioAndTarea(usuario, tarea);
        if (optionalTareaUsuario.isEmpty()) {
            LOGGER.info("El usuario no es un colaborador");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: el usuario no es un colaborador"));
        }
        TareaDeUsuario tareaDeUsuario = optionalTareaUsuario.get();
        this.tareaDeUsuarioService.deleteById(tareaDeUsuario.getId());
        return ResponseEntity.ok(new MessageResponse("Dueño eliminado"));
    }

    @PostMapping(path = "/task/{id}/add-subtask")
    public ResponseEntity<MessageResponse> addSubtask(@PathVariable Integer id, @RequestBody RequestTarea subTask) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Tarea> optionalTarea = this.tareaService.findById(id);
        if (optionalTarea.isEmpty()) {
            LOGGER.info(TAR_NO_ENCONTRADA);
            return ResponseEntity.badRequest().body(new MessageResponse(TAR_NO_ENCONTRADA));
        }
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(new Usuario());
        Tarea tarea = optionalTarea.get();
        if (tarea.getNivel() > 2) {
            LOGGER.info("Profundidad invalida, nivel máximo debe ser igual a 3");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: profundidad invalida, nivel máximo debe ser igual a 3"));
        }
        if (tarea.getRutinaT() != null) {
            if (!tarea.getRutinaT().getUsuario().getUsuario().equals(usuario.getUsuario())) {
                LOGGER.info(TAR_NO_PERTENECE);
                return ResponseEntity.badRequest().body(new MessageResponse(TAR_NO_PERTENECE));
            }
        } else {
            Tarea parent = tarea;
            if (tarea.getNivel() == 2) {
                parent = tarea.getPadre();
            } else if (tarea.getNivel() == 3) {
                parent = tarea.getPadre().getPadre();
            }
            if (this.tareaDeUsuarioService.findByUsuarioAndTarea(usuario, parent).isEmpty()) {
                LOGGER.info(TAR_NO_PERTENECE);
                return ResponseEntity.badRequest().body(new MessageResponse(TAR_NO_PERTENECE));
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
        subtarea.setPadre(tarea);
        tarea.addSubtarea(subtarea);
        this.tareaService.save(tarea);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Subtarea creada"));
    }

    @PatchMapping(path = "/task/{id}/check-subtask")
    public ResponseEntity<MessageResponse> checkSubtask(@PathVariable Integer id) {
        Optional<Tarea> optionalTarea = this.tareaService.findById(id);
        if (optionalTarea.isEmpty()) {
            LOGGER.info(TAR_NO_ENCONTRADA);
            return ResponseEntity.badRequest().body(new MessageResponse(TAR_NO_ENCONTRADA));
        }
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Tarea subtask = optionalTarea.get();
        Tarea parent;
        Usuario usuario = optionalUsuario.orElse(null);
        if (subtask.getNivel() == 2) {
            parent = subtask.getPadre();
        } else {
            parent = subtask.getPadre().getPadre();
        }
        if (this.tareaDeUsuarioService.findByUsuarioAndTarea(usuario, parent).isEmpty()) {
            LOGGER.info(TAR_NO_PERTENECE);
            return ResponseEntity.badRequest().body(new MessageResponse(TAR_NO_PERTENECE));
        }
        subtask.setHecha(!subtask.getHecha());
        this.tareaService.save(subtask);
        return ResponseEntity.ok(new MessageResponse("Subtarea chequeada"));
    }

    @PutMapping(path = "/task/{id}/update-subtask")
    public ResponseEntity<MessageResponse> updateSubtask(@PathVariable Integer id, @RequestBody RequestTarea requestTarea) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Tarea> optionalTarea = this.tareaService.findById(id);
        if (optionalTarea.isEmpty()) {
            LOGGER.info(TAR_NO_ENCONTRADA);
            return ResponseEntity.badRequest().body(new MessageResponse(TAR_NO_ENCONTRADA));
        }
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Tarea subtask = optionalTarea.get();
        Tarea parent;
        if (subtask.getNivel() == 2) {
            parent = subtask.getPadre();
        } else {
            parent = subtask.getPadre().getPadre();
        }
        Usuario usuario = optionalUsuario.orElse(new Usuario());
        if (this.tareaDeUsuarioService.findByUsuarioAndTarea(usuario, parent).isEmpty()) {
            LOGGER.info(TAR_NO_PERTENECE);
            return ResponseEntity.badRequest().body(new MessageResponse(TAR_NO_PERTENECE));
        }
        subtask.setNombre(requestTarea.getNombre());
        subtask.setPrioridad(requestTarea.getPrioridad());
        subtask.setDescripcion(requestTarea.getDescripcion());
        subtask.setFechaFin(requestTarea.getFechaFin());
        subtask.setEtiqueta(requestTarea.getEtiqueta());
        subtask.setEstimacion(requestTarea.getEstimacion());
        subtask.setHecha(requestTarea.getHecha());
        subtask.setRecordatorio(requestTarea.getRecordatorio());
        this.tareaService.save(subtask);
        return ResponseEntity.ok(new MessageResponse("Subtarea actualizada"));
    }

    @DeleteMapping(path = "/task/{id}/delete-subtask")
    public ResponseEntity<MessageResponse> deleteSubtask(@PathVariable Integer id) {
        Optional<Tarea> optionalTarea = this.tareaService.findById(id);
        if (optionalTarea.isEmpty()) {
            LOGGER.info(TAR_NO_ENCONTRADA);
            return ResponseEntity.badRequest().body(new MessageResponse(TAR_NO_ENCONTRADA));
        }
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(null);
        Tarea subtask = optionalTarea.get();
        Tarea parent;
        if (subtask.getNivel() == 2) {
            parent = subtask.getPadre();
        } else {
            parent = subtask.getPadre().getPadre();
        }
        if (this.tareaDeUsuarioService.findByUsuarioAndTarea(usuario, parent).isEmpty()) {
            LOGGER.info(TAR_NO_PERTENECE);
            return ResponseEntity.badRequest().body(new MessageResponse(TAR_NO_PERTENECE));
        }
        parent.deleteSubtarea(subtask);
        subtask.setPadre(null);
        this.tareaService.save(subtask);
        this.tareaService.save(parent);
        this.tareaService.deleteById(subtask.getId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new MessageResponse("Subtarea eliminada"));
    }

    @PutMapping(path = "/task/{id}/increase-time/{time}")
    public ResponseEntity<MessageResponse> increaseInvestedTime(@PathVariable Integer id, @PathVariable Integer time) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Tarea> optionalTarea = this.tareaService.findById(id);
        if (optionalTarea.isEmpty()) {
            LOGGER.info(TAR_NO_ENCONTRADA);
            return ResponseEntity.badRequest().body(new MessageResponse(TAR_NO_ENCONTRADA));
        }
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(null);
        Tarea tarea = optionalTarea.get();
        Tarea parent = tarea;
        if (tarea.getNivel() == 2) {
            parent = tarea.getPadre();
        } else if (tarea.getNivel() == 3) {
            parent = tarea.getPadre().getPadre();
        }
        Optional<TareaDeUsuario> optionalTareaDeUsuario = this.tareaDeUsuarioService.findByUsuarioAndTarea(usuario, parent);
        if (optionalTareaDeUsuario.isEmpty()) {
            LOGGER.info(TAR_NO_PERTENECE);
            return ResponseEntity.badRequest().body(new MessageResponse(TAR_NO_PERTENECE));
        }
        Tiempo tiempo = new Tiempo();
        tiempo.setFecha(OffsetDateTime.now().minusMinutes(time));
        tiempo.setInvertido(time);
        tiempo.setTareaDeUsuario(optionalTareaDeUsuario.get());
        this.tiempoService.save(tiempo);
        return ResponseEntity.ok(new MessageResponse("Tiempo agregado"));
    }

    @PostMapping(path = "/task/{id}/copy")
    public ResponseEntity<MessageResponse> copyTask(@PathVariable Integer id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Tarea> optionalTarea = this.tareaService.findById(id);
        if (optionalTarea.isEmpty()) {
            LOGGER.info(TAR_NO_ENCONTRADA);
            return ResponseEntity.badRequest().body(new MessageResponse(TAR_NO_ENCONTRADA));
        }
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(new Usuario());
        Tarea tarea = optionalTarea.get();
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
        Tarea savedTask = this.tareaService.save(copy);
        tareaDeUsuario.setTarea(savedTask);
        tareaDeUsuario.setAdmin(true);
        this.tareaDeUsuarioService.save(tareaDeUsuario);
        if (savedTask.getRecordatorio() != null && savedTask.getFechaFin() != null) {
            this.notificationSystem.scheduleTaskNotification(userDetails.getUsername(), savedTask.getId());
        }
        return ResponseEntity.ok(new MessageResponse("Tarea copiada"));
    }

}
