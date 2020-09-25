package com.fertigapp.backend.controller;

import com.fertigapp.backend.model.Usuario;
import com.fertigapp.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path="/test")
public class MainController {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping(path="/add")
    public @ResponseBody String addNewUsuario(@RequestParam String correo, @RequestParam String nombre, @RequestParam String password){
        Usuario u = new Usuario();
        u.setCorreo(correo);
        u.setNombre(nombre);
        u.setPassword(password);
        return "Agregado";
    }

    @PostMapping(path="/all")
    public @ResponseBody Iterable<Usuario> getAllUsuarios(){
        return usuarioRepository.findAll();
    }
}
