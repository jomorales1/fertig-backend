package com.fertigApp.backend.controller;

import com.fertigApp.backend.auth.jwt.JwtUtil;
import com.fertigApp.backend.auth.services.UserDetailsImpl;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.payload.response.JwtResponse;
import com.fertigApp.backend.payload.response.MessageResponse;
import com.fertigApp.backend.requestModels.LoginRequest;
import com.fertigApp.backend.requestModels.RequestUsuario;
import com.fertigApp.backend.services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/*
 * Clase responsable de manejar request de tipo GET, POST, PUT y DELETE para
 * la entidad "Usuario".
 * */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController	// This means that this class is a Controller
//@RequestMapping(path="/demo") // This means URL's start with /demo (after Application path)
public class UsuarioController {

    // Repositorio responsable del manejo de la tabla "usuario" en la DB.
    private final UsuarioService usuarioService;

    // Objeto responsable de la encriptación de contraseñas.
    private final PasswordEncoder passwordEncoder;

    final
    AuthenticationManager authenticationManager;

    final
    JwtUtil jwtUtils;

    public UsuarioController(UsuarioService usuarioService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtils) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    //Metodo POST para iniciar sesión
    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        //se llama al administrador de autenticación para que
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    // Método GET para obtener todas las entidades de tipo "usuario" de la DB.
    @GetMapping(path="/users/getAllUsers") //Disponible como rol ADMIN
    public @ResponseBody Iterable<Usuario> getAllUsuarios() {
        // This returns a JSON or XML with the usuarios
        return usuarioService.findAll();
    }

    // Método GET para obtener la información del usuario hace la request.
    @GetMapping(path="/users/get")
    public Usuario getUsuario() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Usuario> usuario = usuarioService.findById(userDetails.getUsername());
        return (usuario.orElse(null));
    }

    // Método PUT para modificar la información de un usuario en la DB.
    @PutMapping(path="/users/update")
    public ResponseEntity<Usuario> replaceUsuario(@RequestBody RequestUsuario requestUsuario) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<Usuario> optUsuario = usuarioService.findById(userDetails.getUsername());
        if(usuarioService.existsByCorreo(requestUsuario.getCorreo()) && optUsuario.isPresent() && !optUsuario.get().getCorreo().equals(requestUsuario.getCorreo())){
            return ResponseEntity.badRequest().body(null);
        }

        if (optUsuario.isPresent() && !optUsuario.get().getUsuario().equals(requestUsuario.getUsuario()) && usuarioService.findById(requestUsuario.getUsuario()).isPresent()) {
            return ResponseEntity.badRequest().body(null);
        }

        Usuario user = new Usuario();
        user.setCorreo(requestUsuario.getCorreo());
        user.setNombre(requestUsuario.getNombre());
        user.setUsuario(requestUsuario.getUsuario());
        user.setPassword(passwordEncoder.encode(requestUsuario.getPassword()));
        if(optUsuario.isPresent()){
            Usuario usuario = optUsuario.get();
            usuario.setCorreo(user.getCorreo());
            usuario.setNombre(user.getNombre());
            if(!requestUsuario.getPassword().equals(""))
                usuario.setPassword(user.getPassword());
            usuarioService.save(usuario);
            return ResponseEntity.ok().body(usuario);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Método POST para añadir un registro de tipo "usuario" en la DB.
    @PostMapping(path="/users/addUser") // Map ONLY POST Requests
    public @ResponseBody ResponseEntity<MessageResponse> addNewUsuario (@RequestBody RequestUsuario requestUsuario) {
        if (usuarioService.existsById(requestUsuario.getUsuario()))
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Ya existe una cuenta con este usuario"));
        if(usuarioService.existsByCorreo(requestUsuario.getCorreo()))
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: a existe una cuenta con este correo"));
        Usuario usuario = new Usuario(
                requestUsuario.getUsuario(),
                requestUsuario.getCorreo(),
                passwordEncoder.encode(requestUsuario.getPassword()),
                requestUsuario.getNombre());
        usuarioService.save(usuario);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Método DELETE para eliminar un registro de tipo "usuario" en la DB.
    @DeleteMapping(path="/users/delete")
    public ResponseEntity<Void> deleteUsuario() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        usuarioService.deleteById(userDetails.getUsername());
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
