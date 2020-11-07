package com.fertigapp.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fertigApp.backend.BackendApplication;
import com.fertigApp.backend.model.Completada;
import com.fertigApp.backend.model.Rutina;
import com.fertigApp.backend.model.Tarea;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.payload.response.AbstractRecurrenteResponse;
import com.fertigApp.backend.payload.response.RecurrenteResponse;
import com.fertigApp.backend.requestModels.LoginRequest;
import com.fertigApp.backend.requestModels.RequestRutina;
import com.fertigApp.backend.requestModels.RequestTarea;
import com.fertigApp.backend.services.CompletadaService;
import com.fertigApp.backend.services.RutinaService;
import com.fertigApp.backend.services.TareaService;
import com.fertigApp.backend.services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = BackendApplication.class)
@AutoConfigureMockMvc
class RutinaControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CompletadaService completadaService;

    @Autowired
    private TareaService tareaService;

    @Autowired
    private RutinaService rutinaService;

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
        user.setRutinas(new ArrayList<>());

        this.usuarioService.save(user);
        return user;
    }

    Rutina setUpRutina(Usuario user) {
        Rutina routine = new Rutina();

        routine.setUsuario(user);
        routine.setNombre("test_routine");
        routine.setDescripcion("test_routine_description");
        routine.setPrioridad(2);
        routine.setEtiqueta("test_routine_tag");
        routine.setDuracion(90);
        routine.setFechaInicio(LocalDateTime.now().minusWeeks(3));
        routine.setFechaFin(LocalDateTime.now().plusWeeks(2));
        routine.setRecurrencia("D2");
        routine.setRecordatorio(60);

        Completada completada = new Completada();
        completada.setRutinaC(routine);
        completada.setFecha(
                AbstractRecurrenteResponse.findSiguiente(routine.getFechaInicio(),
                        routine.getFechaFin(),
                        routine.getRecurrencia()));
        completada.setFechaAjustada(null);
        completada.setHecha(false);
        Rutina saved = this.rutinaService.save(routine);

        this.completadaService.save(completada);
        return saved;
    }

    Rutina setUpRutina(Usuario user, String recurrencia) {
        Rutina routine = new Rutina();

        routine.setUsuario(user);
        routine.setNombre("test_routine");
        routine.setDescripcion("test_routine_description");
        routine.setPrioridad(2);
        routine.setEtiqueta("test_routine_tag");
        routine.setDuracion(45);
        routine.setFechaInicio(LocalDateTime.now().minusWeeks(3));
        routine.setFechaFin(LocalDateTime.now().plusWeeks(2));
        routine.setRecurrencia(recurrencia);
        routine.setRecordatorio(60);

        Completada completada = new Completada();
        completada.setRutinaC(routine);
        completada.setFecha(
                AbstractRecurrenteResponse.findSiguiente(routine.getFechaInicio(),
                        routine.getFechaFin(),
                        routine.getRecurrencia()));
        completada.setFechaAjustada(null);
        completada.setHecha(false);
        Rutina saved = this.rutinaService.save(routine);

        this.completadaService.save(completada);
        return saved;
    }

    Rutina setUpRutina(Usuario user, String recurrencia, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        Rutina routine = new Rutina();

        routine.setUsuario(user);
        routine.setNombre("test_routine");
        routine.setDescripcion("test_routine_description");
        routine.setPrioridad(2);
        routine.setEtiqueta("test_routine_tag");
        routine.setDuracion(45);
        routine.setFechaInicio(fechaInicio);
        routine.setFechaFin(fechaFin);
        routine.setRecurrencia(recurrencia);
        routine.setRecordatorio(60);

        Completada completada = new Completada();
        completada.setRutinaC(routine);
        completada.setFecha(
                AbstractRecurrenteResponse.findSiguiente(routine.getFechaInicio(),
                        routine.getFechaFin(),
                        routine.getRecurrencia()));
        completada.setFechaAjustada(null);
        completada.setHecha(false);
        Rutina saved = this.rutinaService.save(routine);

        this.completadaService.save(completada);
        return saved;
    }

    Rutina setUpRutina(Usuario user, String recurrencia, LocalDateTime fechaInicio, LocalDateTime fechaFin, LocalTime franjaI, LocalTime franjaF) {
        Rutina routine = new Rutina();

        routine.setUsuario(user);
        routine.setNombre("test_routine");
        routine.setDescripcion("test_routine_description");
        routine.setPrioridad(2);
        routine.setEtiqueta("test_routine_tag");
        routine.setDuracion(45);
        routine.setFechaInicio(fechaInicio);
        routine.setFechaFin(fechaFin);
        routine.setRecurrencia(recurrencia);
        routine.setRecordatorio(60);
        routine.setFranjaInicio(franjaI);
        routine.setFranjaFin(franjaF);

        Completada completada = new Completada();
        completada.setRutinaC(routine);
        completada.setFecha(
                AbstractRecurrenteResponse.findSiguiente(routine.getFechaInicio(),
                        routine.getFechaFin(),
                        routine.getRecurrencia(),
                        routine.getDuracion(),
                        routine.getFranjaInicio(),
                        routine.getFranjaFin()));
        completada.setFechaAjustada(null);
        completada.setHecha(false);
        Rutina saved = this.rutinaService.save(routine);

        this.completadaService.save(completada);
        return saved;
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
    void contextLoads(){
        assertTrue(true);
    }

    @Test
    @WithMockUser(value = "ADMIN")
    void getAllRutinas() throws Exception {
        String uri = "/routines";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = setUpUsuario();
        else user = this.usuarioService.findById("test_user").get();
        Rutina rutina = setUpRutina(user);

        ResultActions resultActions = this.mockMvc.perform(get(uri)).andExpect(status().isOk());
        MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        CollectionType javaList = objectMapper.getTypeFactory().constructCollectionType(List.class, Rutina.class);
        List<Rutina> rutinas = objectMapper.readValue(response, javaList);
        assertNotNull(rutinas);
        assertEquals(rutinas.get(0).getId(), rutina.getId());
        assertEquals(rutinas.get(0).getNombre(), rutina.getNombre());
        assertEquals(rutinas.get(0).getUsuario().getUsuario(), user.getUsuario());

        this.completadaService.deleteAllByRutina(rutina);
        this.rutinaService.deleteById(rutina.getId());
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void getAllRutinasByUsuario() throws Exception {
        String uri = "/routines/getRoutines";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = setUpUsuario();
        else user = this.usuarioService.findById("test_user").get();
        String token = getToken(user);

        Rutina routine;
        String recurrencia;
        LocalDateTime fechaInicio, fechaFin = LocalDateTime.of(2021,1,31,12,0);
        LocalTime franjaIncio = LocalTime.of(7,0);
        LocalTime franjaFin  = LocalTime.of(13,0);
        RecurrenteResponse obtainedRoutine;

        ResultActions resultActions;
        MvcResult mvcResult;
        String response;

        CollectionType javaList =  objectMapper.getTypeFactory().constructCollectionType(List.class, RecurrenteResponse.class);
        List<RecurrenteResponse> routines;

        //RECURRENCIA HORARIA CADA 13 HORAS ENTRE 7AM Y 13AM

        recurrencia = "H13";
        fechaInicio = LocalDateTime.of(2021,1,1,14,0);
        routine = setUpRutina(user, recurrencia, fechaInicio, fechaFin, franjaIncio, franjaFin);

        resultActions = this.mockMvc.perform(get(uri).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mvcResult = resultActions.andReturn();
        response = mvcResult.getResponse().getContentAsString();

        routines = objectMapper.readValue(response, javaList);

        obtainedRoutine = routines.get(0);
        assertNotNull(obtainedRoutine);
        assertEquals(obtainedRoutine.getNombre(), routine.getNombre());
        assertEquals(obtainedRoutine.getDescripcion(), routine.getDescripcion());
        assertEquals(obtainedRoutine.getFecha(), LocalDateTime.of(2021,1,4,7,0));

        this.completadaService.deleteAllByRutina(routine);
        this.rutinaService.deleteById(routine.getId());

        //RECURRENCIA DIARIA
        recurrencia = "D2";
        fechaInicio = LocalDateTime.of(2021,1,1,12,0);
        routine = setUpRutina(user, recurrencia, fechaInicio, fechaFin);

        resultActions = this.mockMvc.perform(get(uri).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mvcResult = resultActions.andReturn();
        response = mvcResult.getResponse().getContentAsString();

        routines = objectMapper.readValue(response, javaList);

        obtainedRoutine = routines.get(0);
        assertNotNull(obtainedRoutine);
        assertEquals(obtainedRoutine.getNombre(), routine.getNombre());
        assertEquals(obtainedRoutine.getDescripcion(), routine.getDescripcion());
        assertEquals(obtainedRoutine.getFecha(), LocalDateTime.of(2021,1,1,12,0));

        this.completadaService.deleteAllByRutina(routine);
        this.rutinaService.deleteById(routine.getId());

        //RECURRENCIA SEMANAL
        recurrencia = "S2";
        fechaInicio = LocalDateTime.of(2021,1,1,12,0);
        routine = setUpRutina(user, recurrencia, fechaInicio, fechaFin);

        resultActions = this.mockMvc.perform(get(uri).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mvcResult = resultActions.andReturn();
        response = mvcResult.getResponse().getContentAsString();

        routines = objectMapper.readValue(response, javaList);

        obtainedRoutine = routines.get(0);
        assertNotNull(obtainedRoutine);
        assertEquals(obtainedRoutine.getNombre(), routine.getNombre());
        assertEquals(obtainedRoutine.getDescripcion(), routine.getDescripcion());
        assertEquals(obtainedRoutine.getFecha(), LocalDateTime.of(2021,1,1,12,0));

        this.completadaService.deleteAllByRutina(routine);
        this.rutinaService.deleteById(routine.getId());

        //RECURRENCIA MENSUAL
        recurrencia = "M1";
        fechaInicio = LocalDateTime.of(2020,12,1,12,0);
        routine = setUpRutina(user, recurrencia, fechaInicio, fechaFin);

        resultActions = this.mockMvc.perform(get(uri).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mvcResult = resultActions.andReturn();
        response = mvcResult.getResponse().getContentAsString();

        routines = objectMapper.readValue(response, javaList);

        obtainedRoutine = routines.get(0);
        assertNotNull(obtainedRoutine);
        assertEquals(obtainedRoutine.getNombre(), routine.getNombre());
        assertEquals(obtainedRoutine.getDescripcion(), routine.getDescripcion());
        assertEquals(obtainedRoutine.getFecha(), LocalDateTime.of(2020,12,1,12,0));

        this.completadaService.deleteAllByRutina(routine);
        this.rutinaService.deleteById(routine.getId());

        //RECURRENCIA ESPECIAL - LUNES Y MIERCOLES CADA 2 SEMANAS
        recurrencia = "E5.S2";
        fechaInicio = LocalDateTime.of(2021,1,1,12,0);
        routine = setUpRutina(user, recurrencia, fechaInicio, fechaFin);

        resultActions = this.mockMvc.perform(get(uri).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mvcResult = resultActions.andReturn();
        response = mvcResult.getResponse().getContentAsString();

        routines = objectMapper.readValue(response, javaList);

        obtainedRoutine = routines.get(0);
        assertNotNull(obtainedRoutine);
        assertEquals(obtainedRoutine.getNombre(), routine.getNombre());
        assertEquals(obtainedRoutine.getDescripcion(), routine.getDescripcion());
        assertEquals(obtainedRoutine.getFecha(), LocalDateTime.of(2021,1,11,12,0));

        this.completadaService.deleteAllByRutina(routine);
        this.rutinaService.deleteById(routine.getId());
    }

    @Test
    void getRutina() throws Exception {
        String uri = "/routines/getRoutine";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = setUpUsuario();
        else user = this.usuarioService.findById("test_user").get();
        Rutina rutina = setUpRutina(user);
        String token = getToken(user);

        ResultActions resultActions = this.mockMvc.perform(get(uri+"/"+rutina.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        Rutina rutinaObtained = objectMapper.readValue(response, Rutina.class);
        assertEquals(rutinaObtained.getNombre(), rutina.getNombre());
        assertEquals(rutinaObtained.getDescripcion(), rutina.getDescripcion());
        assertEquals(rutinaObtained.getPrioridad(),rutina.getPrioridad());
        assertEquals(rutinaObtained.getEtiqueta(), rutina.getEtiqueta());
        assertEquals(rutinaObtained.getDuracion(), rutina.getDuracion());
        assertTrue(rutinaObtained.getFechaInicio().compareTo(rutina.getFechaInicio()) < 10);
        assertTrue(rutinaObtained.getFechaFin().compareTo(rutina.getFechaFin()) < 10);
        assertEquals(rutinaObtained.getRecurrencia(), rutina.getRecurrencia());
        assertEquals(rutinaObtained.getRecordatorio(), rutina.getRecordatorio());

        this.mockMvc.perform(get(uri + "/" + (rutina.getId() + 1)).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        Usuario newUser = new Usuario();
        newUser.setUsuario("newUser");
        newUser.setCorreo("new_user@test.com");
        newUser.setNombre("New User");
        newUser.setPassword(passwordEncoder.encode("testing"));
        this.usuarioService.save(newUser);
        Rutina newRutina = setUpRutina(newUser);

        this.mockMvc.perform(get(uri + "/" + newRutina.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        this.completadaService.deleteAllByRutina(rutina);
        this.rutinaService.deleteById(rutina.getId());
        this.completadaService.deleteAllByRutina(newRutina);
        this.rutinaService.deleteById(newRutina.getId());
        this.usuarioService.deleteById(newUser.getUsuario());
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void replaceRutina() throws Exception {
        String uri = "/routines/updateRoutine/";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = setUpUsuario();
        else user = this.usuarioService.findById("test_user").get();
        Rutina rutina = setUpRutina(user);
        String token = getToken(user);

        RequestRutina requestRutina = new RequestRutina();
        requestRutina.setUsuarioR(user);
        requestRutina.setNombre(rutina.getNombre() + " v2");
        requestRutina.setDescripcion(rutina.getDescripcion());
        requestRutina.setPrioridad(rutina.getPrioridad());
        requestRutina.setEtiqueta(rutina.getEtiqueta());
        requestRutina.setDuracion(rutina.getDuracion());
        requestRutina.setFechaInicio(requestRutina.getFechaInicio());
        requestRutina.setFechaFin(rutina.getFechaFin());
        requestRutina.setRecurrencia(rutina.getRecurrencia());
        requestRutina.setRecordatorio(rutina.getRecordatorio());
        requestRutina.setCompletadas(rutina.getCompletadas());

        ResultActions resultActions = this.mockMvc.perform(put(uri + rutina.getId()).header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(requestRutina)))
                .andExpect(status().isOk());
        MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        Rutina rutinaObtained = this.objectMapper.readValue(response, Rutina.class);
        assertNotNull(rutinaObtained);
        assertEquals(rutinaObtained.getUsuario().getUsuario(), requestRutina.getUsuarioR().getUsuario());
        assertEquals(rutinaObtained.getNombre(), requestRutina.getNombre());
        assertEquals(rutinaObtained.getDescripcion(), requestRutina.getDescripcion());

        this.mockMvc.perform(put(uri + (rutina.getId() + 1)).header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(requestRutina)))
                .andExpect(status().isBadRequest());

        Usuario newUser = new Usuario();
        newUser.setUsuario("newUser");
        newUser.setCorreo("new_user@test.com");
        newUser.setNombre("New User");
        newUser.setPassword(passwordEncoder.encode("testing"));
        this.usuarioService.save(newUser);
        Rutina newRutina = setUpRutina(newUser);

        this.mockMvc.perform(put(uri + newRutina.getId()).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(requestRutina)))
                .andExpect(status().isBadRequest());

        this.completadaService.deleteAllByRutina(rutina);
        this.rutinaService.deleteById(rutina.getId());
        this.completadaService.deleteAllByRutina(newRutina);
        this.rutinaService.deleteById(newRutina.getId());
        this.usuarioService.deleteById(newUser.getUsuario());
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void addNewRutina() throws Exception {
        String uri = "/routines/addRoutine";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = setUpUsuario();
        else user = this.usuarioService.findById("test_user").get();
        String token = getToken(user);

        RequestRutina requestRutina = new RequestRutina();
        requestRutina.setUsuarioR(user);
        requestRutina.setNombre("test_routine");
        requestRutina.setDescripcion("test_routine_description");
        requestRutina.setPrioridad(2);
        requestRutina.setEtiqueta("test_routine_tag");
        requestRutina.setDuracion(90);
        requestRutina.setFechaInicio(LocalDateTime.now().minusWeeks(3));
        requestRutina.setFechaFin(LocalDateTime.now().plusWeeks(2));
        requestRutina.setRecurrencia("D2");
        requestRutina.setRecordatorio(60);
        requestRutina.setCompletadas(new ArrayList<>());

        this.mockMvc.perform(post(uri).header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(requestRutina)))
                .andExpect(status().isCreated());

        List<Rutina> rutinas = (List<Rutina>) this.rutinaService.findAll();
        assertFalse(rutinas.isEmpty());
        assertEquals(rutinas.get(0).getUsuario().getUsuario(), requestRutina.getUsuarioR().getUsuario());
        assertEquals(rutinas.get(0).getNombre(), requestRutina.getNombre());
        assertEquals(rutinas.get(0).getDescripcion(), requestRutina.getDescripcion());

        for (Rutina rutina : rutinas) {
            this.completadaService.deleteAllByRutina(rutina);
            this.rutinaService.deleteById(rutina.getId());
        }
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void addSubtask() throws Exception {
        String uri = "/routines/addSubtask/";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = setUpUsuario();
        else user = this.usuarioService.findById("test_user").get();
        Rutina rutina = setUpRutina(user);
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

        this.mockMvc.perform(post(uri + (rutina.getId() + 1)).header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(requestTarea)))
                .andExpect(status().isBadRequest());

        Usuario usuario = new Usuario();
        usuario.setUsuario("newUser");
        usuario.setCorreo("new_user@test.com");
        usuario.setNombre("New User");
        usuario.setPassword(passwordEncoder.encode("testing"));
        this.usuarioService.save(usuario);

        Rutina routine = new Rutina();
        routine.setUsuario(usuario);
        routine.setNombre("test_routine");
        routine.setDescripcion("test_routine_description");
        routine.setPrioridad(2);
        routine.setEtiqueta("test_routine_tag");
        routine.setDuracion(90);
        routine.setFechaInicio(LocalDateTime.now().minusWeeks(3));
        routine.setFechaFin(LocalDateTime.now().plusWeeks(2));
        routine.setRecurrencia("D2");
        routine.setRecordatorio(60);
        routine = this.rutinaService.save(routine);

        this.mockMvc.perform(post(uri + routine.getId()).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(requestTarea)))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(post(uri + rutina.getId()).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(requestTarea)))
                .andExpect(status().isCreated());

        this.completadaService.deleteAllByRutina(rutina);
        this.rutinaService.deleteById(rutina.getId());
        this.completadaService.deleteAllByRutina(routine);
        this.rutinaService.deleteById(routine.getId());
        this.usuarioService.deleteById(usuario.getUsuario());
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void updateSubtask() throws Exception {
        String uri = "/routines/updateSubtask/";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = setUpUsuario();
        else user = this.usuarioService.findById("test_user").get();
        Rutina rutina = setUpRutina(user);
        String token = getToken(user);

        Tarea subtarea = new Tarea();
        subtarea.setNombre("Test Subtask L2");
        subtarea.setDescripcion("Test description");
        subtarea.setPrioridad(1);
        subtarea.setEtiqueta("Test label");
        subtarea.setEstimacion(4);
        subtarea.setHecha(false);
        subtarea.setNivel(2);
        subtarea.setRecordatorio(2);
        subtarea.setTiempoInvertido(0);
        subtarea.setRutinaT(rutina);
        subtarea = this.tareaService.save(subtarea);
        this.rutinaService.save(rutina);

        Tarea subtarea1 = new Tarea();
        subtarea1.setNombre("Test Subtask L3");
        subtarea1.setDescripcion("Test description");
        subtarea1.setPrioridad(1);
        subtarea1.setEtiqueta("Test label");
        subtarea1.setEstimacion(4);
        subtarea1.setHecha(false);
        subtarea1.setNivel(3);
        subtarea1.setRecordatorio(2);
        subtarea1.setTiempoInvertido(0);
        subtarea1.setPadre(subtarea);
        subtarea.addSubtarea(subtarea1);
        subtarea = this.tareaService.save(subtarea);

        RequestTarea requestTarea = new RequestTarea();
        requestTarea.setNombre(subtarea.getNombre() + " v2");
        requestTarea.setDescripcion(subtarea.getDescripcion());
        requestTarea.setPrioridad(subtarea.getPrioridad());
        requestTarea.setEtiqueta(subtarea.getEtiqueta());
        requestTarea.setEstimacion(subtarea.getEstimacion());
        requestTarea.setHecha(subtarea.getHecha());
        requestTarea.setRecordatorio(subtarea.getRecordatorio());
        requestTarea.setTiempoInvertido(subtarea.getTiempoInvertido());

        this.mockMvc.perform(put(uri + (subtarea.getId() + 2)).header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(requestTarea)))
                .andExpect(status().isBadRequest());

        Usuario newUser = new Usuario();
        newUser.setUsuario("newUser");
        newUser.setCorreo("new_user@test.com");
        newUser.setNombre("New User");
        newUser.setPassword(passwordEncoder.encode("testing"));
        this.usuarioService.save(newUser);
        Rutina newRutina = setUpRutina(newUser);

        Tarea newSubtarea = new Tarea();
        newSubtarea.setNombre("Test Subtask L2");
        newSubtarea.setDescripcion("Test description");
        newSubtarea.setPrioridad(1);
        newSubtarea.setEtiqueta("Test label");
        newSubtarea.setEstimacion(4);
        newSubtarea.setHecha(false);
        newSubtarea.setNivel(2);
        newSubtarea.setRecordatorio(2);
        newSubtarea.setTiempoInvertido(0);
        newSubtarea.setRutinaT(newRutina);
        newSubtarea = this.tareaService.save(newSubtarea);
        this.rutinaService.save(rutina);

        this.mockMvc.perform(put(uri + newSubtarea.getId()).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(requestTarea)))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(put(uri + subtarea.getId()).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(requestTarea)))
                .andExpect(status().isOk());

        this.tareaService.deleteById(subtarea.getId());
        this.tareaService.deleteById(newSubtarea.getId());
        this.completadaService.deleteAllByRutina(rutina);
        this.completadaService.deleteAllByRutina(newRutina);
        this.rutinaService.deleteById(rutina.getId());
        this.rutinaService.deleteById(newRutina.getId());
        this.usuarioService.deleteById(newUser.getUsuario());
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void checkSubtask() throws Exception {
        String uri = "/routines/checkSubtask/";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = setUpUsuario();
        else user = this.usuarioService.findById("test_user").get();
        Rutina rutina = setUpRutina(user);
        String token = getToken(user);

        Tarea subtarea = new Tarea();
        subtarea.setNombre("Test Subtask L2");
        subtarea.setDescripcion("Test description");
        subtarea.setPrioridad(1);
        subtarea.setEtiqueta("Test label");
        subtarea.setEstimacion(4);
        subtarea.setHecha(false);
        subtarea.setNivel(2);
        subtarea.setRecordatorio(2);
        subtarea.setTiempoInvertido(0);
        subtarea.setRutinaT(rutina);
        subtarea = this.tareaService.save(subtarea);
        this.rutinaService.save(rutina);

        this.mockMvc.perform(put(uri + (subtarea.getId() + 1)).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        Usuario newUser = new Usuario();
        newUser.setUsuario("newUser");
        newUser.setCorreo("new_user@test.com");
        newUser.setNombre("New User");
        newUser.setPassword(passwordEncoder.encode("testing"));
        this.usuarioService.save(newUser);
        Rutina newRutina = setUpRutina(newUser);

        Tarea newSubtarea = new Tarea();
        newSubtarea.setNombre("Test Subtask L2");
        newSubtarea.setDescripcion("Test description");
        newSubtarea.setPrioridad(1);
        newSubtarea.setEtiqueta("Test label");
        newSubtarea.setEstimacion(4);
        newSubtarea.setHecha(false);
        newSubtarea.setNivel(2);
        newSubtarea.setRecordatorio(2);
        newSubtarea.setTiempoInvertido(0);
        newSubtarea.setRutinaT(newRutina);
        newSubtarea = this.tareaService.save(newSubtarea);
        this.rutinaService.save(rutina);

        this.mockMvc.perform(put(uri + newSubtarea.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(put(uri + subtarea.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        subtarea = this.tareaService.findById(subtarea.getId()).get();
        assertTrue(subtarea.getHecha());

        this.mockMvc.perform(put(uri + subtarea.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        subtarea = this.tareaService.findById(subtarea.getId()).get();
        assertFalse(subtarea.getHecha());

        this.tareaService.deleteById(subtarea.getId());
        this.tareaService.deleteById(newSubtarea.getId());
        this.completadaService.deleteAllByRutina(rutina);
        this.rutinaService.deleteById(rutina.getId());
        this.completadaService.deleteAllByRutina(newRutina);
        this.rutinaService.deleteById(newRutina.getId());
        this.usuarioService.deleteById(newUser.getUsuario());
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void deleteSubtask() throws Exception {
        String uri = "/routines/deleteSubtask/";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = setUpUsuario();
        else user = this.usuarioService.findById("test_user").get();
        Rutina rutina = setUpRutina(user);
        String token = getToken(user);

        Tarea subtarea = new Tarea();
        subtarea.setNombre("Test Subtask L2");
        subtarea.setDescripcion("Test description");
        subtarea.setPrioridad(1);
        subtarea.setEtiqueta("Test label");
        subtarea.setEstimacion(4);
        subtarea.setHecha(false);
        subtarea.setNivel(2);
        subtarea.setRecordatorio(2);
        subtarea.setTiempoInvertido(0);
        subtarea.setRutinaT(rutina);
        subtarea = this.tareaService.save(subtarea);
        this.rutinaService.save(rutina);

        this.mockMvc.perform(delete(uri + (subtarea.getId() + 1)).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        Usuario newUser = new Usuario();
        newUser.setUsuario("newUser");
        newUser.setCorreo("new_user@test.com");
        newUser.setNombre("New User");
        newUser.setPassword(passwordEncoder.encode("testing"));
        this.usuarioService.save(newUser);
        Rutina newRutina = setUpRutina(newUser);

        Tarea newSubtarea = new Tarea();
        newSubtarea.setNombre("Test Subtask L2");
        newSubtarea.setDescripcion("Test description");
        newSubtarea.setPrioridad(1);
        newSubtarea.setEtiqueta("Test label");
        newSubtarea.setEstimacion(4);
        newSubtarea.setHecha(false);
        newSubtarea.setNivel(2);
        newSubtarea.setRecordatorio(2);
        newSubtarea.setTiempoInvertido(0);
        newSubtarea.setRutinaT(newRutina);
        newSubtarea = this.tareaService.save(newSubtarea);
        this.rutinaService.save(rutina);

        this.mockMvc.perform(delete(uri + newSubtarea.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(delete(uri + subtarea.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        this.tareaService.deleteById(newSubtarea.getId());
        this.completadaService.deleteAllByRutina(rutina);
        this.rutinaService.deleteById(rutina.getId());
        this.completadaService.deleteAllByRutina(newRutina);
        this.rutinaService.deleteById(newRutina.getId());
        this.usuarioService.deleteById(newUser.getUsuario());
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void deleteRutina() throws Exception {
        String uri = "/routines/deleteRoutine/";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = setUpUsuario();
        else user = this.usuarioService.findById("test_user").get();
        Rutina rutina = setUpRutina(user);
        String token = getToken(user);

        this.mockMvc.perform(delete(uri + rutina.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isAccepted());

        this.mockMvc.perform(delete(uri + rutina.getId() + 1).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        Usuario newUser = new Usuario();
        newUser.setUsuario("newUser");
        newUser.setCorreo("new_user@test.com");
        newUser.setNombre("New User");
        newUser.setPassword(passwordEncoder.encode("testing"));
        this.usuarioService.save(newUser);
        Rutina newRutina = setUpRutina(newUser);

        this.mockMvc.perform(delete(uri + newRutina.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        this.completadaService.deleteAllByRutina(newRutina);
        this.rutinaService.deleteById(newRutina.getId());
        this.usuarioService.deleteById(newUser.getUsuario());
        this.usuarioService.deleteById(user.getUsuario());
    }

}
