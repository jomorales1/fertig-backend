package com.fertigApp.backend.controller;

import com.fertigApp.backend.model.Completada;
import com.fertigApp.backend.model.Rutina;
import com.fertigApp.backend.requestModels.RequestRutina;
import com.fertigApp.backend.services.RutinaService;
import com.fertigApp.backend.services.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Level;

/*
 * Clase responsable de manejar request de tipo GET, POST, PUT y DELETE para
 * la entidad "Rutina".
 * */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class RutinaController {

    private static final Logger LOGGER= LoggerFactory.getLogger(Completada.class);

    // Repositorio responsable del manejo de la tabla "rutina" en la DB.
    private final RutinaService rutinaService;

    // Repositorio responsable del manejo de la tabla "usuario" en la DB.
    private final UsuarioService usuarioService;

    public RutinaController(RutinaService rutinaService, UsuarioService usuarioService) {
        this.rutinaService = rutinaService;
        this.usuarioService = usuarioService;
    }

    // Método GET para obtener todas las entidades de tipo "Rutina" almacenadas en la DB.
    @GetMapping(path="/routines")
    public @ResponseBody
    Iterable<Rutina> getAllRutinas() {
        return this.rutinaService.findAll();
    }

    // Método GET para obtener todas las rutinas de un usuario específico.
    @GetMapping(path="/routines/getRoutines")
    public Iterable<Rutina> getAllRutinasByUsuario() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(usuarioService.findById(userDetails.getUsername()).isPresent()){
            return usuarioService.findById(userDetails.getUsername()).get().getRutinas();
        }
        LOGGER.info("User not found");
        return null;

    }

    // Método GET para obtener una rutina específica por medio de su ID.
    @GetMapping(path="/routines/getRoutine/{id}")
    public Rutina getRutina(@PathVariable Integer id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String user = userDetails.getUsername();

        if(rutinaService.findById(id).isPresent()){
            if(rutinaService.findById(id).get().getUsuario().getUsuario().equals(user))
                return rutinaService.findById(id).get();
            LOGGER.info("Wrong user");
            return null;
        }
        LOGGER.info("Routine not found");
        return null;

    }

    // Método PUT para modificar un registro en la base de datos.
    @PutMapping(path="/routines/updateRoutine/{id}")
    public ResponseEntity<?> replaceRutina(@PathVariable Integer id, @RequestBody RequestRutina routine) {
        Object principal =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        java.util.logging.Logger.getGlobal().log(Level.INFO,principal.toString());
        UserDetails userDetails = (UserDetails) principal;
        return this.rutinaService.findById(id)
                .map(rutina -> {
                    if(usuarioService.findByUsuario(userDetails.getUsername()).isEmpty()){
                        LOGGER.info("User not found");
                        return ResponseEntity.badRequest().body(null);
                    }
                    rutina.setUsuario(usuarioService.findByUsuario(userDetails.getUsername()).get());
                    rutina.setNombre(routine.getNombre());
                    rutina.setDescripcion(routine.getDescripcion());
                    rutina.setPrioridad(routine.getPrioridad());
                    rutina.setEtiqueta(routine.getEtiqueta());
                    rutina.setEstimacion(routine.getEstimacion());
                    rutina.setFechaInicio(routine.getFechaInicio());
                    rutina.setFechaFin(routine.getFechaFin());
                    rutina.setRecurrencia(routine.getRecurrencia());
                    rutina.setRecordatorio(routine.getRecordatorio());
                    rutina.setCompletadas(routine.getCompletadas());
                    this.rutinaService.save(rutina);
                    LOGGER.info("Routine replaced");
                    return ResponseEntity.ok().body(rutina);
                })
                .orElseGet(() -> {
                    LOGGER.info("Routine not found");
                    return ResponseEntity.badRequest().body(null);
                });
    }

    // Método POST para añadir un registro en la tabla "rutina" de la DB.
    @PostMapping(path="/routines/addRoutine")
    public @ResponseBody ResponseEntity<Void> addNewRutina(@RequestBody RequestRutina requestRutina) {
        Rutina rutina = new Rutina();
        Object principal =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        java.util.logging.Logger.getGlobal().log(Level.INFO,principal.toString());
        UserDetails userDetails = (UserDetails) principal;
        if(usuarioService.findById(userDetails.getUsername()).isPresent()) {
            rutina.setUsuario(usuarioService.findById(userDetails.getUsername()).get());
            rutina.setNombre(requestRutina.getNombre());
            rutina.setDescripcion(requestRutina.getDescripcion());
            rutina.setPrioridad(requestRutina.getPrioridad());
            rutina.setEtiqueta(requestRutina.getEtiqueta());
            if (requestRutina.getEstimacion() != null)
                rutina.setEstimacion(requestRutina.getEstimacion());
            rutina.setRecurrencia(requestRutina.getRecurrencia());
            if (requestRutina.getRecordatorio() != null)
                rutina.setRecordatorio(requestRutina.getRecordatorio());
            this.rutinaService.save(rutina);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // Método DELETE para borrar un registro en la tabla "rutina" de la DB.
    @DeleteMapping(path="/routines/deleteRoutine/{id}")
    public ResponseEntity<Void> deleteRutina(@PathVariable Integer id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (this.rutinaService.findById(id).isPresent() && this.rutinaService.findById(id).get().getUsuario().getUsuario().equals(userDetails.getUsername())){
            this.rutinaService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
