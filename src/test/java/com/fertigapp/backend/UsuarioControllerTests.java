package com.fertigapp.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fertigApp.backend.BackendApplication;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.requestModels.LoginRequest;
import com.fertigApp.backend.requestModels.RequestUsuario;
import com.fertigApp.backend.services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK, classes = BackendApplication.class)
@AutoConfigureMockMvc
class UsuarioControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioService usuarioService;

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

    @Test
    void contextLoads() {
        assertTrue(true);
    }

    @Test
    void signIn() throws Exception {
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        String uri = "/signin";
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(user.getUsuario());
        loginRequest.setPassword("testing");
        this.mockMvc.perform(post(uri).contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(objectMapper.writeValueAsString(loginRequest)).accept(MediaType.ALL)).andExpect(status().isOk());
        //assertThat(resultActions.andExpect(status().isOk()));
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    @WithMockUser(value = "ADMIN")
    void getAllUsuarios() throws Exception {
        String uri = "/users/getAllUsers";
        ResultActions resultActions = this.mockMvc.perform(get(uri)).andExpect(status().isOk());
        //assertThat(resultActions.andExpect(status().isOk()));
        MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        CollectionType javaList = objectMapper.getTypeFactory().constructCollectionType(List.class, Usuario.class);
        List<Usuario> users = objectMapper.readValue(response, javaList);
        assertNotNull(users);
    }

    @Test
    void getUsuario() throws Exception {
        String uri = "/users/get";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        String token = getToken(user);

        ResultActions resultActions = this.mockMvc.perform(get(uri).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        Usuario userObtained = objectMapper.readValue(response, Usuario.class);
        assertEquals(userObtained.getUsuario(), user.getUsuario());
        assertEquals(userObtained.getCorreo(), user.getCorreo());
        assertEquals(userObtained.getNombre(), user.getNombre());

        this.mockMvc.perform(get(uri)).andExpect(status().isUnauthorized());

        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void replaceUsuario() throws Exception {
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        String token = getToken(user);

        String uri = "/users/update";
        String searchUri = "/users/get";

        // Valid request -> status 200 expected
        RequestUsuario requestUsuario = new RequestUsuario(user.getCorreo(), user.getNombre() + "Version 2", user.getUsuario(), "testing");
        this.mockMvc.perform(put(uri).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper
                        .writeValueAsString(requestUsuario))).andExpect(status().isOk());
        ResultActions findUpdatedUser = this.mockMvc.perform(get(searchUri)
        .header("Authorization", "Bearer " + token)).andExpect(status().isOk());
        MvcResult mvcResult = findUpdatedUser.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        Usuario userObtained = objectMapper.readValue(response, Usuario.class);
        assertEquals(userObtained.getUsuario(), requestUsuario.getUsuario());
        assertEquals(userObtained.getCorreo(), requestUsuario.getCorreo());
        assertEquals(userObtained.getNombre(), requestUsuario.getNombre());

        Usuario newUser = new Usuario("srogers", "srogers@avengers.com", this.passwordEncoder.encode("imbetterthanstark"), "Captain America");
        this.usuarioService.save(newUser);

        // Invalid request (existing email) -> status 400 expected
        requestUsuario.setCorreo("srogers@avengers.com");
        this.mockMvc.perform(put(uri).header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(requestUsuario)))
            .andExpect(status().isBadRequest());

        // Invalid request (existing username) -> status 400 expected
        requestUsuario.setCorreo(user.getCorreo());
        requestUsuario.setUsuario("srogers");
        this.mockMvc.perform(put(uri).header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(requestUsuario)))
            .andExpect(status().isBadRequest());

        this.usuarioService.deleteById(user.getUsuario());
        this.usuarioService.deleteById("srogers");
    }

    @Test
    void addNewUser() throws Exception {
        String uri = "/users/addUser";
        RequestUsuario requestUsuario = new RequestUsuario("add_user@test.com", "Test Add User", "addUser", "add_user_password");

        // Valid request -> status 201 expected
        this.mockMvc.perform(post(uri).contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(objectMapper.writeValueAsString(requestUsuario))).andExpect(status().isCreated());

        // Invalid request (existing username) -> status 400 expected
        requestUsuario.setCorreo("add_user2@test.com");
        this.mockMvc.perform(post(uri).contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(objectMapper.writeValueAsString(requestUsuario))).andExpect(status().isBadRequest());

        // Invalid request (existing email) -> status 400 expected
        requestUsuario.setUsuario("addUser2");
        requestUsuario.setCorreo("add_user@test.com");
        this.mockMvc.perform(post(uri).contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(objectMapper.writeValueAsString(requestUsuario))).andExpect(status().isBadRequest());

        this.usuarioService.deleteById("addUser");
    }

    @Test
    void deleteUsuario() throws Exception {
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        String token = getToken(user);

        String uri = "/users/delete";
        this.mockMvc.perform(delete(uri).header("Authorization", "Bearer " + token))
                .andExpect(status().isAccepted());
    }

}

