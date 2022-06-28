package com.fertigapp.backend.controller;

import java.net.URLEncoder;
import com.fertigapp.backend.auth.jwt.JwtUtil;
import com.fertigapp.backend.auth.services.UserDetailsImpl;
import com.fertigapp.backend.model.Usuario;
import com.fertigapp.backend.payload.response.JwtResponse;
import com.fertigapp.backend.payload.response.MessageResponse;
import com.fertigapp.backend.requestmodels.LoginRequest;
import com.fertigapp.backend.requestmodels.RequestUsuario;
import com.fertigapp.backend.services.UsuarioService;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
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
    @PostMapping("/sign-in")
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
                userDetails.getName(),
                userDetails.getEmail(),
                roles));
    }

    // Método GET para obtener todas las entidades de tipo "usuario" de la DB.
    @GetMapping(path="/user/all-users")
    public @ResponseBody Iterable<Usuario> getAllUsuarios() {
        // This returns a JSON or XML with the usuarios
        return usuarioService.findAll();
    }
    // Método GET para obtener todas las entidades de tipo "usuario" cuyo usuario corresponde con la cadena dada
    @GetMapping(path="/user/search/{usuario}")
    public @ResponseBody Iterable<Usuario> getAllUsuarios(@PathVariable String usuario) {
        // This returns a JSON or XML with the usuarios
        return usuarioService.findAllByUsuario(usuario);
    }

    // Método GET para obtener la información del usuario hace la request.
    @GetMapping(path="/user/get")
    public Usuario getUsuario() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Usuario> usuario = usuarioService.findById(userDetails.getUsername());
        return (usuario.orElse(null));
    }

    // Método PUT para modificar la información de un usuario en la DB.
    @PutMapping(path="/user/update")
    public ResponseEntity<Usuario> replaceUsuario(@RequestBody RequestUsuario requestUsuario) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Usuario> optionalUsuario = usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(new Usuario());
        if (this.usuarioService.existsByCorreo(requestUsuario.getCorreo()) && !usuario.getCorreo().equals(requestUsuario.getCorreo())){
            return ResponseEntity.badRequest().body(null);
        }
        if (!usuario.getUsuario().equals(requestUsuario.getUsuario())) {
            return ResponseEntity.badRequest().body(null);
        }
        Usuario user = new Usuario();
        user.setCorreo(requestUsuario.getCorreo());
        user.setNombre(requestUsuario.getNombre());
        user.setUsuario(requestUsuario.getUsuario());
        user.setPassword(passwordEncoder.encode(requestUsuario.getPassword()));
        return ResponseEntity.ok().body(usuarioService.save(user));
    }

    // Método POST para añadir un registro de tipo "usuario" en la DB.
    @PostMapping(path="/user/add") // Map ONLY POST Requests
    public @ResponseBody ResponseEntity<MessageResponse> addNewUsuario (@RequestBody RequestUsuario requestUsuario) throws IOException, JSONException {
        if (requestUsuario.getRecaptcha() == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: recaptchaToken is required"));
        }

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        try {
            HttpPost request = new HttpPost("https://www.google.com/recaptcha/api/siteverify?secret=6Ld1wZ4gAAAAAG0YhFQlFjh6pFkd1edY2Li7_fsW&response="+requestUsuario.getRecaptcha());
            HttpResponse response = httpClient.execute(request);
            String json_string = EntityUtils.toString(response.getEntity());
            JSONObject temp1 = new JSONObject(json_string);
            boolean valid = temp1.getBoolean("success");
            if (!valid){
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: recaptchaToken is required"));
            }
        } catch (Exception ex) {
            // handle exception here
        } finally {
            httpClient.close();
        }

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
    @DeleteMapping(path="/user/delete")
    public ResponseEntity<Void> deleteUsuario() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        usuarioService.deleteById(userDetails.getUsername());
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping(path = "/user/friends")
    public ResponseEntity<Set<Usuario>> getFriends() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(new Usuario());
        return ResponseEntity.ok(usuario.getAgregados());
    }

    @PutMapping(path = "/user/add-friend/{username}")
    public ResponseEntity<Void> addFriend(@PathVariable String username) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Optional<Usuario> optionalUsuario1 = this.usuarioService.findById(username);
        if (optionalUsuario1.isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Usuario usuario = optionalUsuario.orElse(new Usuario());
        Usuario friend = optionalUsuario1.get();
        usuario.addAmigo(friend);
        this.usuarioService.save(usuario);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(path = "/user/delete-friend/{username}")
    public ResponseEntity<Void> deleteFriend(@PathVariable String username) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Optional<Usuario> optionalUsuario1 = this.usuarioService.findById(username);
        if (optionalUsuario1.isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Usuario usuario = optionalUsuario.orElse(new Usuario());
        Usuario friend = optionalUsuario1.get();
        boolean deleted = usuario.deleteAgregado(friend);
        if (!deleted) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        this.usuarioService.save(usuario);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
