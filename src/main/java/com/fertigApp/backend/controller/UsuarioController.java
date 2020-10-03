package com.fertigApp.backend.controller;

import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.repository.UsuarioRepository;
import com.fertigApp.backend.requestModels.RequestUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController	// This means that this class is a Controller
//@RequestMapping(path="/demo") // This means URL's start with /demo (after Application path)
public class UsuarioController {
	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserDetailsManager userDetailsManager;

	@GetMapping(path="/users/getAllUsers")
	public @ResponseBody Iterable<Usuario> getAllUsuarios() {
		// This returns a JSON or XML with the usuarios
		return usuarioRepository.findAll();
	}

	@GetMapping(path="/users/get")
	public Usuario getUsuario() {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		try {
			return usuarioRepository.findById(userDetails.getUsername()).get();
		} catch(java.util.NoSuchElementException ex){
			return null;
		}
	}

	@PutMapping(path="/users/update")
	public Usuario replaceUsuario(@RequestBody RequestUsuario requestUsuario) {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario user = new Usuario();
		user.setCorreo(requestUsuario.getCorreo());
		user.setNombre(requestUsuario.getNombre());
		user.setUsuario(requestUsuario.getUsuario());
		user.setPassword(passwordEncoder.encode(requestUsuario.getPassword()));
		return usuarioRepository.findById(userDetails.getUsername())
				.map(usuario -> {
					usuario.setCorreo(user.getCorreo());
					usuario.setNombre(user.getNombre());
					usuario.setPassword(user.getPassword());
					usuarioRepository.save(usuario);
					return usuario;
				})
				.orElseGet(() -> {
					Usuario newUser = new Usuario();
					newUser.setCorreo(user.getCorreo());
					newUser.setNombre(user.getNombre());
					newUser.setPassword(user.getPassword());
					usuarioRepository.save(newUser);
					return newUser;
				});
	}

	@PostMapping(path="/users/addUser") // Map ONLY POST Requests
	public @ResponseBody ResponseEntity<Void> addNewUsuario (@RequestBody RequestUsuario requestUsuario) {
		Usuario usuario = new Usuario();
		usuario.setCorreo(requestUsuario.getCorreo());
		usuario.setNombre(requestUsuario.getNombre());
		usuario.setUsuario(requestUsuario.getUsuario());
		usuario.setPassword(passwordEncoder.encode(requestUsuario.getPassword()));

		if(usuarioRepository.existsById(usuario.getCorreo())) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		usuarioRepository.save(usuario);
		UserDetails user = User.builder().username(usuario.getUsuario()).password(usuario.getPassword()).
				roles("USER").build();
		userDetailsManager.createUser(user);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@DeleteMapping(path="/users/delete/")
	public boolean deleteUsuario() {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		try{
			usuarioRepository.deleteById(userDetails.getUsername());
			return true;
		} catch(org.springframework.dao.EmptyResultDataAccessException ex){
			return false;
		}
	}

	@RequestMapping("/publica")
	public String publico() {
		return "Pagina Publica";
	}
	@RequestMapping("/privada")
	public String privada() {
		return "Pagina Privada";
	}
	@RequestMapping("/admin")
	public String admin() {
		return "Pagina Administrador";
	}/**/
}
