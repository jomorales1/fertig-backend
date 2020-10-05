package com.fertigApp.backend.controller;

import com.fertigApp.backend.model.Completada;
import com.fertigApp.backend.model.Rutina;
import com.fertigApp.backend.repository.CompletadaRepository;
import com.fertigApp.backend.repository.RutinaRepository;
import com.fertigApp.backend.repository.UsuarioRepository;
import com.fertigApp.backend.requestModels.RequestCompletada;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
public class CompletadaController {
    @Autowired
    private CompletadaRepository completadaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RutinaRepository rutinaRepository;

    @GetMapping(path="/completed/getCompleted/{id}")
    public Iterable<Completada> getAllCompletadasByRutina(@PathVariable Integer id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String user = userDetails.getUsername();
        try {
            Rutina rutina = rutinaRepository.findById(id).get();
            if (rutina == null || rutina.getUsuario().getUsuario() != user) {
                System.out.println("Invalid data");
                return null;
            }
            return rutina.getCompletadas();
        } catch (java.util.NoSuchElementException ex) {
            System.out.println("User not found");
            return null;
        }
    }

    @GetMapping(path="/completed/getOneCompleted/{id}")
    public Completada getCompleted(@PathVariable Integer id) {
        try {
            return completadaRepository.findById(id).get();
        } catch (java.util.NoSuchElementException ex) {
            System.out.println("Completed not found");
            return null;
        }
    }

    @PostMapping(path="/completed/addCompleted/")
    public @ResponseBody
    ResponseEntity<Void> addNewCompletada(@RequestBody RequestCompletada requestCompletada) {
        // Missing check information process
        Completada completada = new Completada();
        if (rutinaRepository.findById(requestCompletada.getRutina()).get() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        completada.setRutina(rutinaRepository.findById(requestCompletada.getRutina()).get());
        completada.setFecha(requestCompletada.getFecha());
        this.completadaRepository.save(completada);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
