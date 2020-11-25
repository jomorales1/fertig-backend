package com.fertigapp.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fertigapp.backend.model.*;
import com.fertigapp.backend.payload.response.TareaSugeridaResponse;
import com.fertigapp.backend.requestModels.FranjaActivaRequest;
import com.fertigapp.backend.requestModels.LoginRequest;
import com.fertigapp.backend.services.*;
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

import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = BackendApplication.class)
@AutoConfigureMockMvc
public class FranjaActivaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FranjaActivaService franjaActivaService;

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

    Usuario setUpUsuario() {
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

    Tarea setUpTarea(Usuario user) {
        if (usuarioService.findById(user.getUsuario()).isEmpty()) {
            this.usuarioService.save(user);
        }

        Tarea task = new Tarea();
        task.setNombre("Test Task");
        task.setDescripcion("Test description");
        task.setPrioridad(5);
        task.setEtiqueta("Test label");
        task.setEstimacion(4);
        task.setNivel(1);
        task.setHecha(false);
        task.setRecordatorio(2);
        task.setFechaFin(OffsetDateTime.of(2030, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC));
        task = this.tareaService.save(task);

        TareaDeUsuario tareaDeUsuario = new TareaDeUsuario();
        tareaDeUsuario.setUsuario(user);
        tareaDeUsuario.setTarea(task);
        tareaDeUsuario.setAdmin(true);
        this.tareaDeUsuarioService.save(tareaDeUsuario);

        return task;
    }

    Tarea setUpTarea(Usuario usuario, int prioridad, int estimacion, OffsetDateTime fechaFin){
        Tarea task = new Tarea();
        task.setNombre("Test Task");
        task.setDescripcion("Test description");
        task.setPrioridad(prioridad);
        task.setEtiqueta("Test label");
        task.setEstimacion(estimacion);
        task.setNivel(1);
        task.setHecha(false);
        task.setRecordatorio(2);
        task.setFechaFin(fechaFin);
        task = this.tareaService.save(task);

        TareaDeUsuario tareaDeUsuario = new TareaDeUsuario();
        tareaDeUsuario.setUsuario(usuario);
        tareaDeUsuario.setTarea(task);
        tareaDeUsuario.setAdmin(true);
        this.tareaDeUsuarioService.save(tareaDeUsuario);

        return task;
    }

    FranjaActiva setUpFranjaActiva(Usuario usuario){
        FranjaActiva franjaActiva = new FranjaActiva();
        franjaActiva.setUsuarioFL(usuario);
        franjaActiva.setFranjaInicio(OffsetTime.of(0, 0, 0, 0, ZoneOffset.UTC));
        franjaActiva.setFranjaFin(OffsetTime.of(23, 59, 59, 0, ZoneOffset.UTC));
        franjaActiva.setDay(1);

        this.franjaActivaService.save(franjaActiva);

        return franjaActiva;
    }

    FranjaActiva setUpFranjaActiva(Usuario usuario, int day){
        FranjaActiva franjaActiva = new FranjaActiva();
        franjaActiva.setUsuarioFL(usuario);
        franjaActiva.setFranjaInicio(OffsetTime.of(0, 0, 0, 0, ZoneOffset.UTC));
        franjaActiva.setFranjaFin(OffsetTime.of(23, 59, 59, 0, ZoneOffset.UTC));
        franjaActiva.setDay(day);

        this.franjaActivaService.save(franjaActiva);

        return franjaActiva;
    }

    FranjaActiva setUpFranjaActiva(Usuario usuario, int day, OffsetTime fi, OffsetTime ff){
        FranjaActiva franjaActiva = new FranjaActiva();
        franjaActiva.setUsuarioFL(usuario);
        franjaActiva.setFranjaInicio(fi);
        franjaActiva.setFranjaFin(ff);
        franjaActiva.setDay(day);

        this.franjaActivaService.save(franjaActiva);

        return franjaActiva;
    }

    @Test
    void contextLoads() {
        assertTrue(true);
    }

    @Test
    void add() throws Exception {
        String uri = "/franja-activa/add";
        Usuario usuario = setUpUsuario();
        String token = getToken(usuario);

        FranjaActivaRequest franjaActiva = new FranjaActivaRequest();
        franjaActiva.setUsuarioFL(usuario);
        franjaActiva.setFranjaInicio(OffsetTime.of(0, 0, 0, 0, ZoneOffset.UTC));
        franjaActiva.setFranjaFin(OffsetTime.of(23, 59, 59, 0, ZoneOffset.UTC));
        franjaActiva.setDay(1);

        // Valid request -> status 200 expected
        this.mockMvc.perform(post(uri).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(franjaActiva)))
                .andExpect(status().isOk());

        // Invalid request -> status 400 expected
        // FA is present
        this.mockMvc.perform(post(uri).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(franjaActiva)))
                .andExpect(status().isBadRequest());

        this.franjaActivaService.deleteByUser(usuario);
        this.usuarioService.deleteById(usuario.getUsuario());
    }

    @Test
    void getAll() throws Exception {
        String uri = "/franja-activa/franjas";
        Usuario usuario = setUpUsuario();
        String token = getToken(usuario);
        FranjaActiva franjaActiva = setUpFranjaActiva(usuario);

        // Valid request -> status 200 expected
        ResultActions resultActions = this.mockMvc.perform(get(uri).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        CollectionType javaList =  objectMapper.getTypeFactory().constructCollectionType(List.class, FranjaActiva.class);
        List<FranjaActiva> franjaActivas = objectMapper.readValue(response, javaList);

        FranjaActiva obtainedFranja = franjaActivas.get(0);
        assertNotNull(obtainedFranja);
        assertEquals(obtainedFranja.getDay(), franjaActiva.getDay());
        //assertEquals(obtainedFranja.getFranjaInicio(), franjaActiva.getFranjaInicio());
        //assertEquals(obtainedFranja.getFranjaFin(), franjaActiva.getFranjaFin());

        this.franjaActivaService.deleteByUser(usuario);
        this.usuarioService.deleteById(usuario.getUsuario());
    }

    @Test
    void update() throws Exception {
        String uri = "/franja-activa/update/";
        Usuario usuario = setUpUsuario();
        String token = getToken(usuario);
        FranjaActiva franjaActiva = setUpFranjaActiva(usuario);

        Usuario usuario1 = new Usuario();
        usuario1.setUsuario("newUser");
        usuario1.setCorreo("new_user@test.com");
        usuario1.setNombre("New User");
        usuario1.setPassword(passwordEncoder.encode("testing"));
        this.usuarioService.save(usuario1);
        FranjaActiva franjaActiva1 = setUpFranjaActiva(usuario1);

        FranjaActivaRequest franjaActivaRequest = new FranjaActivaRequest();
        franjaActivaRequest.setDay(franjaActiva.getDay());
        franjaActiva.setFranjaInicio(OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC));
        franjaActiva.setFranjaFin(OffsetTime.of(14, 0, 0, 0, ZoneOffset.UTC));

        // Valid request -> status 200 expected
        this.mockMvc.perform(put(uri + franjaActiva.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(franjaActivaRequest)))
                .andExpect(status().isOk());

        // Invalid request -> status 400 expected - Invalid id
        this.mockMvc.perform(put(uri + franjaActiva.getId() + 100)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(franjaActivaRequest)))
                .andExpect(status().isBadRequest());

        // Invalid request -> status 400 expected - No pertenece al usuario
        this.mockMvc.perform(put(uri + franjaActiva1.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(franjaActivaRequest)))
                .andExpect(status().isBadRequest());

        this.franjaActivaService.deleteByUser(usuario1);
        this.usuarioService.deleteById(usuario1.getUsuario());

        this.franjaActivaService.deleteByUser(usuario);
        this.usuarioService.deleteById(usuario.getUsuario());
    }

    @Test
    void deleteById() throws Exception {
        String uri = "/franja-activa/delete/";
        Usuario usuario = setUpUsuario();
        String token = getToken(usuario);
        FranjaActiva franjaActiva = setUpFranjaActiva(usuario);

        // Valid request -> status 200 expected
        this.mockMvc.perform(delete(uri + franjaActiva.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // Invalid request -> status 400 expected
        this.mockMvc.perform(delete(uri + franjaActiva.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        this.franjaActivaService.deleteByUser(usuario);
        this.usuarioService.deleteById(usuario.getUsuario());
    }

    @Test
    void deleteAll() throws Exception {
        String uri = "/franja-activa/delete-all";
        Usuario usuario = setUpUsuario();
        String token = getToken(usuario);
        FranjaActiva franjaActiva = setUpFranjaActiva(usuario);

        // Valid request -> status 200 expected
        this.mockMvc.perform(delete(uri).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        this.franjaActivaService.deleteByUser(usuario);
        this.usuarioService.deleteById(usuario.getUsuario());
    }

    @Test
    void getRecomendations() throws Exception {
        String uri = "/franja-activa/recomendations/";
        Usuario usuario = setUpUsuario();
        String token = getToken(usuario);
        FranjaActiva franjaActiva1 = setUpFranjaActiva(usuario, 1);
        FranjaActiva franjaActiva2 = setUpFranjaActiva(usuario, 2, OffsetTime.of(0, 0, 0, 0, ZoneOffset.UTC), OffsetTime.of(0, 0, 0, 0, ZoneOffset.UTC));
        FranjaActiva franjaActiva3 = setUpFranjaActiva(usuario, 3, OffsetTime.of(23, 59, 59, 0, ZoneOffset.UTC), OffsetTime.of(23, 59, 59, 0, ZoneOffset.UTC));
        Tarea tarea = setUpTarea(usuario, 1, 1, OffsetDateTime.now().plusDays(8));
        Tarea tareaMasProx = setUpTarea(usuario, 1, 1, OffsetDateTime.now().plusDays(3));
        Tarea tareaMasPrio = setUpTarea(usuario, 5, 1, OffsetDateTime.now().plusDays(5));
        Tarea tareaMasEsti = setUpTarea(usuario, 1, 5, OffsetDateTime.now().plusDays(7));

        ResultActions resultActions;
        MvcResult mvcResult;
        String response;
        List<TareaSugeridaResponse> tareasSugeridas;
        CollectionType javaList =  objectMapper.getTypeFactory().constructCollectionType(List.class, TareaSugeridaResponse.class);

        // Valid request -> status 200 expected
        // Actividades sugeridas
        resultActions = this.mockMvc.perform(get(uri + 1)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mvcResult = resultActions.andReturn();
        response = mvcResult.getResponse().getContentAsString();
        tareasSugeridas = objectMapper.readValue(response, javaList);
        assertNotNull(tareasSugeridas);
        assertEquals(tareaMasProx.getId(), tareasSugeridas.get(0).getId());
        assertEquals(tareaMasPrio.getId(), tareasSugeridas.get(1).getId());
        assertEquals(tareaMasEsti.getId(), tareasSugeridas.get(2).getId());

        // Valid request -> status 400 expected
        // Sin actividades sugeridas
        resultActions = this.mockMvc.perform(get(uri + 2)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mvcResult = resultActions.andReturn();
        response = mvcResult.getResponse().getContentAsString();
        tareasSugeridas = objectMapper.readValue(response, javaList);
        assertEquals(0, tareasSugeridas.size());

        // Valid request -> status 400 expected
        // Sin actividades sugeridas
        resultActions = this.mockMvc.perform(get(uri + 3)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mvcResult = resultActions.andReturn();
        response = mvcResult.getResponse().getContentAsString();
        tareasSugeridas = objectMapper.readValue(response, javaList);
        assertEquals(0, tareasSugeridas.size());

        // Valid request -> status 200 expected
        // Actividades sugeridas cuando no hay una franja definida para el dia
        resultActions = this.mockMvc.perform(get(uri + 4)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mvcResult = resultActions.andReturn();
        response = mvcResult.getResponse().getContentAsString();
        tareasSugeridas = objectMapper.readValue(response, javaList);
        assertNotNull(tareasSugeridas);
        assertEquals(tareaMasProx.getId(), tareasSugeridas.get(0).getId());
        assertEquals(tareaMasPrio.getId(), tareasSugeridas.get(1).getId());
        assertEquals(tareaMasEsti.getId(), tareasSugeridas.get(2).getId());

        this.tareaDeUsuarioService.deleteAllByUsuario(usuario);
        this.tareaService.deleteById(tareaMasProx.getId());
        this.tareaService.deleteById(tareaMasPrio.getId());
        this.tareaService.deleteById(tareaMasEsti.getId());

        this.franjaActivaService.deleteByUser(usuario);
        this.usuarioService.deleteById(usuario.getUsuario());
    }
}
