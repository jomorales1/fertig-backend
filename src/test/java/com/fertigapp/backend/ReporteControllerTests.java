package com.fertigapp.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fertigapp.backend.firebase.NotificationSystem;
import com.fertigapp.backend.model.*;
import com.fertigapp.backend.recurrentstrategy.RutinaRecurrentEntityStrategy;
import com.fertigapp.backend.requestmodels.LoginRequest;
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
import org.springframework.test.web.servlet.ResultActions;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = BackendApplication.class)
@AutoConfigureMockMvc
class ReporteControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompletadaService completadaService;

    @Autowired
    private RutinaService rutinaService;

    @Autowired
    private TareaService tareaService;

    @Autowired
    private TareaDeUsuarioService tareaDeUsuarioService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TiempoService tiempoService;

    @Autowired
    private NotificationSystem notificationSystem;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Jackson2ObjectMapperBuilder mapperBuilder = new Jackson2ObjectMapperBuilder();

    private final ObjectMapper objectMapper = mapperBuilder.build();

    Usuario createUser() {
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
        task.setPrioridad(1);
        task.setEtiqueta("Test label");
        task.setEstimacion(4);
        task.setNivel(1);
        task.setHecha(false);
        task.setRecordatorio(20);
        task = this.tareaService.save(task);

        TareaDeUsuario tareaDeUsuario = new TareaDeUsuario();
        tareaDeUsuario.setUsuario(user);
        tareaDeUsuario.setTarea(task);
        tareaDeUsuario.setAdmin(true);
        tareaDeUsuario = this.tareaDeUsuarioService.save(tareaDeUsuario);

        Tiempo tiempo = new Tiempo();
        tiempo.setTareaDeUsuario(tareaDeUsuario);
        tiempo.setFecha(OffsetDateTime.now().minusHours(10));
        tiempo.setInvertido(30);

        return task;
    }

    Rutina setUpRutina(Usuario user) {
        Rutina routine = new Rutina();

        routine.setUsuario(user);
        routine.setNombre("test_routine");
        routine.setDescripcion("test_routine_description");
        routine.setPrioridad(2);
        routine.setEtiqueta("test_routine_tag");
        routine.setDuracion(90);
        routine.setFechaInicio(OffsetDateTime.now().minusWeeks(3));
        routine.setFechaFin(OffsetDateTime.now().plusWeeks(2));
        routine.setRecurrencia("D2");
        routine.setRecordatorio(60);

        Rutina saved =  this.rutinaService.save(routine);

        RutinaRecurrentEntityStrategy rutinaRecurrentEntityStrategy = new RutinaRecurrentEntityStrategy(routine);
        Completada completada = new Completada();
        completada.setRutinaC(routine);
        completada.setFecha(rutinaRecurrentEntityStrategy.findSiguiente(routine.getFechaInicio()));
        completada.setFechaAjustada(null);
        completada.setHecha(true);
        this.completadaService.save(completada);

        return saved;
    }

    @Test
    void contextLoads() {
        assertTrue(true);
    }

    @Test
    void mainTest() throws Exception {
        ArrayList<String> uris = new ArrayList<>();
        uris.add("/report/month");
        uris.add("/report/week");
        uris.add("/report/year");
        uris.add("/graphic/month");
        uris.add("/graphic/week");
        uris.add("/graphic/year");
        for (String uri : uris) {
            reporte(uri);
        }
    }

    void reporte(String uri) throws Exception {
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        String token = getToken(user);
        Tarea task = setUpTarea(user);
        Rutina rutina = setUpRutina(user);

        String fecha = OffsetDateTime.now().toString();

        this.mockMvc.perform(get(uri).header("Authorization", "Bearer " + token)
            .param("fecha", fecha)).andExpect(status().isOk());

        this.notificationSystem.cancelAllScheduledTaskNotifications();
        this.notificationSystem.cancelAllScheduledRoutineNotifications();
        this.completadaService.deleteAllByRutina(rutina);
        this.rutinaService.deleteById(rutina.getId());
        List<Tiempo> tiempos = (List<Tiempo>) this.tiempoService.findAllByUsuarioAndTarea(user, task);
        for (Tiempo tiempo : tiempos) {
            this.tiempoService.deleteById(tiempo.getId());
        }
        this.tareaDeUsuarioService.deleteAllByTarea(task);
        this.tareaService.deleteById(task.getId());
        this.usuarioService.deleteById(user.getUsuario());
    }

}
