package com.fertigApp.backend.controller;

import com.fertigApp.backend.model.FranjaActiva;
import com.fertigApp.backend.model.Tarea;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.payload.response.MessageResponse;
import com.fertigApp.backend.payload.response.TareaSugeridaResponse;
import com.fertigApp.backend.requestModels.FranjaActivaRequest;
import com.fertigApp.backend.services.FranjaActivaService;
import com.fertigApp.backend.services.TareaDeUsuarioService;
import com.fertigApp.backend.services.TareaService;
import com.fertigApp.backend.services.UsuarioService;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetTime;
import java.util.*;

/*
 * Clase responsable de manejar request de tipo GET, POST, PUT y DELETE para
 * la entidad "FranjaLibre".
 * */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class FranjaActivaController {

    private  static final String FA_IS_PRESENT = "La franja ya existe";
    private static final String FA_IS_NOT_PRESENT = "Franja no encontrada";
    private static final String FA_DOES_NOT_BELONG = "El usuario no es dueño de la franja";

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(FranjaActiva.class);

    // Servicio responsable del manejo de la tabla "franja activa" en la DB
    private final FranjaActivaService franjaActivaService;

    // Servicio responsable del manejo de la tabla "tarea" en la DB.
    private final TareaService tareaService;

    // Servicio responsable del manejo de la tabla "usuario" en la DB.
    private final UsuarioService usuarioService;

    // Servicio responsable del manejo de la tabla "Tarea" en la DB.
    private final TareaDeUsuarioService tareaDeUsuarioService;

    public FranjaActivaController(TareaService tareaService, UsuarioService usuarioService, TareaDeUsuarioService tareaDeUsuarioService, FranjaActivaService franjaActivaService) {
        this.tareaService = tareaService;
        this.usuarioService = usuarioService;
        this.tareaDeUsuarioService = tareaDeUsuarioService;
        this.franjaActivaService = franjaActivaService;
    }

    @PostMapping(path="/franja-activa/add")
    public ResponseEntity<MessageResponse> addFranjaActiva(@RequestBody FranjaActivaRequest franjaActivaRequest){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(new Usuario());

        Optional<FranjaActiva> optionalFranjaActiva = this.franjaActivaService.findByUserAndDay(usuario, franjaActivaRequest.getDay());
        if(optionalFranjaActiva.isPresent()){
            LOGGER.info(FA_IS_PRESENT);
            return ResponseEntity.badRequest().body(new MessageResponse(FA_IS_PRESENT));
        }

        FranjaActiva franjaActiva = new FranjaActiva();
        franjaActiva.setDay(franjaActivaRequest.getDay());
        franjaActiva.setFranjaFin(franjaActivaRequest.getFranjaFin());
        franjaActiva.setFranjaInicio(franjaActivaRequest.getFranjaInicio());
        franjaActiva.setUsuarioFL(usuario);
        this.franjaActivaService.save(franjaActiva);
        return ResponseEntity.ok(new MessageResponse("Franja libre agregada"));
    }

    @GetMapping(path="/franja-activa/franjas")
    public ResponseEntity<List<FranjaActiva>> getAllFranjasActivasByUsuario() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(new Usuario());
        List<FranjaActiva> franjaActivas = (List<FranjaActiva>) this.franjaActivaService.findByUser(usuario);
        return ResponseEntity.ok(franjaActivas);
    }

    @PutMapping(path="/franja-activa/update/{id}")
    public ResponseEntity<MessageResponse> updateFranja(@PathVariable int id, @RequestBody FranjaActivaRequest franjaActivaRequest){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<FranjaActiva> optionalFranjaActiva = franjaActivaService.findById(id);
        String response = verify(optionalFranjaActiva, userDetails.getUsername());
        if(response != null){
            return ResponseEntity.badRequest().body(new MessageResponse(response));
        }

        FranjaActiva franjaActiva = optionalFranjaActiva.get();
        franjaActiva.setDay(franjaActivaRequest.getDay());
        franjaActiva.setFranjaInicio(franjaActivaRequest.getFranjaInicio());
        franjaActiva.setFranjaFin(franjaActivaRequest.getFranjaFin());

        franjaActivaService.save(franjaActiva);
        return ResponseEntity.ok().body(new MessageResponse("Franja actualizada"));
    }

    @DeleteMapping(path="/franja-activa/delete/{id}")
    public ResponseEntity<MessageResponse> deleteFranjaById(@PathVariable int id){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<FranjaActiva> optionalFranjaActiva = this.franjaActivaService.findById(id);
        String response = verify(optionalFranjaActiva, userDetails.getUsername());
        if(response != null){
            return ResponseEntity.badRequest().body(new MessageResponse(response));
        }
        this.franjaActivaService.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("Franja eliminada con exito"));
    }

    @DeleteMapping(path="/franja-activa/delete-all")
    public ResponseEntity<MessageResponse> deleteAllByUser(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(new Usuario());
        this.franjaActivaService.deleteByUser(usuario);
        return ResponseEntity.ok(new MessageResponse("Franjas eliminadas"));
    }

    @GetMapping(path="/franja-activa/recomendations/{day}")
    public ResponseEntity<List<TareaSugeridaResponse>> getRecomendations(@PathVariable int day){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(new Usuario());

        //comparacion de las franjas
        Optional<FranjaActiva> optionalFranjaActiva = this.franjaActivaService.findByUserAndDay(usuario, day);
        if(optionalFranjaActiva.isPresent()){
            OffsetTime currentTime = OffsetTime.now();
            FranjaActiva franjaActiva = optionalFranjaActiva.get();
            if(currentTime.compareTo(franjaActiva.getFranjaInicio()) < 0 || currentTime.compareTo(franjaActiva.getFranjaFin()) > 0){
                return ResponseEntity.ok(new ArrayList<>());
            }
        }

        List<TareaSugeridaResponse> tareasSugeridas = new ArrayList<>();
        ArrayList<Tarea> tareas = (ArrayList<Tarea>) this.tareaDeUsuarioService.findTareasPendientesByUsuario(usuario);

        Tarea masCercana = tareas.get(0);
        Tarea mayorPrioridad = tareas.get(0);
        Tarea mayorEstimacion = tareas.get(0);
        for(int i = 1; i < tareas.size(); i++){
            Tarea current = tareas.get(i);
            if(masCercana.getFechaFin().compareTo(current.getFechaFin()) > 0)
                masCercana = tareas.get(i);
            if(mayorPrioridad.getPrioridad() < current.getPrioridad())
                mayorPrioridad = tareas.get(i);
            if(mayorEstimacion.getEstimacion() < current.getEstimacion())
                mayorEstimacion = tareas.get(i);
        }

        TareaSugeridaResponse tsr = new TareaSugeridaResponse();
        tsr.setId(masCercana.getId());
        tsr.setCriterio("Tarea mas cercana");
        tareasSugeridas.add(tsr);

        tsr = new TareaSugeridaResponse();
        tsr.setId(mayorPrioridad.getId());
        tsr.setCriterio("Tarea con mas prioridad");
        tareasSugeridas.add(tsr);

        tsr = new TareaSugeridaResponse();
        tsr.setId(mayorEstimacion.getId());
        tsr.setCriterio("Tarea que se espera dure mas tiempo");
        tareasSugeridas.add(tsr);

        return ResponseEntity.ok(tareasSugeridas);
    }

    private String verify(Optional<FranjaActiva> optionalFranjaActiva, String username){
        if(optionalFranjaActiva.isEmpty()){
            LOGGER.info(FA_IS_NOT_PRESENT);
            return FA_IS_NOT_PRESENT;
        }
        FranjaActiva franjaActiva = optionalFranjaActiva.get();
        if(!franjaActiva.getUsuarioFL().getUsuario().equals(username)){
            LOGGER.info(FA_DOES_NOT_BELONG);
            return FA_DOES_NOT_BELONG;
        }
        return null;
    }
}
