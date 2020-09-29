package com.fertigApp.backend.controller;

import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController	// This means that this class is a Controller
//@RequestMapping(path="/demo") // This means URL's start with /demo (after Application path)
public class UsuarioController {
	@Autowired // This means to get the bean called usuarioRepository
			   // Which is auto-generated by Spring, we will use it to handle the data
	private UsuarioRepository usuarioRepository;

	@GetMapping(path="/users")
	public @ResponseBody Iterable<Usuario> getAllUsuarios() {
		// This returns a JSON or XML with the usuarios
		return usuarioRepository.findAll();
	}

	@GetMapping(path="/users/{correo}")
	public Usuario getUsuario(@PathVariable String id) {
		return usuarioRepository.findById(id).get();
	}

	@PutMapping(path="/users/{correo}")
	public Usuario replaceUsuario(@PathVariable String correo, @RequestBody Usuario user) {
		return usuarioRepository.findById(correo)
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

	@PostMapping(path="/users") // Map ONLY POST Requests
	public @ResponseBody String addNewUsuario (@RequestBody Usuario usuario) {
		if (usuarioRepository.existsById(usuario.getCorreo()))
			return "Already exists";
		usuarioRepository.save(usuario);
		return "Saved";
	}

	@DeleteMapping(path="/users/{correo}")
	public void deleteUsuario(@PathVariable String correo) {
		usuarioRepository.deleteById(correo);
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
	}
}
