package com.fertigApp.backend.controller;

import com.fertigApp.backend.model.Rutina;
import com.fertigApp.backend.repository.RutinaRepository;
import com.fertigApp.backend.repository.UsuarioRepository;
import com.fertigApp.backend.requestModels.RequestRutina;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/*
 * Clase responsable de manejar request de tipo GET, POST, PUT y DELETE para
 * la entidad "Rutina".
 * */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class RutinaController {

    // Repositorio responsable del manejo de la tabla "rutina" en la DB.
    @Autowired
    private RutinaRepository rutinaRepository;

    // Repositorio responsable del manejo de la tabla "usuario" en la DB.
    @Autowired
    private UsuarioRepository usuarioRepository;

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
        try {
            return usuarioRepository.findById(userDetails.getUsername()).get().getRutinas();
        } catch (java.util.NoSuchElementException ex) {
            System.out.println("User not found");
            return null;
        }
    }

    // Método GET para obtener una rutina específica por medio de su ID.
    @GetMapping(path="/routines/getRoutine/{id}")
    public Rutina getRutina(@PathVariable Integer id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String user = userDetails.getUsername();
        try {
            Rutina evento = this.rutinaRepository.findById(id).get();
            if (evento.getUsuario().getUsuario() != user) {
                System.out.println("Wrong user");
                return null;
            }
            return this.rutinaRepository.findById(id).get();
        } catch (java.util.NoSuchElementException ex) {
            System.out.println("Routine not found");
            return null;
        }
    }

    // Método PUT para modificar un registro en la base de datos.
    @PutMapping(path="/routines/updateRoutine/{id}")
    public Rutina replaceRutina(@PathVariable Integer id, @RequestBody Rutina routine) {
        return this.rutinaRepository.findById(id)
                .map(rutina -> {
                    rutina = routine;
                    this.rutinaRepository.save(rutina);
                    return rutina;
                })
                .orElseGet(() -> {
                    this.rutinaRepository.save(routine);
                    return routine;
                });
    }

    // Método POST para añadir un registro en la tabla "rutina" de la DB.
    @PostMapping(path="/routines/addRoutine")
    public @ResponseBody
    ResponseEntity<Void> addNewRutina(@RequestBody RequestRutina requestRutina) {
        // Missing check information process
        Rutina rutina = new Rutina();
        if (usuarioRepository.findById(requestRutina.getUsuario()).isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        rutina.setUsuario(usuarioRepository.findById(requestRutina.getUsuario()).get());
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

    // Método DELETE para borrar un registro en la tabla "rutina" de la DB.
    @DeleteMapping(path="/routines/deleteRoutine/{id}")
    public ResponseEntity<Void> deleteRutina(@PathVariable Integer id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Rutina rutina = this.rutinaRepository.findById(id).get();
        if (rutina.getUsuario().getUsuario() != userDetails.getUsername())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        this.rutinaRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
