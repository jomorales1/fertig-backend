package com.fertigApp.backend.controller;

import com.fertigApp.backend.model.Completada;
import com.fertigApp.backend.model.Rutina;
import com.fertigApp.backend.repository.RutinaRepository;
import com.fertigApp.backend.repository.UsuarioRepository;
import com.fertigApp.backend.requestModels.RequestRutina;
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
    private final RutinaRepository rutinaRepository;

    // Repositorio responsable del manejo de la tabla "usuario" en la DB.
    private final UsuarioRepository usuarioRepository;

    public RutinaController(RutinaRepository rutinaRepository, UsuarioRepository usuarioRepository) {
        this.rutinaRepository = rutinaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Método GET para obtener todas las entidades de tipo "Rutina" almacenadas en la DB.
    @GetMapping(path="/routines")
    public @ResponseBody
    Iterable<Rutina> getAllRutinas() {
        return this.rutinaRepository.findAll();
    }

    // Método GET para obtener todas las rutinas de un usuario específico.
    @GetMapping(path="/routines/getRoutines")
    public Iterable<Rutina> getAllRutinasByUsuario() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(usuarioRepository.findById(userDetails.getUsername()).isPresent()){
            return usuarioRepository.findById(userDetails.getUsername()).get().getRutinas();
        }
        LOGGER.info("User not found");
        return null;

    }

    // Método GET para obtener una rutina específica por medio de su ID.
    @GetMapping(path="/routines/getRoutine/{id}")
    public Rutina getRutina(@PathVariable Integer id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String user = userDetails.getUsername();

        if(rutinaRepository.findById(id).isPresent()){
            if(rutinaRepository.findById(id).get().getUsuario().getUsuario().equals(user))
                return rutinaRepository.findById(id).get();
            LOGGER.info("Wrong user");
            return null;
        }
        LOGGER.info("Routine not found");
        return null;

    }

    // Método PUT para modificar un registro en la base de datos.
    @PutMapping(path="/routines/updateRoutine/{id}")
    public Rutina replaceRutina(@PathVariable Integer id, @RequestBody RequestRutina routine) {
        Object principal =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        java.util.logging.Logger.getGlobal().log(Level.INFO,principal.toString());
        UserDetails userDetails = (UserDetails) principal;
        return this.rutinaRepository.findById(id)
                .map(rutina -> {
                    if(usuarioRepository.findByUsuario(userDetails.getUsername()).isEmpty()){
                        LOGGER.info("User not found");
                        return null;
                    }
                    rutina.setUsuario(usuarioRepository.findByUsuario(userDetails.getUsername()).get());
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
                    this.rutinaRepository.save(rutina);
                    LOGGER.info("Routine replaced");
                    return rutina;
                })
                .orElseGet(() -> {
                    LOGGER.info("Routine not found");
                    return null;
                });
    }

    // Método POST para añadir un registro en la tabla "rutina" de la DB.
    @PostMapping(path="/routines/addRoutine")
    public @ResponseBody
    ResponseEntity<Void> addNewRutina(@RequestBody RequestRutina requestRutina) {
        Rutina rutina = new Rutina();
        Object principal =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        java.util.logging.Logger.getGlobal().log(Level.INFO,principal.toString());
        UserDetails userDetails = (UserDetails) principal;
        if(usuarioRepository.findById(userDetails.getUsername()).isPresent()) {
            rutina.setUsuario(usuarioRepository.findById(userDetails.getUsername()).get());
            rutina.setNombre(requestRutina.getNombre());
            rutina.setDescripcion(requestRutina.getDescripcion());
            rutina.setPrioridad(requestRutina.getPrioridad());
            rutina.setEtiqueta(requestRutina.getEtiqueta());
            if (requestRutina.getEstimacion() != null)
                rutina.setEstimacion(requestRutina.getEstimacion());
            rutina.setRecurrencia(requestRutina.getRecurrencia());
            if (requestRutina.getRecordatorio() != null)
                rutina.setRecordatorio(requestRutina.getRecordatorio());
            this.rutinaRepository.save(rutina);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // Método DELETE para borrar un registro en la tabla "rutina" de la DB.
    @DeleteMapping(path="/routines/deleteRoutine/{id}")
    public ResponseEntity<Void> deleteRutina(@PathVariable Integer id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (this.rutinaRepository.findById(id).isPresent() && this.rutinaRepository.findById(id).get().getUsuario().getUsuario().equals(userDetails.getUsername())){
            this.rutinaRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
