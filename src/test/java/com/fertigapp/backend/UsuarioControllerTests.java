package com.fertigapp.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fertigApp.backend.BackendApplication;
import com.fertigApp.backend.auth.jwt.JwtUtil;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.repository.UsuarioRepository;
import com.fertigApp.backend.requestModels.LoginRequest;
import com.fertigApp.backend.requestModels.RequestUsuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK, classes = BackendApplication.class)
@AutoConfigureMockMvc
public class UsuarioControllerTests {

    private int port = 8090;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Jackson2ObjectMapperBuilder mapperBuilder = new Jackson2ObjectMapperBuilder();

    private ObjectMapper objectMapper = mapperBuilder.build();

    public Usuario setUp() {
        Usuario user = new Usuario();

        user.setUsuario("test_user");
        user.setCorreo("test@email.com");
        user.setNombre("Test User");
        user.setPassword(passwordEncoder.encode("testing"));

        this.usuarioRepository.save(user);
        return user;
    }

    public String getToken(Usuario user) throws Exception {
        String token = "";

        if (usuarioRepository.existsById(user.getUsuario())) {
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
    public void contextLoads() {
        assertTrue(true);
    }

    @Test
    public void signIn() throws Exception {
        Usuario user = null;
        if (!this.usuarioRepository.existsById("test_user"))
            user = setUp();
        else user = this.usuarioRepository.findById("test_user").get();
        String uri = "/signin";
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(user.getUsuario());
        loginRequest.setPassword("testing");
        ResultActions resultActions = this.mockMvc.perform(post(uri).contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(objectMapper.writeValueAsString(loginRequest)).accept(MediaType.ALL));
        assertThat(resultActions.andExpect(status().isOk()));
        this.usuarioRepository.deleteById(user.getUsuario());
    }

    @Test
    @WithMockUser(value = "ADMIN")
    public void getAllUsuarios() throws Exception {
        String uri = "/users/getAllUsers";
        ResultActions resultActions = this.mockMvc.perform(get(uri));
        assertThat(resultActions.andExpect(status().isOk()));
        MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        CollectionType javaList = objectMapper.getTypeFactory().constructCollectionType(List.class, Usuario.class);
        List<Usuario> users = objectMapper.readValue(response, javaList);
        assertTrue(users != null);
    }

    @Test
    public void getUsuario() throws Exception {
        String uri = "/users/get";
        Usuario user = null;
        if (!this.usuarioRepository.existsById("test_user"))
            user = setUp();
        else user = this.usuarioRepository.findById("test_user").get();
        String token = getToken(user);
        ResultActions resultActions = this.mockMvc.perform(get(uri).header("Authorization", "Bearer " + token));
        assertThat(resultActions.andExpect(status().isOk()));
        MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        Usuario userObtained = objectMapper.readValue(response, Usuario.class);
        assertTrue(userObtained.getUsuario().equals(user.getUsuario()));
        assertTrue(userObtained.getCorreo().equals(user.getCorreo()));
        assertTrue(userObtained.getNombre().equals(user.getNombre()));
        this.usuarioRepository.deleteById(user.getUsuario());
    }

    @Test
    public void replaceUsuario() throws Exception {
        Usuario user = null;
        if (!this.usuarioRepository.existsById("test_user"))
            user = setUp();
        else user = this.usuarioRepository.findById("test_user").get();
        String token = getToken(user);

        String uri = "/users/update";
        String searchUri = "/users/get";
        RequestUsuario requestUsuario = new RequestUsuario(user.getCorreo(), user.getNombre() + "Version 2", user.getUsuario(), "testing");
        ResultActions resultActions = this.mockMvc.perform(put(uri).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper
                        .writeValueAsString(requestUsuario)));
        assertThat(resultActions.andExpect(status().isOk()));
        ResultActions findUpdatedUser = this.mockMvc.perform(get(searchUri)
        .header("Authorization", "Bearer " + token));
        assertThat(findUpdatedUser.andExpect(status().isOk()));
        MvcResult mvcResult = findUpdatedUser.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        Usuario userObtained = objectMapper.readValue(response, Usuario.class);
        assertTrue(userObtained.getUsuario().equals(requestUsuario.getUsuario()));
        assertTrue(userObtained.getCorreo().equals(requestUsuario.getCorreo()));
        assertTrue(userObtained.getNombre().equals(requestUsuario.getNombre()));
        this.usuarioRepository.deleteById(user.getUsuario());
    }

    @Test
    public void addNewUser() throws Exception {
        String uri = "users/addUser";
        RequestUsuario requestUsuario = new RequestUsuario("add_user@test.com", "Test Add User", "addUser", "add_user_password");
        ResultActions resultActions = this.mockMvc.perform(post(uri).content(MediaType.APPLICATION_JSON_VALUE)
        .content(objectMapper.writeValueAsString(requestUsuario)));
        assertThat(resultActions.andExpect(status().isOk()));


    }

    @Test
    public void deleteUsuario() throws Exception {
        String uri = "/users/delete";
        String id = "valid_username";
        ResultActions resultActions = this.mockMvc.perform(delete(uri + id));
        assertThat(resultActions.andExpect(status().isAccepted()));
        id = "pepitoooo";
        resultActions = this.mockMvc.perform(delete(uri + id));
        assertThat(resultActions.andExpect(status().isBadRequest()));
    }

}

