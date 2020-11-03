package com.fertigApp.backend.controller;

import com.fertigApp.backend.model.Completada;
import com.fertigApp.backend.model.Rutina;
import com.fertigApp.backend.model.Tarea;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.payload.response.AbstractRecurrenteResponse;
import com.fertigApp.backend.payload.response.RecurrenteResponse;
import com.fertigApp.backend.payload.response.RutinaRepeticionesResponse;
import com.fertigApp.backend.requestModels.RequestRutina;
import com.fertigApp.backend.requestModels.RequestTarea;
import com.fertigApp.backend.services.CompletadaService;
import com.fertigApp.backend.services.RutinaService;
import com.fertigApp.backend.services.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
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

    private final CompletadaService completadaService;

    public RutinaController(RutinaService rutinaService, UsuarioService usuarioService, CompletadaService completadaService) {
        this.rutinaService = rutinaService;
        this.usuarioService = usuarioService;
        this.completadaService = completadaService;
    }

    // Método GET para obtener todas las entidades de tipo "Rutina" almacenadas en la DB.
    @GetMapping(path="/routines")
    public @ResponseBody
    Iterable<Rutina> getAllRutinas() {
        return this.rutinaService.findAll();
    }

    // Método GET para obtener todas las rutinas de un usuario específico.
//    @GetMapping(path="/routines/getRoutines")
//    public ResponseEntity<List<RutinaResponse>> getAllRutinasByUsuario() {
//        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        Optional<Usuario> optUsuario = usuarioService.findById(userDetails.getUsername());
//        List<Rutina> rutinas = (List<Rutina>) rutinaService.findByUsuario(optUsuario.orElse(null));
//        List<RutinaResponse> rutinaResponses = new ArrayList<>();
//        for(Rutina rutina : rutinas) {
//            List<Completada> completadas;
//            completadas = (List<Completada>) completadaService.findByRutina(rutina);
//            Completada ultimaCompletada = null;
//            if (!completadas.isEmpty())
//                ultimaCompletada = completadas.get(completadas.size() - 1);
//            rutinaResponses.add(new RutinaResponse(rutina,ultimaCompletada));
//        }
//        return ResponseEntity.ok().body(rutinaResponses);
//    }

    @GetMapping(path="/routines/getRoutines")
    public ResponseEntity<List<RecurrenteResponse>> getAllRutinasByUsuario() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<Usuario> optUsuario = usuarioService.findById(userDetails.getUsername());
        if(optUsuario.isEmpty())
            return ResponseEntity.badRequest().body(null);
        List<Rutina> rutinas = (List<Rutina>) rutinaService.findByUsuario(optUsuario.orElse(null));
        List<RecurrenteResponse> rutinaResponses = new ArrayList<>();
        for(Rutina rutina : rutinas) {
            rutinaResponses.add(new RecurrenteResponse(rutina));
        }
        return ResponseEntity.ok().body(rutinaResponses);
    }

    @GetMapping(path="/routines/getRoutinesAndRepetitions")
    public ResponseEntity<List<RutinaRepeticionesResponse>> getAllRutinasRepeticionesByUsuario() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<Usuario> optUsuario = usuarioService.findById(userDetails.getUsername());
        if(optUsuario.isEmpty())
            return ResponseEntity.badRequest().body(null);
        List<RutinaRepeticionesResponse> rutinas = new LinkedList<>();
        for(Rutina rutina : rutinaService.findByUsuario(optUsuario.get())){
            rutinas.add(new RutinaRepeticionesResponse(rutina,
                    (List<LocalDateTime>) completadaService.findFechasCompletadasByRutina(rutina),
                    completadaService.findMaxAjustadaCompletadasByRutina(rutina)));
        }
        return ResponseEntity.ok().body(rutinas);
    }

    // Método GET para obtener una rutina específica por medio de su ID.
    @GetMapping(path="/routines/getRoutine/{id}")
    public Rutina getRutina(@PathVariable Integer id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        Optional<Rutina> optRutina = rutinaService.findById(id);
        if(optRutina.isPresent()){
            if(optRutina.get().getUsuario().getUsuario().equals(username))
                return optRutina.get();
            LOGGER.info("Wrong user");
            return null;
        }
        LOGGER.info("Routine not found");
        return null;
    }

    // Método PUT para modificar un registro en la base de datos.
    @PutMapping(path="/routines/updateRoutine/{id}")
    public ResponseEntity<Rutina> replaceRutina(@PathVariable Integer id, @RequestBody RequestRutina routine) {
        Object principal =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        java.util.logging.Logger.getGlobal().log(Level.INFO,principal.toString());
        UserDetails userDetails = (UserDetails) principal;

        Optional<Rutina> optionalRutina = rutinaService.findById(id);
        Optional<Usuario> optionalUsuario = usuarioService.findByUsuario(userDetails.getUsername());
        if(optionalRutina.isPresent() && optionalUsuario.isPresent()){
            Rutina rutina = optionalRutina.get();
            rutina.setUsuario(optionalUsuario.get());
            rutina.setNombre(routine.getNombre());
            rutina.setDescripcion(routine.getDescripcion());
            rutina.setPrioridad(routine.getPrioridad());
            rutina.setEtiqueta(routine.getEtiqueta());
            rutina.setDuracion(routine.getDuracion());
            rutina.setFechaInicio(routine.getFechaInicio());
            rutina.setFechaFin(routine.getFechaFin());
            rutina.setRecurrencia(routine.getRecurrencia());
            rutina.setRecordatorio(routine.getRecordatorio());
            rutina.setFranjaInicio(routine.getFranjaInicio());
            rutina.setFranjaFin(routine.getFranjaFin());
            rutina.setCompletadas((List<Completada>) completadaService.findByRutina(rutina));
            this.rutinaService.save(rutina);
            LOGGER.info("Routine replaced");
            return ResponseEntity.ok().body(rutina);
        } else {
            LOGGER.info("Routine not found");
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Método POST para añadir un registro en la tabla "rutina" de la DB.
    @PostMapping(path="/routines/addRoutine")
    public @ResponseBody ResponseEntity<Void> addNewRutina(@RequestBody RequestRutina requestRutina) {
        Rutina rutina = new Rutina();
        Object principal =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        java.util.logging.Logger.getGlobal().log(Level.INFO,principal.toString());
        UserDetails userDetails = (UserDetails) principal;

        Optional<Usuario> optUsuario =usuarioService.findById(userDetails.getUsername());
        rutina.setUsuario(optUsuario.orElse(null));
        rutina.setNombre(requestRutina.getNombre());
        rutina.setDescripcion(requestRutina.getDescripcion());
        rutina.setPrioridad(requestRutina.getPrioridad());
        rutina.setEtiqueta(requestRutina.getEtiqueta());
        rutina.setDuracion(requestRutina.getDuracion());
        rutina.setRecurrencia(requestRutina.getRecurrencia());
        rutina.setRecordatorio(requestRutina.getRecordatorio());
        rutina.setFechaInicio(requestRutina.getFechaInicio());
        rutina.setFechaFin(requestRutina.getFechaFin());
        rutina.setFranjaInicio(requestRutina.getFranjaInicio());
        rutina.setFranjaFin(requestRutina.getFranjaFin());
        this.rutinaService.save(rutina);

        Completada completada = new Completada();
        completada.setRutina(rutina);
        completada.setFecha(
                AbstractRecurrenteResponse.findSiguiente(rutina.getFechaInicio(),
                        rutina.getFechaFin(),
                        rutina.getRecurrencia()));
        completada.setFechaAjustada(completada.getFecha());
        completada.setHecha(false);
        this.completadaService.save(completada);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping(path = "/routines/addSubtask/{id}")
    public ResponseEntity<Void> addSubtask(@PathVariable Integer id, @RequestBody RequestTarea requestTarea) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!this.rutinaService.findById(id).isPresent())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(null);
        Rutina rutina = this.rutinaService.findById(id).get();
        Tarea subtarea = new Tarea();
        subtarea.setDescripcion(requestTarea.getDescripcion());
        subtarea.setEstimacion(requestTarea.getEstimacion());
        subtarea.setEtiqueta(requestTarea.getEtiqueta());
        subtarea.setFechaFin(requestTarea.getFechaFin());
        subtarea.setHecha(requestTarea.getHecha());
        subtarea.setNivel(2);
        subtarea.setNombre(requestTarea.getNombre());
        subtarea.setPrioridad(requestTarea.getPrioridad());
        subtarea.setRecordatorio(requestTarea.getRecordatorio());
        subtarea.setTiempoInvertido(0);
        subtarea.setRutinaT(rutina);
        rutina.addSubtarea(subtarea);
        this.rutinaService.save(rutina);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //@PutMapping
    @PatchMapping(path="/routines/checkRoutine/{id}")
    public ResponseEntity<Void> checkRoutine(@PathVariable Integer id){
        Object principal =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        java.util.logging.Logger.getGlobal().log(Level.INFO,principal.toString());
        UserDetails userDetails = (UserDetails) principal;

        Optional<Rutina> optionalRutina = rutinaService.findById(id);
        Optional<Usuario> optionalUsuario = usuarioService.findByUsuario(userDetails.getUsername());
        if(optionalRutina.isPresent() && optionalUsuario.isPresent()){
            ArrayList<Completada>  completadas =  (ArrayList<Completada>) completadaService.findHechaByRutina(optionalRutina.get());
            Completada completada = (completadas.isEmpty()) ? null : completadas.get(0);
            completada.setFecha(LocalDateTime.now());
            completada.setFechaAjustada(
                    AbstractRecurrenteResponse.findAnterior(optionalRutina.get().getFechaInicio(),
                            optionalRutina.get().getFechaFin(),
                            optionalRutina.get().getRecurrencia(),
                            optionalRutina.get().getDuracion(),
                            optionalRutina.get().getFranjaInicio(),
                            optionalRutina.get().getFranjaFin())
            );
            completada.setHecha(true);
            this.completadaService.save(completada);
            Completada newCompletada = new Completada();
            newCompletada.setRutina(optionalRutina.get());
            newCompletada.setFecha(
                    AbstractRecurrenteResponse.findSiguiente(optionalRutina.get().getFechaInicio(),
                            optionalRutina.get().getFechaFin(),
                            optionalRutina.get().getRecurrencia(),
                            optionalRutina.get().getDuracion(),
                            optionalRutina.get().getFranjaInicio(),
                            optionalRutina.get().getFranjaFin())
            );
            newCompletada.setHecha(false);
            LOGGER.info("Routine repetition checked");
            return ResponseEntity.ok().body(null);
        } else {
            LOGGER.info("Routine not found");
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Método DELETE para borrar un registro en la tabla "rutina" de la DB.
    @DeleteMapping(path="/routines/deleteRoutine/{id}")
    public ResponseEntity<Void> deleteRutina(@PathVariable Integer id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Rutina> optRutina = this.rutinaService.findById(id);
        if (optRutina.isPresent() && optRutina.get().getUsuario().getUsuario().equals(userDetails.getUsername())){
            this.rutinaService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
