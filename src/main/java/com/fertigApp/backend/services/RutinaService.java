package com.fertigApp.backend.services;

import com.fertigApp.backend.model.Rutina;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.repository.RutinaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RutinaService {

    private final RutinaRepository rutinaRepository;


    public RutinaService(RutinaRepository rutinaRepository) {
        this.rutinaRepository = rutinaRepository;
    }

    public Iterable<Rutina> findAll(){
        return rutinaRepository.findAll();
    }

    public Optional<Rutina> findById(Integer id){
        return rutinaRepository.findById(id);
    }

    public Rutina save(Rutina rutina){
        return rutinaRepository.save(rutina);
    }

    public void deleteById(Integer id){
        rutinaRepository.deleteById(id);
    }

    public Iterable<Rutina> findByUsuario (Usuario usuario){
        return rutinaRepository.findByUsuarioR(usuario);
    }
}
