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

import java.util.List;

/*
 * Clase responsable de manejar request de tipo GET, POST, PUT y DELETE para
 * la entidad "Usuario".
 * */
@RestController	// This means that this class is a Controller
//@RequestMapping(path="/demo") // This means URL's start with /demo (after Application path)
public class UsuarioController {

	// Repositorio responsable del manejo de la tabla "usuario" en la DB.
	@Autowired
	private UsuarioRepository usuarioRepository;

	// Objeto responsable de la encriptación de contraseñas.
	@Autowired
	private PasswordEncoder passwordEncoder;

	// Objeto responsable de la creación de tokens para usuarios.
	@Autowired
	private UserDetailsManager userDetailsManager;

	// Método GET para obtener todas las entidades de tipo "usuario" de la DB.
	@GetMapping(path="/users/getAllUsers") //Disponible como rol ADMIN
	public @ResponseBody Iterable<Usuario> getAllUsuarios() {
		// This returns a JSON or XML with the usuarios
		return usuarioRepository.findAll();
	}

	// Método GET para obtener la información del usuario hace la request.
	@GetMapping(path="/users/get")
	public Usuario getUsuario() {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		try {
			return usuarioRepository.findById(userDetails.getUsername()).get();
		} catch(java.util.NoSuchElementException ex){
			return null;
		}
	}

	// Método PUT para modificar la información de un usuario en la DB.
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

	// Método POST para añadir un registro de tipo "usuario" en la DB.
	@PostMapping(path="/users/addUser") // Map ONLY POST Requests
	public @ResponseBody ResponseEntity<Void> addNewUsuario (@RequestBody RequestUsuario requestUsuario) {
		Usuario usuario = new Usuario();
		usuario.setCorreo(requestUsuario.getCorreo());
		usuario.setNombre(requestUsuario.getNombre());
		usuario.setUsuario(requestUsuario.getUsuario());
		usuario.setPassword(passwordEncoder.encode(requestUsuario.getPassword()));

		if (usuarioRepository.existsById(usuario.getUsuario()))
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		List<Usuario> usuarios = (List<Usuario>) usuarioRepository.findByCorreo(usuario.getCorreo());
		if(!usuarios.isEmpty())
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		usuarioRepository.save(usuario);
		UserDetails user = User.builder().username(usuario.getUsuario()).password(usuario.getPassword()).
				roles("USER").build();
		userDetailsManager.createUser(user);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	// Método DELETE para eliminar un registro de tipo "usuario" en la DB.
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
