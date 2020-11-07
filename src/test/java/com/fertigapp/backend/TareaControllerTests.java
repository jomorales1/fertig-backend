package com.fertigapp.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fertigApp.backend.BackendApplication;
import com.fertigApp.backend.model.Tarea;
import com.fertigApp.backend.model.TareaDeUsuario;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.requestModels.LoginRequest;
import com.fertigApp.backend.requestModels.RequestTarea;
import com.fertigApp.backend.services.TareaDeUsuarioService;
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
import java.util.Optional;

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
    private TareaDeUsuarioService tareaDeUsuarioService;

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
        task.setNombre("Test Task");
        task.setDescripcion("Test description");
        task.setPrioridad(1);
        task.setEtiqueta("Test label");
        task.setEstimacion(4);
        task.setNivel(1);
        task.setHecha(false);
        task.setRecordatorio(2);
        task.setTiempoInvertido(0);
        task = this.tareaService.save(task);

        TareaDeUsuario tareaDeUsuario = new TareaDeUsuario();
        tareaDeUsuario.setUsuario(user);
        tareaDeUsuario.setTarea(task);
        tareaDeUsuario.setAdmin(true);
        this.tareaDeUsuarioService.save(tareaDeUsuario);

        return task;
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
        this.tareaDeUsuarioService.deleteAllByTarea(task);
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
        assertEquals(tasks.get(0).getNombre(), task.getNombre());
        this.tareaDeUsuarioService.deleteAllByTarea(tasks.get(0));
        this.tareaService.deleteById(tasks.get(0).getId());
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
        assertEquals(obtainedTask.getNombre(), task.getNombre());

        this.mockMvc.perform(get(uri + (task.getId() + 1)).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        this.tareaDeUsuarioService.deleteAllByTarea(task);
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
        requestTarea.setNombre(task.getNombre() + " v2");
        requestTarea.setDescripcion(task.getDescripcion());
        requestTarea.setPrioridad(task.getPrioridad());
        requestTarea.setEtiqueta(task.getEtiqueta());
        requestTarea.setEstimacion(task.getEstimacion());
        requestTarea.setHecha(task.getHecha());
        requestTarea.setRecordatorio(task.getRecordatorio());
        requestTarea.setTiempoInvertido(task.getTiempoInvertido());

        // Valid request -> status 200 expected
        ResultActions resultActions = this.mockMvc.perform(put(uri + task.getId()).header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(requestTarea)))
                .andExpect(status().isOk());
        MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        Tarea obtainedTask = objectMapper.readValue(response, Tarea.class);
        assertNotNull(obtainedTask);
        assertEquals(obtainedTask.getId(), task.getId());
        assertEquals(obtainedTask.getNombre(), requestTarea.getNombre());
        assertEquals(1,obtainedTask.getNivel());

        this.mockMvc.perform(put(uri + (task.getId() + 1)).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(requestTarea)))
                .andExpect(status().isBadRequest());

        Usuario usuario = new Usuario();
        usuario.setUsuario("usuario");
        usuario.setCorreo("correo@test.com");
        usuario.setNombre("Usuario de prueba");
        usuario.setPassword(passwordEncoder.encode("testing"));
        this.usuarioService.save(usuario);

        Tarea tarea = new Tarea();
        tarea.setNombre("Test Task");
        tarea.setDescripcion("Test description");
        tarea.setPrioridad(1);
        tarea.setEtiqueta("Test label");
        tarea.setEstimacion(4);
        tarea.setNivel(2);
        tarea.setHecha(false);
        tarea.setRecordatorio(2);
        tarea.setTiempoInvertido(0);
        tarea = this.tareaService.save(tarea);

        TareaDeUsuario tareaDeUsuario = new TareaDeUsuario();
        tareaDeUsuario.setUsuario(usuario);
        tareaDeUsuario.setTarea(tarea);
        tareaDeUsuario.setAdmin(true);
        this.tareaDeUsuarioService.save(tareaDeUsuario);

        this.mockMvc.perform(put(uri + tarea.getId()).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(requestTarea)))
                .andExpect(status().isBadRequest());

        this.tareaDeUsuarioService.deleteAllByTarea(tarea);
        this.tareaDeUsuarioService.deleteAllByTarea(obtainedTask);
        this.tareaService.deleteById(tarea.getId());
        this.tareaService.deleteById(obtainedTask.getId());
        this.usuarioService.deleteById(usuario.getUsuario());
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
        Optional<Tarea> optionalTarea = this.tareaService.findById(task.getId());
        optionalTarea.ifPresent(tarea -> assertTrue(tarea.isHecha()));

        this.mockMvc.perform(patch(uri + task.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        Optional<Tarea> optionalTarea1 = this.tareaService.findById(task.getId());
        optionalTarea1.ifPresent(tarea -> assertFalse(tarea.getHecha()));

        this.mockMvc.perform(patch(uri + (task.getId() + 1)).header("Authorization", "Bearer " + token))
            .andExpect(status().isBadRequest());

        Usuario usuario = new Usuario();
        usuario.setUsuario("usuario");
        usuario.setCorreo("correo@test.com");
        usuario.setNombre("Usuario de prueba");
        usuario.setPassword(passwordEncoder.encode("testing"));
        this.usuarioService.save(usuario);

        Tarea tarea = new Tarea();
        tarea.setNombre("Test Task");
        tarea.setDescripcion("Test description");
        tarea.setPrioridad(1);
        tarea.setEtiqueta("Test label");
        tarea.setEstimacion(4);
        tarea.setNivel(2);
        tarea.setHecha(false);
        tarea.setRecordatorio(2);
        tarea.setTiempoInvertido(0);
        tarea = this.tareaService.save(tarea);

        TareaDeUsuario tareaDeUsuario = new TareaDeUsuario();
        tareaDeUsuario.setUsuario(usuario);
        tareaDeUsuario.setTarea(tarea);
        tareaDeUsuario.setAdmin(true);
        this.tareaDeUsuarioService.save(tareaDeUsuario);

        this.mockMvc.perform(patch(uri + tarea.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        this.tareaDeUsuarioService.deleteAllByTarea(tarea);
        this.tareaDeUsuarioService.deleteAllByTarea(optionalTarea.orElse(null));
        this.tareaService.deleteById(tarea.getId());
        this.tareaService.deleteById(task.getId());
        this.usuarioService.deleteById(usuario.getUsuario());
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
        requestTarea.setNombre("Test Task");
        requestTarea.setDescripcion("Test description");
        requestTarea.setPrioridad(1);
        requestTarea.setEtiqueta("Test label");
        requestTarea.setEstimacion(4);
        requestTarea.setHecha(false);
        requestTarea.setRecordatorio(2);
        requestTarea.setTiempoInvertido(0);

        this.mockMvc.perform(post(uri).header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(requestTarea)))
                .andExpect(status().isCreated());

        List<Tarea> tasks = (List<Tarea>) this.tareaService.findAll();
        for (Tarea task : tasks) {
            this.tareaDeUsuarioService.deleteAllByTarea(task);
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

        this.mockMvc.perform(delete(uri + task.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        Usuario usuario = new Usuario();
        usuario.setUsuario("usuario");
        usuario.setCorreo("correo@test.com");
        usuario.setNombre("Usuario de prueba");
        usuario.setPassword(passwordEncoder.encode("testing"));
        this.usuarioService.save(usuario);

        Tarea tarea = new Tarea();
        tarea.setNombre("Test Task");
        tarea.setDescripcion("Test description");
        tarea.setPrioridad(1);
        tarea.setEtiqueta("Test label");
        tarea.setEstimacion(4);
        tarea.setNivel(2);
        tarea.setHecha(false);
        tarea.setRecordatorio(2);
        tarea.setTiempoInvertido(0);
        tarea = this.tareaService.save(tarea);

        TareaDeUsuario tareaDeUsuario = new TareaDeUsuario();
        tareaDeUsuario.setUsuario(usuario);
        tareaDeUsuario.setTarea(tarea);
        tareaDeUsuario.setAdmin(true);
        this.tareaDeUsuarioService.save(tareaDeUsuario);

        this.mockMvc.perform(delete(uri + tarea.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        TareaDeUsuario tareaDeUsuario1 = new TareaDeUsuario();
        tareaDeUsuario1.setUsuario(user);
        tareaDeUsuario1.setTarea(tarea);
        tareaDeUsuario1.setAdmin(false);
        this.tareaDeUsuarioService.save(tareaDeUsuario1);

        this.mockMvc.perform(delete(uri + tarea.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        this.tareaDeUsuarioService.deleteAllByTarea(tarea);
        this.tareaService.deleteById(tarea.getId());
        this.usuarioService.deleteById(usuario.getUsuario());
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void getTaskOwners() throws Exception {
        String uri = "/tasks/getOwners/";
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
        CollectionType javaList = objectMapper.getTypeFactory().constructCollectionType(List.class, Usuario.class);
        List<Usuario> owners = objectMapper.readValue(response, javaList);
        assertNotNull(owners);
        assertEquals(owners.get(0).getUsuario(), user.getUsuario());

        this.mockMvc.perform(get(uri + (task.getId() + 1)).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        Usuario usuario = new Usuario();
        usuario.setUsuario("usuario");
        usuario.setCorreo("correo@test.com");
        usuario.setNombre("Usuario de prueba");
        usuario.setPassword(passwordEncoder.encode("testing"));
        this.usuarioService.save(usuario);

        Tarea tarea = new Tarea();
        tarea.setNombre("Test Task");
        tarea.setDescripcion("Test description");
        tarea.setPrioridad(1);
        tarea.setEtiqueta("Test label");
        tarea.setEstimacion(4);
        tarea.setNivel(2);
        tarea.setHecha(false);
        tarea.setRecordatorio(2);
        tarea.setTiempoInvertido(0);
        tarea = this.tareaService.save(tarea);

        TareaDeUsuario tareaDeUsuario = new TareaDeUsuario();
        tareaDeUsuario.setUsuario(usuario);
        tareaDeUsuario.setTarea(tarea);
        tareaDeUsuario.setAdmin(true);
        this.tareaDeUsuarioService.save(tareaDeUsuario);

        this.mockMvc.perform(get(uri + tarea.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        this.tareaDeUsuarioService.deleteAllByTarea(tarea);
        this.tareaDeUsuarioService.deleteAllByTarea(task);
        this.tareaService.deleteById(tarea.getId());
        this.tareaService.deleteById(task.getId());
        this.usuarioService.deleteById(usuario.getUsuario());
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void addTaskAdmin() throws Exception {
        String uri = "/tasks/addAdmin/";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        String token = getToken(user);
        Tarea task = setUp(user);

        Usuario newAdmin = new Usuario();
        newAdmin.setUsuario("newAdmin");
        newAdmin.setNombre("New Admin");
        newAdmin.setCorreo("new_admin@testing.com");
        newAdmin.setPassword(passwordEncoder.encode("testing"));
        this.usuarioService.save(newAdmin);

        this.mockMvc.perform(post(uri + (task.getId() + 1) + "/" + newAdmin.getUsuario())
            .header("Authorization", "Bearer " + token)).andExpect(status().isBadRequest());

        this.mockMvc.perform(post(uri + task.getId() + "/" + newAdmin.getUsuario() + "a")
            .header("Authorization", "Bearer " + token)).andExpect(status().isBadRequest());

        this.mockMvc.perform(post(uri + task.getId() + "/" + newAdmin.getUsuario())
                .header("Authorization", "Bearer " + token)).andExpect(status().isBadRequest());

        Tarea tarea = new Tarea();
        tarea.setNombre("Test Task");
        tarea.setDescripcion("Test description");
        tarea.setPrioridad(1);
        tarea.setEtiqueta("Test label");
        tarea.setEstimacion(4);
        tarea.setNivel(1);
        tarea.setHecha(false);
        tarea.setRecordatorio(2);
        tarea.setTiempoInvertido(0);
        tarea = this.tareaService.save(tarea);

        this.mockMvc.perform(post(uri + tarea.getId() + "/" + newAdmin.getUsuario())
                .header("Authorization", "Bearer " + token)).andExpect(status().isBadRequest());

        TareaDeUsuario relation = new TareaDeUsuario();
        relation.setUsuario(user);
        relation.setTarea(tarea);
        relation.setAdmin(false);
        this.tareaDeUsuarioService.save(relation);

        TareaDeUsuario relation1 = new TareaDeUsuario();
        relation1.setUsuario(newAdmin);
        relation1.setTarea(tarea);
        relation1.setAdmin(false);
        this.tareaDeUsuarioService.save(relation1);

        this.mockMvc.perform(post(uri + tarea.getId() + "/" + newAdmin.getUsuario())
                .header("Authorization", "Bearer " + token)).andExpect(status().isBadRequest());

        TareaDeUsuario tareaDeUsuario = new TareaDeUsuario();
        tareaDeUsuario.setUsuario(newAdmin);
        tareaDeUsuario.setTarea(task);
        tareaDeUsuario.setAdmin(false);
        this.tareaDeUsuarioService.save(tareaDeUsuario);

        this.mockMvc.perform(post(uri + tarea.getId() + "/" + newAdmin.getUsuario())
                .header("Authorization", "Bearer " + token)).andExpect(status().isBadRequest());

        this.mockMvc.perform(post(uri + task.getId() + "/" + newAdmin.getUsuario())
                .header("Authorization", "Bearer " + token)).andExpect(status().isOk());

        Optional<TareaDeUsuario> optionalTareaDeUsuario =  this.tareaDeUsuarioService.findByUsuarioAndTarea(newAdmin, task);
        assertTrue(optionalTareaDeUsuario.isPresent());
        assertTrue(optionalTareaDeUsuario.get().isAdmin());

        this.tareaDeUsuarioService.deleteAllByTarea(tarea);
        this.tareaDeUsuarioService.deleteAllByTarea(task);
        this.tareaService.deleteById(tarea.getId());
        this.tareaService.deleteById(task.getId());
        this.usuarioService.deleteById(newAdmin.getUsuario());
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void addTaskOwner() throws Exception {
        String uri = "/tasks/addOwner/";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        String token = getToken(user);
        Tarea task = setUp(user);

        Usuario newOwner = new Usuario();
        newOwner.setUsuario("newAdmin");
        newOwner.setNombre("New Admin");
        newOwner.setCorreo("new_admin@testing.com");
        newOwner.setPassword(passwordEncoder.encode("testing"));
        this.usuarioService.save(newOwner);

        this.mockMvc.perform(post(uri + (task.getId() + 1) + "/" + newOwner.getUsuario())
                .header("Authorization", "Bearer " + token)).andExpect(status().isBadRequest());

        this.mockMvc.perform(post(uri + task.getId() + "/" + newOwner.getUsuario() + "a")
                .header("Authorization", "Bearer " + token)).andExpect(status().isBadRequest());

        Tarea tarea = new Tarea();
        tarea.setNombre("Test Task");
        tarea.setDescripcion("Test description");
        tarea.setPrioridad(1);
        tarea.setEtiqueta("Test label");
        tarea.setEstimacion(4);
        tarea.setNivel(1);
        tarea.setHecha(false);
        tarea.setRecordatorio(2);
        tarea.setTiempoInvertido(0);
        tarea = this.tareaService.save(tarea);

        this.mockMvc.perform(post(uri + tarea.getId() + "/" + newOwner.getUsuario())
                .header("Authorization", "Bearer " + token)).andExpect(status().isBadRequest());

        TareaDeUsuario tareaDeUsuario = new TareaDeUsuario();
        tareaDeUsuario.setUsuario(user);
        tareaDeUsuario.setTarea(tarea);
        tareaDeUsuario.setAdmin(false);
        this.tareaDeUsuarioService.save(tareaDeUsuario);

        this.mockMvc.perform(post(uri + tarea.getId() + "/" + newOwner.getUsuario())
                .header("Authorization", "Bearer " + token)).andExpect(status().isBadRequest());

        this.mockMvc.perform(post(uri + task.getId() + "/" + newOwner.getUsuario())
                .header("Authorization", "Bearer " + token)).andExpect(status().isOk());

        this.tareaDeUsuarioService.deleteAllByTarea(task);
        this.tareaDeUsuarioService.deleteAllByTarea(tarea);
        this.tareaService.deleteById(task.getId());
        this.tareaService.deleteById(tarea.getId());
        this.usuarioService.deleteById(newOwner.getUsuario());
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void addSubTask() throws Exception {
        String uri = "/tasks/addSubTask/";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        String token = getToken(user);
        Tarea task = setUp(user);

        RequestTarea requestTarea = new RequestTarea();
        requestTarea.setNombre("Test Task");
        requestTarea.setDescripcion("Test description");
        requestTarea.setPrioridad(1);
        requestTarea.setEtiqueta("Test label");
        requestTarea.setEstimacion(4);
        requestTarea.setHecha(false);
        requestTarea.setRecordatorio(2);
        requestTarea.setTiempoInvertido(0);

        this.mockMvc.perform(post(uri + (task.getId() + 1)).header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(requestTarea)))
                .andExpect(status().isBadRequest());

        Tarea tarea = new Tarea();
        tarea.setNombre("Test Task");
        tarea.setDescripcion("Test description");
        tarea.setPrioridad(1);
        tarea.setEtiqueta("Test label");
        tarea.setEstimacion(4);
        tarea.setNivel(2);
        tarea.setHecha(false);
        tarea.setRecordatorio(2);
        tarea.setTiempoInvertido(0);
        tarea = this.tareaService.save(tarea);

        this.mockMvc.perform(post(uri + tarea.getId()).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(requestTarea)))
                .andExpect(status().isBadRequest());

        this.tareaService.deleteById(tarea.getId());

        Tarea tarea2 = new Tarea();
        tarea2.setNombre("Test Task");
        tarea2.setDescripcion("Test description");
        tarea2.setPrioridad(1);
        tarea2.setEtiqueta("Test label");
        tarea2.setEstimacion(4);
        tarea2.setNivel(3);
        tarea2.setHecha(false);
        tarea2.setRecordatorio(2);
        tarea2.setTiempoInvertido(0);
        tarea2 = this.tareaService.save(tarea2);
        TareaDeUsuario tareaDeUsuario = new TareaDeUsuario();
        tareaDeUsuario.setUsuario(user);
        tareaDeUsuario.setTarea(tarea2);
        tareaDeUsuario.setAdmin(true);
        this.tareaDeUsuarioService.save(tareaDeUsuario);

        this.mockMvc.perform(post(uri + tarea2.getId()).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(requestTarea)))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(post(uri + task.getId()).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(requestTarea)))
                .andExpect(status().isCreated());

        this.tareaDeUsuarioService.deleteAllByTarea(task);
        this.tareaDeUsuarioService.deleteAllByTarea(tarea2);
        this.tareaService.deleteById(task.getId());
        this.tareaService.deleteById(tarea2.getId());
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void updateSubtask() throws Exception {
        String uri = "/tasks/updateSubtask/";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        String token = getToken(user);
        Tarea task = setUp(user);

        Tarea subtarea = new Tarea();
        subtarea.setNombre("Test Subtask A");
        subtarea.setDescripcion("Test description");
        subtarea.setPrioridad(1);
        subtarea.setEtiqueta("Test label");
        subtarea.setEstimacion(4);
        subtarea.setHecha(false);
        subtarea.setNivel(2);
        subtarea.setRecordatorio(2);
        subtarea.setTiempoInvertido(0);
        subtarea.setPadre(task);
        task.addSubtarea(subtarea);
        this.tareaService.save(task);

        RequestTarea requestTarea = new RequestTarea();
        requestTarea.setNombre(subtarea.getNombre() + " v2");
        requestTarea.setDescripcion(subtarea.getDescripcion());
        requestTarea.setPrioridad(subtarea.getPrioridad());
        requestTarea.setEtiqueta(subtarea.getEtiqueta());
        requestTarea.setEstimacion(subtarea.getEstimacion());
        requestTarea.setHecha(subtarea.getHecha());
        requestTarea.setRecordatorio(subtarea.getRecordatorio());
        requestTarea.setTiempoInvertido(subtarea.getTiempoInvertido());

        this.mockMvc.perform(put(uri + (task.getId() + 2)).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(requestTarea)))
                .andExpect(status().isBadRequest());

        Usuario newUser = new Usuario();
        newUser.setUsuario("newUser");
        newUser.setCorreo("new_user@test.com");
        newUser.setNombre("New User");
        newUser.setPassword(passwordEncoder.encode("testing"));
        this.usuarioService.save(newUser);
        Tarea newTarea = setUp(newUser);

        Tarea newSubtarea = new Tarea();
        newSubtarea.setNombre("Test Subtask B");
        newSubtarea.setDescripcion("Test description");
        newSubtarea.setPrioridad(1);
        newSubtarea.setEtiqueta("Test label");
        newSubtarea.setEstimacion(4);
        newSubtarea.setHecha(false);
        newSubtarea.setNivel(2);
        newSubtarea.setRecordatorio(2);
        newSubtarea.setTiempoInvertido(0);
        newSubtarea.setPadre(newTarea);
        newTarea.addSubtarea(newSubtarea);
        this.tareaService.save(newTarea);

        List<Tarea> tareas = (List<Tarea>) this.tareaService.findAll();
        int indexA = -1, indexB = -1;
        for (Tarea tarea : tareas) {
            if (tarea.getNombre().equals(subtarea.getNombre()))
                indexA = tarea.getId();
            if (tarea.getNombre().equals(newSubtarea.getNombre()))
                indexB = tarea.getId();
        }

        assertEquals(4, tareas.size());

        this.mockMvc.perform(put(uri + indexB).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(requestTarea)))
                .andExpect(status().isBadRequest());

        assertEquals(newSubtarea.getNombre(), this.tareaService.findById(indexB).get().getNombre());

        this.mockMvc.perform(put(uri + indexA).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(requestTarea)))
                .andExpect(status().isOk());

        this.tareaDeUsuarioService.deleteAllByTarea(newTarea);
        this.tareaDeUsuarioService.deleteAllByTarea(task);
        this.tareaService.deleteById(newTarea.getId());
        this.tareaService.deleteById(task.getId());
        this.usuarioService.deleteById(newUser.getUsuario());
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void checkSubtask() throws Exception {
        String uri = "/tasks/checkSubtask/";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        String token = getToken(user);
        Tarea task = setUp(user);

        Tarea subtarea = new Tarea();
        subtarea.setNombre("Test Subtask A");
        subtarea.setDescripcion("Test description");
        subtarea.setPrioridad(1);
        subtarea.setEtiqueta("Test label");
        subtarea.setEstimacion(4);
        subtarea.setHecha(false);
        subtarea.setNivel(2);
        subtarea.setRecordatorio(2);
        subtarea.setTiempoInvertido(0);
        subtarea.setPadre(task);
        task.addSubtarea(subtarea);
        this.tareaService.save(task);

        this.mockMvc.perform(put(uri + (task.getId() + 2)).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        Usuario newUser = new Usuario();
        newUser.setUsuario("newUser");
        newUser.setCorreo("new_user@test.com");
        newUser.setNombre("New User");
        newUser.setPassword(passwordEncoder.encode("testing"));
        this.usuarioService.save(newUser);
        Tarea newTarea = setUp(newUser);

        Tarea newSubtarea = new Tarea();
        newSubtarea.setNombre("Test Subtask B");
        newSubtarea.setDescripcion("Test description");
        newSubtarea.setPrioridad(1);
        newSubtarea.setEtiqueta("Test label");
        newSubtarea.setEstimacion(4);
        newSubtarea.setHecha(false);
        newSubtarea.setNivel(2);
        newSubtarea.setRecordatorio(2);
        newSubtarea.setTiempoInvertido(0);
        newSubtarea.setPadre(newTarea);
        newTarea.addSubtarea(newSubtarea);
        this.tareaService.save(newTarea);

        List<Tarea> tareas = (List<Tarea>) this.tareaService.findAll();
        int indexA = -1, indexB = -1;
        for (Tarea tarea : tareas) {
            if (tarea.getNombre().equals(subtarea.getNombre()))
                indexA = tarea.getId();
            if (tarea.getNombre().equals(newSubtarea.getNombre()))
                indexB = tarea.getId();
        }

        this.mockMvc.perform(put(uri + indexB).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(put(uri + indexA).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        subtarea = this.tareaService.findById(indexA).get();
        assertTrue(subtarea.getHecha());

        this.mockMvc.perform(put(uri + (indexA)).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        subtarea = this.tareaService.findById(indexA).get();
        assertFalse(subtarea.getHecha());

        this.tareaDeUsuarioService.deleteAllByTarea(task);
        this.tareaDeUsuarioService.deleteAllByTarea(newTarea);
        this.tareaService.deleteById(task.getId());
        this.tareaService.deleteById(newTarea.getId());
        this.usuarioService.deleteById(newUser.getUsuario());
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void deleteSubtask() throws Exception {
        String uri = "/tasks/deleteSubtask/";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        String token = getToken(user);

        Tarea task = setUp(user);
        Tarea subtarea = new Tarea();
        subtarea.setNombre("Test Subtask A");
        subtarea.setDescripcion("Test description");
        subtarea.setPrioridad(1);
        subtarea.setEtiqueta("Test label");
        subtarea.setEstimacion(4);
        subtarea.setHecha(false);
        subtarea.setNivel(2);
        subtarea.setRecordatorio(2);
        subtarea.setTiempoInvertido(0);
        subtarea.setPadre(task);
        task.addSubtarea(subtarea);
        this.tareaService.save(task);

        this.mockMvc.perform(delete(uri + (task.getId() + 2)).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        Usuario newUser = new Usuario();
        newUser.setUsuario("newUser");
        newUser.setCorreo("new_user@test.com");
        newUser.setNombre("New User");
        newUser.setPassword(passwordEncoder.encode("testing"));
        this.usuarioService.save(newUser);
        Tarea newTarea = setUp(newUser);

        Tarea newSubtarea = new Tarea();
        newSubtarea.setNombre("Test Subtask B");
        newSubtarea.setDescripcion("Test description");
        newSubtarea.setPrioridad(1);
        newSubtarea.setEtiqueta("Test label");
        newSubtarea.setEstimacion(4);
        newSubtarea.setHecha(false);
        newSubtarea.setNivel(2);
        newSubtarea.setRecordatorio(2);
        newSubtarea.setTiempoInvertido(0);
        newSubtarea.setPadre(newTarea);
        newTarea.addSubtarea(newSubtarea);
        this.tareaService.save(newTarea);

        List<Tarea> tareas = (List<Tarea>) this.tareaService.findAll();
        int indexA = -1, indexB = -1;
        for (Tarea tarea : tareas) {
            if (tarea.getNombre().equals(subtarea.getNombre()))
                indexA = tarea.getId();
            if (tarea.getNombre().equals(newSubtarea.getNombre()))
                indexB = tarea.getId();
        }

        this.mockMvc.perform(delete(uri + indexB).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(delete(uri + indexA).header("Authorization", "Bearer " + token))
                .andExpect(status().isAccepted());

        this.tareaDeUsuarioService.deleteAllByTarea(newTarea);
        this.tareaDeUsuarioService.deleteAllByTarea(task);
        this.tareaService.deleteById(newTarea.getId());
        this.tareaService.deleteById(task.getId());
        this.usuarioService.deleteById(newUser.getUsuario());
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void increaseInvestedTime() throws Exception {
        String uri = "/tasks/increaseTime/";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        String token = getToken(user);
        Tarea task = setUp(user);

        this.mockMvc.perform(put(uri + (task.getId() + 1) + "/" + "10").header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        Tarea tarea = new Tarea();
        tarea.setNombre("Test Task");
        tarea.setDescripcion("Test description");
        tarea.setPrioridad(1);
        tarea.setEtiqueta("Test label");
        tarea.setEstimacion(4);
        tarea.setNivel(2);
        tarea.setHecha(false);
        tarea.setRecordatorio(2);
        tarea.setTiempoInvertido(0);
        tarea = this.tareaService.save(tarea);

        this.mockMvc.perform(put(uri + tarea.getId() + "/" + "10").header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(put(uri + task.getId() + "/" + "10").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        task = this.tareaService.findById(task.getId()).get();
        assertEquals(10, task.getTiempoInvertido());

        this.tareaDeUsuarioService.deleteAllByTarea(task);
        this.tareaService.deleteById(tarea.getId());
        this.tareaService.deleteById(task.getId());
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void copyTask() throws Exception {
        String uri = "/tasks/copyTask/";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        String token = getToken(user);
        Tarea task = setUp(user);

        Usuario usuario = new Usuario();
        usuario.setUsuario("usuario");
        usuario.setCorreo("correo@test.com");
        usuario.setNombre("Usuario de prueba");
        usuario.setPassword(passwordEncoder.encode("testing"));
        this.usuarioService.save(usuario);

        Tarea tarea = new Tarea();
        tarea.setNombre("Test Task");
        tarea.setDescripcion("Test description");
        tarea.setPrioridad(1);
        tarea.setEtiqueta("Test label");
        tarea.setEstimacion(4);
        tarea.setNivel(1);
        tarea.setHecha(false);
        tarea.setRecordatorio(2);
        tarea.setTiempoInvertido(0);
        tarea = this.tareaService.save(tarea);

        Tarea subtask1 = new Tarea();
        subtask1.setNombre("Test Subtask 1");
        subtask1.setDescripcion("Test description");
        subtask1.setPrioridad(1);
        subtask1.setEtiqueta("Test label");
        subtask1.setEstimacion(4);
        subtask1.setNivel(2);
        subtask1.setHecha(false);
        subtask1.setRecordatorio(2);
        subtask1.setTiempoInvertido(0);
        subtask1.setPadre(tarea);
        tarea.addSubtarea(subtask1);
        tarea = this.tareaService.save(tarea);

        Tarea subtask2 = new Tarea();
        subtask2.setNombre("Test Subtask 2");
        subtask2.setDescripcion("Test description");
        subtask2.setPrioridad(1);
        subtask2.setEtiqueta("Test label");
        subtask2.setEstimacion(4);
        subtask2.setNivel(3);
        subtask2.setHecha(false);
        subtask2.setRecordatorio(2);
        subtask2.setTiempoInvertido(0);
        subtask2.setPadre(tarea.getSubtareas().get(0));
        tarea.getSubtareas().get(0).addSubtarea(subtask2);
        this.tareaService.save(tarea.getSubtareas().get(0));

        TareaDeUsuario tareaDeUsuario = new TareaDeUsuario();
        tareaDeUsuario.setUsuario(usuario);
        tareaDeUsuario.setTarea(tarea);
        tareaDeUsuario.setAdmin(true);
        this.tareaDeUsuarioService.save(tareaDeUsuario);

        this.mockMvc.perform(post(uri + (tarea.getId() + 1)).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(post(uri + subtask1.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(post(uri + task.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(post(uri + tarea.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        this.tareaDeUsuarioService.deleteAllByUsuario(user);
        this.tareaDeUsuarioService.deleteAllByUsuario(usuario);
        List<Tarea> tareas = (List<Tarea>) this.tareaService.findAll();
        for (Tarea tarea1 : tareas) {
            this.tareaDeUsuarioService.deleteAllByTarea(tarea1);
            if (this.tareaService.findById(tarea1.getId()).isPresent())
                this.tareaService.deleteById(tarea1.getId());
        }
        this.usuarioService.deleteById(usuario.getUsuario());
        this.usuarioService.deleteById(user.getUsuario());
    }

}
