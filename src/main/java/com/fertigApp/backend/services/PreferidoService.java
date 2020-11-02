package com.fertigApp.backend.services;

import com.fertigApp.backend.model.IdPreferido;
import com.fertigApp.backend.model.Preferido;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.repository.PreferidoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PreferidoService {
    private final PreferidoRepository preferidoRepository;


    public PreferidoService(PreferidoRepository preferidoRepository) {
        this.preferidoRepository = preferidoRepository;
    }

    public Iterable<Preferido> findAll(){
        return this.preferidoRepository.findAll();
    }

    public Optional<Preferido> findById(String id){
        return this.preferidoRepository.findById(id);
    }

    public Preferido add(Preferido preferido){
        return  this.preferidoRepository.save(preferido);
    }

    public  void deleteById(String id){
        this.preferidoRepository.deleteById(id);
    }

    public Iterable<Preferido> getByUsuario(Usuario usuario){
         return this.preferidoRepository.findPreferidoByUsuario(usuario);
    }
}
