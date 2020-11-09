package com.fertigapp.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fertigApp.backend.BackendApplication;
import com.fertigApp.backend.model.Preferido;
import com.fertigApp.backend.model.Sonido;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.payload.response.SonidoResponse;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
            String uri = "/sign-in";

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
        this.preferidoService.save(preferido);
        return sonido;
    }

    @Test
    void contextLoads() {
        assertTrue(true);
    }

    @Test
    void getAllSounds() throws Exception {
        String uri = "/sounds";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        String token = getToken(user);
        Sonido sonido = setUpSonido(user);
        Sonido sonido1 = new Sonido();
        sonido1.setId("testSound2");
        this.sonidoService.save(sonido1);

        ResultActions resultActions = this.mockMvc.perform(get(uri).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        CollectionType javaList = objectMapper.getTypeFactory().constructCollectionType(List.class, SonidoResponse.class);
        List<SonidoResponse> sonidoResponses = objectMapper.readValue(response, javaList);
        assertNotNull(sonidoResponses);
        assertEquals(sonidoResponses.size(), 2);

        for (SonidoResponse sonidoResponse : sonidoResponses) {
            if (sonidoResponse.getSonido().equals(sonido.getId())) {
                assertTrue(sonidoResponse.isFavorite());
            } else {
                assertFalse(sonidoResponse.isFavorite());
            }
        }

        this.preferidoService.deleteAllByUsuarioAndSonido(user, sonido);
        this.sonidoService.deleteById(sonido1.getId());
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void addFavorite() throws Exception {
        String uri = "/sound/add-favorite/";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        String token = getToken(user);
        Sonido preferido = setUpSonido(user);
        Sonido sonido = new Sonido();
        sonido.setId("testSound2");
        this.sonidoService.save(sonido);

        this.mockMvc.perform(post(uri + sonido.getId() + "a").header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(post(uri + preferido.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(post(uri + sonido.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());

        this.preferidoService.deleteAllByUsuarioAndSonido(user, sonido);
        this.preferidoService.deleteAllByUsuarioAndSonido(user, preferido);
        this.sonidoService.deleteById(sonido.getId());
        this.sonidoService.deleteById(preferido.getId());
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void getAllFavoritesByUser() throws Exception {
        String uri = "/sound/favorites";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        String token = getToken(user);
        Sonido sonido = setUpSonido(user);

        ResultActions resultActions = this.mockMvc.perform(get(uri).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        CollectionType javaList = objectMapper.getTypeFactory().constructCollectionType(List.class, Sonido.class);
        List<Sonido> sonidos = objectMapper.readValue(response, javaList);
        assertNotNull(sonidos);
        assertEquals(sonidos.get(0).getId(), sonido.getId());

        this.preferidoService.deleteAllByUsuarioAndSonido(user, sonido);
        this.sonidoService.deleteById(sonido.getId());
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void deleteFavorite() throws Exception {
        String uri = "/sound/delete-favorite/";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        String token = getToken(user);
        Sonido sonido = setUpSonido(user);

        this.mockMvc.perform(delete(uri + sonido.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        this.mockMvc.perform(delete(uri + sonido.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(delete(uri + sonido.getId() + "a").header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        this.preferidoService.deleteAllByUsuarioAndSonido(user, sonido);
        this.sonidoService.deleteById(sonido.getId());
        this.usuarioService.deleteById(user.getUsuario());
    }

}
