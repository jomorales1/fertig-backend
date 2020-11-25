package com.fertigapp.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fertigapp.backend.model.FirebaseNotificationToken;
import com.fertigapp.backend.model.Usuario;
import com.fertigapp.backend.requestModels.LoginRequest;
import com.fertigapp.backend.services.FirebaseNTService;
import com.fertigapp.backend.services.UsuarioService;
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

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = BackendApplication.class)
@AutoConfigureMockMvc
class NotificationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private FirebaseNTService firebaseNTService;

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
        user.setTareas(new HashSet<>());
        user.setRutinas(new HashSet<>());
        user.setEventos(new HashSet<>());

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

    public FirebaseNotificationToken setUpToken(Usuario user) {
        if (usuarioService.findById(user.getUsuario()).isEmpty()) {
            this.usuarioService.save(user);
        }

        FirebaseNotificationToken notificationToken = new FirebaseNotificationToken();
        notificationToken.setUsuarioF(user);
        notificationToken.setToken("token");
        notificationToken = this.firebaseNTService.save(notificationToken);

        return notificationToken;
    }

    @Test
    void contextLoads() {
        assertTrue(true);
    }

    @Test
    void getAllTokensByUser() throws Exception {
        String uri = "/notification/tokens";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        String token = getToken(user);
        FirebaseNotificationToken notificationToken = setUpToken(user);

        ResultActions resultActions = this.mockMvc.perform(get(uri).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        CollectionType javaList = objectMapper.getTypeFactory().constructCollectionType(List.class, String.class);
        List<String> tokens = this.objectMapper.readValue(response, javaList);
        assertNotNull(tokens);
        assertEquals(1, tokens.size());
        assertEquals(notificationToken.getToken(), tokens.get(0));

        this.firebaseNTService.deleteById(notificationToken.getToken());
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void addFirebaseToken() throws Exception {
        String uri = "/notification/add-token";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        String token = getToken(user);

        String firebaseToken = "token";

        this.mockMvc.perform(post(uri).header("Authorization", "Bearer " + token)
            .param("token", firebaseToken)).andExpect(status().isOk());

        this.firebaseNTService.deleteById(firebaseToken);
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void deleteFirebaseToken() throws Exception {
        String uri = "/notification/delete-token";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        String token = getToken(user);
        FirebaseNotificationToken notificationToken = setUpToken(user);

        this.mockMvc.perform(delete(uri).header("Authorization", "Bearer " + token)
            .param("id", notificationToken.getToken() + "a")).andExpect(status().isBadRequest());

        Usuario usuario = new Usuario();
        usuario.setUsuario("usuario");
        usuario.setCorreo("correo@test.com");
        usuario.setNombre("Usuario de prueba");
        usuario.setPassword(passwordEncoder.encode("testing"));
        this.usuarioService.save(usuario);

        FirebaseNotificationToken newToken = new FirebaseNotificationToken();
        newToken.setUsuarioF(usuario);
        newToken.setToken("new_token");
        this.firebaseNTService.save(newToken);

        this.mockMvc.perform(delete(uri).header("Authorization", "Bearer " + token)
                .param("id", newToken.getToken())).andExpect(status().isBadRequest());

        this.mockMvc.perform(delete(uri).header("Authorization", "Bearer " + token)
                .param("id", notificationToken.getToken())).andExpect(status().isOk());

        this.firebaseNTService.deleteById(newToken.getToken());
        this.usuarioService.deleteById(usuario.getUsuario());
        this.usuarioService.deleteById(user.getUsuario());
    }

}
