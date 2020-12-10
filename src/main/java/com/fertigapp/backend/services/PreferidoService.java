package com.fertigapp.backend.services;

import com.fertigapp.backend.model.Preferido;
import com.fertigapp.backend.model.Sonido;
import com.fertigapp.backend.model.Usuario;
import com.fertigapp.backend.repository.PreferidoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PreferidoService {
    private final PreferidoRepository preferidoRepository;


    public PreferidoService(PreferidoRepository preferidoRepository) {
        this.preferidoRepository = preferidoRepository;
    }

    public Optional<Preferido> findById(String id){
        return this.preferidoRepository.findById(id);
    }

    public Preferido save(Preferido preferido){
        return  this.preferidoRepository.save(preferido);
    }

    public  void deleteById(String id){
        this.preferidoRepository.deleteById(id);
    }

    public Iterable<Preferido> findByUsuario(Usuario usuario){
         return this.preferidoRepository.findPreferidoByUsuario(usuario);
    }

    public void deleteAllByUsuarioAndSonido(Usuario usuario, Sonido sonido){
        this.preferidoRepository.deleteAllByUsuarioAndSonido(usuario,sonido);
    }

    public Optional<Preferido> findByUsuarioAndSonido(Usuario usuario, Sonido sonido){
        return this.preferidoRepository.findByUsuarioAndSonido(usuario, sonido);
    }
}
