package com.fertigapp.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fertigApp.backend.BackendApplication;
import com.fertigApp.backend.model.Tarea;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.requestModels.LoginRequest;
import com.fertigApp.backend.requestModels.RequestTarea;
import com.fertigApp.backend.services.TareaService;
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
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK, classes = BackendApplication.class)
@AutoConfigureMockMvc
public class TareaControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TareaService tareaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Jackson2ObjectMapperBuilder mapperBuilder = new Jackson2ObjectMapperBuilder();

    private final ObjectMapper objectMapper = mapperBuilder.build();

    public Usuario createUser() {
        if (this.usuarioService.findById("test_user").isPresent())
            return this.usuarioService.findById("test_user").get();

        Usuario user = new Usuario();
        user.setUsuario("test_user");
        user.setCorreo("test@email.com");
        user.setNombre("Test User");
        user.setPassword(passwordEncoder.encode("testing"));

        this.usuarioService.save(user);
        return user;
    }

    public String getToken(Usuario user) throws Exception {
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

    public Tarea setUp(Usuario user) {
        if (!usuarioService.findById(user.getUsuario()).isPresent()) {
            this.usuarioService.save(user);
        }

        Tarea task = new Tarea();
        task.setUsuario(user);
        task.setNombre("Test Task");
        task.setDescripcion("Test description");
        task.setPrioridad(1);
        task.setEtiqueta("Test label");
        task.setEstimacion(4);
        task.setNivel(0);
        task.setHecha(false);
        task.setRecordatorio(2);

        return this.tareaService.save(task);
    }

    @Test
    public void contextLoads() {
        assertTrue(true);
    }

    @Test
    @WithMockUser(value = "ADMIN")
    public void getAllTareas() throws Exception {
        String uri = "/tasks";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        Tarea task = setUp(user);

        ResultActions resultActions = this.mockMvc.perform(get(uri));
        assertThat(resultActions.andExpect(status().isOk()));
        MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        CollectionType javaList = objectMapper.getTypeFactory().constructCollectionType(List.class, Tarea.class);
        List<Tarea> tareas = objectMapper.readValue(response, javaList);
        assertTrue(tareas != null);
        this.tareaService.deleteById(task.getId());
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    public void getAllTareasByUsuario() throws Exception {
        String uri = "/tasks/getTasks";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        String token = getToken(user);
        Tarea task = setUp(user);

        ResultActions resultActions = this.mockMvc.perform(get(uri).header("Authorization", "Bearer " + token));
        assertThat(resultActions.andExpect(status().isOk()));
        MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        CollectionType javaList = objectMapper.getTypeFactory().constructCollectionType(List.class, Tarea.class);
        List<Tarea> tasks = objectMapper.readValue(response, javaList);
        assertTrue(tasks != null);
        assertThat(tasks.get(0).getUsuarioT().getUsuario().equals(task.getUsuarioT().getUsuario()));
        assertThat(tasks.get(0).getNombre().equals(task.getNombre()));
        this.tareaService.deleteById(task.getId());
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    public void getTarea() throws Exception {
        String uri = "/tasks/getTask/";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        String token = getToken(user);
        Tarea task = setUp(user);

        ResultActions resultActions = this.mockMvc.perform(get(uri + task.getId()).header("Authorization", "Bearer " + token));
        assertThat(resultActions.andExpect(status().isOk()));
        MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        Tarea obtainedTask = objectMapper.readValue(response, Tarea.class);
        assertThat(obtainedTask.getUsuarioT().getUsuario().equals(user.getUsuario()));
        assertThat(obtainedTask.getNombre().equals(task.getNombre()));

        this.tareaService.deleteById(task.getId());
        this.usuarioService.deleteById(user.getUsuario());
    }

}
