package com.fertigApp.backend.controller;

import com.fertigApp.backend.model.Completada;
import com.fertigApp.backend.model.Rutina;
import com.fertigApp.backend.requestModels.RequestCompletada;
import com.fertigApp.backend.services.CompletadaService;
import com.fertigApp.backend.services.RutinaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/*
* Clase responsable de manejar request de tipo GET, POST y DELETE para
* la entidad "Completada".
* */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class CompletadaController {

    private static final Logger LOGGER= LoggerFactory.getLogger(Completada.class);

    // Repositorio responsable del manejo de la tabla "completada" en la DB.
    private final CompletadaService completadaService;

    // Repositorio responsable del manejo de la tabla "rutina" en la DB.
    private final RutinaService rutinaService;

    public CompletadaController(CompletadaService completadaService, RutinaService rutinaService) {
        this.completadaService = completadaService;
        this.rutinaService = rutinaService;
    }

    // Método GET para obtener del servidor una lista de actividades completadas
    // que están relacionadas con una rutina específica.
    @GetMapping(path="/completed/getCompleted/{id}")
    public Iterable<Completada> getAllCompletadasByRutina(@PathVariable Integer id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String user = userDetails.getUsername();

        if(rutinaService.findById(id).isPresent()){
            Rutina rutina = rutinaService.findById(id).get();
            if (rutina.getUsuario().getUsuario().equals(user)) {
                LOGGER.info("Invalid data");
                return null;
            }
            return rutina.getCompletadas();
        }
        return null;
    }

    // Método GET para obtener una entidad "completada" por medio de su id.
    @GetMapping(path="/completed/getOneCompleted/{id}")
    public Completada getCompleted(@PathVariable Integer id) {
        if(completadaService.findById(id).isPresent())
            return completadaService.findById(id).get();
        else{
            LOGGER.info("Completed not found");
            return null;
        }
    }

    // Método POST para agregar una entidad "completada" relacionada con cierta rutina.
    @PostMapping(path="/completed/addCompleted/")
    public @ResponseBody
    ResponseEntity<Void> addNewCompletada(@RequestBody RequestCompletada requestCompletada) {
        // Missing check information process
        Completada completada = new Completada();
        if (rutinaService.findById(requestCompletada.getRutina()).isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        completada.setRutina(rutinaService.findById(requestCompletada.getRutina()).get());
        completada.setFecha(requestCompletada.getFecha());
        rutinaService.findById(requestCompletada.getRutina()).get().getCompletadas().add(completada);
        this.completadaService.save(completada);
        this.rutinaService.save(rutinaService.findById(requestCompletada.getRutina()).get());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
