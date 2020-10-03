package com.fertigApp.backend.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fertigApp.backend.model.Tarea;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.repository.TareaRepository;
import com.fertigApp.backend.repository.UsuarioRepository;
import org.codehaus.jackson.map.ser.std.IterableSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.swing.tree.ExpandVetoException;
import java.util.List;

@RestController	// This means that this class is a Controller
//@RequestMapping(path="/demo") // This means URL's start with /demo (after Application path)
public class MainController {/*

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private TareaRepository tareaRepository;

	private ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).setSerializationInclusion(JsonInclude.Include.NON_NULL);;

	@Autowired
	private UserDetailsManager userDetailsManager;

	@Autowired
	private PasswordEncoder passwordEncoder;


	@PostMapping(path="/add") // Map ONLY POST Requests
	public ResponseEntity<Void> addNewUsuario (@RequestParam String usuario,@RequestParam String correo, @RequestParam String nombre, @RequestParam String password) {
	    if(usuarioRepository.existsById(usuario)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

		Usuario n = new Usuario();
	    n.setUsuario(usuario);
		n.setCorreo(correo);
		n.setNombre(nombre);
		n.setPassword(password);
		usuarioRepository.save(n);
		UserDetails user = User.builder().username(usuario).password(passwordEncoder.encode(password)).
				roles("USER").build();
		userDetailsManager.createUser(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@GetMapping(path="/all")
	public @ResponseBody Iterable<Usuario> getAllUsuarios() {
		// This returns a JSON or XML with the usuarios
		return usuarioRepository.findAll();
	}

	@GetMapping(path="/{usuario}/tareas")
	public @ResponseBody List<Tarea> getTareasByUsuario(@PathVariable String usuario){
		return usuarioRepository.findById(usuario).get().getTareas();
//		try {
//			if(usuarioRepository.findById(usuario).isPresent()) {
//				//return objectMapper.writeValueAsString(usuarioRepository.findById(usuario).get().getTareas());
//			} else {
//				return "No encontrado";
//			}
//		} catch(Exception e){e.printStackTrace();}
//
//		return ";";
	}

	@RequestMapping("/publica")
	public String publico() {
		return "Pagina Publica";
	}/*
	@RequestMapping("/privada")
	public String privada() {
		return "Pagina Privada";
	}
	@RequestMapping("/admin")
	public String admin() {
		return "Pagina Administrador";
	}*/
}
