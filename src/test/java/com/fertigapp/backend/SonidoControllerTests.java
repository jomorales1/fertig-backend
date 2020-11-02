package com.fertigapp.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fertigApp.backend.BackendApplication;
import com.fertigApp.backend.model.Preferido;
import com.fertigApp.backend.model.Sonido;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.requestModels.LoginRequest;
import com.fertigApp.backend.services.PreferidoService;
import com.fertigApp.backend.services.SonidoService;
import com.fertigApp.backend.services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = BackendApplication.class)
@AutoConfigureMockMvc
class SonidoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SonidoService sonidoService;

    @Autowired
    private PreferidoService preferidoService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Jackson2ObjectMapperBuilder mapperBuilder = new Jackson2ObjectMapperBuilder();

    private final ObjectMapper objectMapper = mapperBuilder.build();

    Usuario createUser() {
        Usuario user = new Usuario();

        user.setUsuario("test_user");
        user.setCorreo("test@email.com");
        user.setNombre("Test User");
        user.setPassword(passwordEncoder.encode("testing"));
        user.setTareas(new ArrayList<>());
        user.setRutinas(new ArrayList<>());
        user.setEventos(new ArrayList<>());

        this.usuarioService.save(user);
        return user;
    }

    String getToken(Usuario user) throws Exception {
        String token = "";

        if (usuarioService.existsById(user.getUsuario())) {
            String uri = "/signin";

            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setUsername(user.getUsuario());
            loginRequest.setPassword("testing");

            ResultActions resultActions = this.mockMvc.perform(post(uri).contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(loginRequest)).accept(MediaType.ALL)).andExpect(status().isOk());

            String response = resultActions.andReturn().getResponse().getContentAsString();
            JacksonJsonParser jsonParser = new JacksonJsonParser();
            token = jsonParser.parseMap(response).get("access_token").toString();
        }

        return token;
    }

    Sonido setUpSonido(Usuario usuario) {
        if (this.usuarioService.findById(usuario.getUsuario()).isEmpty())
            this.usuarioService.save(usuario);
        Sonido sonido = new Sonido();
        sonido.setId("testSound");
        sonido.addUsuario(usuario);
        sonido = this.sonidoService.save(sonido);
        Preferido preferido = new Preferido();
        preferido.setUsuario(usuario);
        preferido.setSonido(sonido);
        this.preferidoService.add(preferido);
        return sonido;
    }

    @Test
    void contextLoads() {
        assertTrue(true);
    }

    @Test
    void addSound() throws Exception {

    }

    @Test
    void getAllSoundsByUser() throws Exception {

    }

    @Test
    void deleteSound() throws Exception {

    }

}
