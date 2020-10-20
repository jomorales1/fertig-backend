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
class TareaControllerTests {

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
        if (usuarioService.findById(user.getUsuario()).isEmpty()) {
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
    void contextLoads() {
        assertTrue(true);
    }

    @Test
    @WithMockUser(value = "ADMIN")
    void getAllTareas() throws Exception {
        String uri = "/tasks";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        Tarea task = setUp(user);

        ResultActions resultActions = this.mockMvc.perform(get(uri)).andExpect(status().isOk());
        MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        CollectionType javaList = objectMapper.getTypeFactory().constructCollectionType(List.class, Tarea.class);
        List<Tarea> tasks = objectMapper.readValue(response, javaList);
        assertNotNull(tasks);
        this.tareaService.deleteById(task.getId());
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void getAllTareasByUsuario() throws Exception {
        String uri = "/tasks/getTasks";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        String token = getToken(user);
        Tarea task = setUp(user);

        ResultActions resultActions = this.mockMvc.perform(get(uri).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        CollectionType javaList = objectMapper.getTypeFactory().constructCollectionType(List.class, Tarea.class);
        List<Tarea> tasks = objectMapper.readValue(response, javaList);
        assertNotNull(tasks);
        assertEquals(tasks.get(0).getUsuarioT().getUsuario(), task.getUsuarioT().getUsuario());
        assertEquals(tasks.get(0).getNombre(), task.getNombre());
        for (Tarea tarea : tasks) {
            this.tareaService.deleteById(tarea.getId());
        }
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void getTarea() throws Exception {
        String uri = "/tasks/getTask/";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        String token = getToken(user);
        Tarea task = setUp(user);

        ResultActions resultActions = this.mockMvc.perform(get(uri + task.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        Tarea obtainedTask = objectMapper.readValue(response, Tarea.class);
        assertEquals(obtainedTask.getUsuarioT().getUsuario(), user.getUsuario());
        assertEquals(obtainedTask.getNombre(), task.getNombre());

        this.tareaService.deleteById(task.getId());
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void replaceTarea() throws Exception {
        String uri = "/tasks/updateTask/";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        String token = getToken(user);
        Tarea task = setUp(user);

        RequestTarea requestTarea = new RequestTarea();
        requestTarea.setUsuarioT(user);
        requestTarea.setNombre(task.getNombre() + " v2");
        requestTarea.setDescripcion(task.getDescripcion());
        requestTarea.setPrioridad(task.getPrioridad());
        requestTarea.setEtiqueta(task.getEtiqueta());
        requestTarea.setEstimacion(task.getEstimacion());
        requestTarea.setNivel(task.getNivel());
        requestTarea.setHecha(task.getHecha());
        requestTarea.setRecordatorio(task.getRecordatorio());

        // Valid request -> status 200 expected
        ResultActions resultActions = this.mockMvc.perform(put(uri + task.getId()).header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(requestTarea)))
                .andExpect(status().isOk());
        MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        Tarea obtainedTask = objectMapper.readValue(response, Tarea.class);
        assertNotNull(obtainedTask);
        assertEquals(obtainedTask.getUsuarioT().getUsuario(), requestTarea.getUsuarioT().getUsuario());
        assertEquals(obtainedTask.getNombre(), requestTarea.getNombre());

        this.mockMvc.perform(put(uri + (task.getId() + 1)).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        this.tareaService.deleteById(task.getId());
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void checkTarea() throws Exception {
        String uri = "/tasks/checkTask/";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        String token = getToken(user);
        Tarea task = setUp(user);

        this.mockMvc.perform(patch(uri + task.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        if (this.tareaService.findById(task.getId()).isPresent()) {
            Tarea obtainedTask = this.tareaService.findById(task.getId()).get();
            assertEquals(obtainedTask.getUsuarioT().getUsuario(), task.getUsuarioT().getUsuario());
            assertTrue(obtainedTask.getHecha());
        }

        this.mockMvc.perform(patch(uri + (task.getId() + 1)).header("Authorization", "Bearer " + token))
            .andExpect(status().isBadRequest());

        this.tareaService.deleteById(task.getId());
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void addNewTarea() throws Exception {
        String uri = "/tasks/addTask";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        String token = getToken(user);

        RequestTarea requestTarea = new RequestTarea();
        requestTarea.setUsuarioT(user);
        requestTarea.setNombre("Test Task");
        requestTarea.setDescripcion("Test description");
        requestTarea.setPrioridad(1);
        requestTarea.setEtiqueta("Test label");
        requestTarea.setEstimacion(4);
        requestTarea.setNivel(0);
        requestTarea.setHecha(false);
        requestTarea.setRecordatorio(2);

        this.mockMvc.perform(post(uri).header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(requestTarea)))
                .andExpect(status().isCreated());

        List<Tarea> tasks = (List<Tarea>) this.tareaService.findAll();
        for (Tarea task : tasks) {
            this.tareaService.deleteById(task.getId());
        }
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void deleteTarea() throws Exception {
        String uri = "/tasks/deleteTask/";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        String token = getToken(user);
        Tarea task = setUp(user);

        this.mockMvc.perform(delete(uri + task.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        this.usuarioService.deleteById(user.getUsuario());
    }

}
