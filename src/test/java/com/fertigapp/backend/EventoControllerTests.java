package com.fertigapp.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fertigApp.backend.BackendApplication;
import com.fertigApp.backend.model.Evento;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.payload.response.EventoRepeticionesResponse;
import com.fertigApp.backend.payload.response.RecurrenteResponse;
import com.fertigApp.backend.requestModels.LoginRequest;
import com.fertigApp.backend.requestModels.RequestEvento;
import com.fertigApp.backend.services.EventoService;
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
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = BackendApplication.class)
@AutoConfigureMockMvc
class EventoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventoService eventoService;

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

    Evento setUpEvento(Usuario user) {
        if (usuarioService.findById(user.getUsuario()).isEmpty()) {
            this.usuarioService.save(user);
        }

        Evento evento = new Evento();
        evento.setUsuario(user);
        evento.setNombre("Test Event");
        evento.setDescripcion("Event description");
        evento.setPrioridad(1);
        evento.setEtiqueta("Event label");
        evento.setDuracion(1);
        evento.setRecurrencia("D2");
        evento.setRecordatorio(1);
        evento.setFechaInicio(LocalDateTime.now().minusWeeks(3));
        evento.setFechaFin(LocalDateTime.now().plusWeeks(2));

        return this.eventoService.save(evento);
    }

    Evento setUpEvento(Usuario user, String recurrencia, LocalDateTime fechaInicio, LocalDateTime fechaFin){
        if (usuarioService.findById(user.getUsuario()).isEmpty()) {
            this.usuarioService.save(user);
        }

        Evento evento = new Evento();
        evento.setUsuario(user);
        evento.setNombre("Test Event");
        evento.setDescripcion("Event description");
        evento.setPrioridad(1);
        evento.setEtiqueta("Event label");
        evento.setDuracion(1);
        evento.setRecurrencia(recurrencia);
        evento.setRecordatorio(1);
        evento.setFechaInicio(fechaInicio);
        evento.setFechaFin(fechaFin);

        return this.eventoService.save(evento);
    }

    @Test
    void contextLoads() {
        assertTrue(true);
    }

    @Test
    @WithMockUser(value = "ADMIN")
    void getAllEventos() throws Exception {
        String uri = "/events";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = setUpUsuario();
        else user = this.usuarioService.findById("test_user").get();
        Evento event = setUpEvento(user);

        ResultActions resultActions = this.mockMvc.perform(get(uri)).andExpect(status().isOk());
        MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        CollectionType javaList = objectMapper.getTypeFactory().constructCollectionType(List.class, Evento.class);
        List<Evento> events = objectMapper.readValue(response, javaList);
        assertNotNull(events);
        assertEquals(events.get(0).getUsuario().getUsuario(), event.getUsuario().getUsuario());
        assertEquals(events.get(0).getNombre(), event.getNombre());
        assertEquals(events.get(0).getDescripcion(), event.getDescripcion());
        this.eventoService.deleteById(event.getId());
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void getAllEventosByUsuario() throws Exception {
        String uri = "/events/getEvents";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = setUpUsuario();
        else user = this.usuarioService.findById("test_user").get();
        String token = getToken(user);

        Evento event;
        String recurrencia;
        LocalDateTime fechaInicio, fechaFin = LocalDateTime.of(2021,1,31,12,0);
        LocalTime franjaIncio = LocalTime.of(7,0);
        LocalTime franjaFin  = LocalTime.of(13,0);
        RecurrenteResponse obtainedEvent;

        ResultActions resultActions;
        MvcResult mvcResult;
        String response;

        CollectionType javaList =  objectMapper.getTypeFactory().constructCollectionType(List.class, RecurrenteResponse.class);
        List<RecurrenteResponse> events;

        //RECURRENCIA CADA 12 HORAS
        recurrencia = "H12";
        fechaInicio = LocalDateTime.of(2021,1,1,12,0);
        event = setUpEvento(user, recurrencia, fechaInicio, fechaFin);

        resultActions = this.mockMvc.perform(get(uri).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mvcResult = resultActions.andReturn();
        response = mvcResult.getResponse().getContentAsString();

        events = objectMapper.readValue(response, javaList);

        obtainedEvent = events.get(0);
        assertNotNull(obtainedEvent);
        assertEquals(obtainedEvent.getNombre(), event.getNombre());
        assertEquals(obtainedEvent.getDescripcion(), event.getDescripcion());
        assertEquals(obtainedEvent.getFecha(), LocalDateTime.of(2021,1,1,12,0));

        this.eventoService.deleteById(event.getId());

        //RECURRENCIA DIARIA
        recurrencia = "D2";
        fechaInicio = LocalDateTime.of(2021,1,1,12,0);
        event = setUpEvento(user, recurrencia, fechaInicio, fechaFin);

        resultActions = this.mockMvc.perform(get(uri).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mvcResult = resultActions.andReturn();
        response = mvcResult.getResponse().getContentAsString();

        events = objectMapper.readValue(response, javaList);

        obtainedEvent = events.get(0);
        assertNotNull(obtainedEvent);
        assertEquals(obtainedEvent.getNombre(), event.getNombre());
        assertEquals(obtainedEvent.getDescripcion(), event.getDescripcion());
        assertEquals(obtainedEvent.getFecha(), LocalDateTime.of(2021,1,1,12,0));

        this.eventoService.deleteById(event.getId());

        //RECURRENCIA CADA 2 SEMANAS
        recurrencia = "S2";
        fechaInicio = LocalDateTime.of(2021,1,1,12,0);
        event = setUpEvento(user, recurrencia, fechaInicio, fechaFin);

        resultActions = this.mockMvc.perform(get(uri).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mvcResult = resultActions.andReturn();
        response = mvcResult.getResponse().getContentAsString();

        events = objectMapper.readValue(response, javaList);

        obtainedEvent = events.get(0);
        assertNotNull(obtainedEvent);
        assertEquals(obtainedEvent.getNombre(), event.getNombre());
        assertEquals(obtainedEvent.getDescripcion(), event.getDescripcion());
        assertEquals(obtainedEvent.getFecha(), LocalDateTime.of(2021,1,1,12,0));

        this.eventoService.deleteById(event.getId());

        //RECURRENCIA MENSUAL
        recurrencia = "S2";
        fechaInicio = LocalDateTime.of(2021,1,1,12,0);
        event = setUpEvento(user, recurrencia, fechaInicio, fechaFin);

        resultActions = this.mockMvc.perform(get(uri).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mvcResult = resultActions.andReturn();
        response = mvcResult.getResponse().getContentAsString();

        events = objectMapper.readValue(response, javaList);

        obtainedEvent = events.get(0);
        assertNotNull(obtainedEvent);
        assertEquals(obtainedEvent.getNombre(), event.getNombre());
        assertEquals(obtainedEvent.getDescripcion(), event.getDescripcion());
        assertEquals(obtainedEvent.getFecha(), LocalDateTime.of(2021,1,1,12,0));

        this.eventoService.deleteById(event.getId());

        //RECURRENCIA ESPECIAL - LUNES Y MIERCOLES CADA DOS SEMANAS
        recurrencia = "E5.S2";
        fechaInicio = LocalDateTime.of(2021,1,1,12,0);
        event = setUpEvento(user, recurrencia, fechaInicio, fechaFin);

        resultActions = this.mockMvc.perform(get(uri).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mvcResult = resultActions.andReturn();
        response = mvcResult.getResponse().getContentAsString();

        events = objectMapper.readValue(response, javaList);

        obtainedEvent = events.get(0);
        assertNotNull(obtainedEvent);
        assertEquals(obtainedEvent.getNombre(), event.getNombre());
        assertEquals(obtainedEvent.getDescripcion(), event.getDescripcion());
        assertEquals(obtainedEvent.getFecha(), LocalDateTime.of(2021,1,11,12,0));

        this.eventoService.deleteById(event.getId());

        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void getEventsRepetitions() throws  Exception {
        String uri = "/events/getEventsAndRepetitions";
        Usuario user = setUpUsuario();
        String token = getToken(user);

        Evento event;
        String recurrencia;
        LocalDateTime fechaInicio, fechaFin = LocalDateTime.of(2020, 7, 1, 0, 0);
        LinkedList<LocalDateTime> expectedDates = new LinkedList<>();
        EventoRepeticionesResponse obtainedEvent;
        List<LocalDateTime> obtainedDates;

        ResultActions resultActions;
        MvcResult mvcResult;
        String response;

        CollectionType javaList = objectMapper.getTypeFactory().constructCollectionType(List.class, EventoRepeticionesResponse.class);
        List<EventoRepeticionesResponse> events;

        //RECURRENCIA DIARIA
        recurrencia = "D1";
        fechaInicio = LocalDateTime.of(2020, 6, 25, 16, 0);
        event = setUpEvento(user, recurrencia, fechaInicio, fechaFin);

        for(int day = 25; day <= 30; day++){
            expectedDates.add(LocalDateTime.of(2020, 6, day, 16, 0));
        }

        // Valid request -> status 200 expected
        resultActions = this.mockMvc.perform(get(uri).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mvcResult = resultActions.andReturn();
        response = mvcResult.getResponse().getContentAsString();

        events = objectMapper.readValue(response, javaList);

        obtainedEvent = events.get(0);
        assertNotNull(obtainedEvent);
        assertEquals(obtainedEvent.getNombre(), event.getNombre());
        assertEquals(obtainedEvent.getDescripcion(), event.getDescripcion());
        obtainedDates = obtainedEvent.getRepeticiones();

        assertTrue(obtainedDates.size() == expectedDates.size());
        for(LocalDateTime date : expectedDates){
            assertTrue(obtainedDates.contains(date));
        }
        expectedDates.clear();
        this.eventoService.deleteById(event.getId());

        //RECURRENCIA SEMANAL
        recurrencia = "S1";
        fechaInicio = LocalDateTime.of(2020, 6, 2, 16, 0);
        event = setUpEvento(user, recurrencia, fechaInicio, fechaFin);

        for(int day = 2; day <= 30; day+=7){
            expectedDates.add(LocalDateTime.of(2020, 6, day, 16, 0));
        }

        // Valid request -> status 200 expected
        resultActions = this.mockMvc.perform(get(uri).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mvcResult = resultActions.andReturn();
        response = mvcResult.getResponse().getContentAsString();

        events = objectMapper.readValue(response, javaList);

        obtainedEvent = events.get(0);
        assertNotNull(events);
        assertEquals(obtainedEvent.getNombre(), event.getNombre());
        assertEquals(obtainedEvent.getDescripcion(), event.getDescripcion());
        obtainedDates = obtainedEvent.getRepeticiones();

        assertTrue(obtainedDates.size() == expectedDates.size());
        for(LocalDateTime date : expectedDates){
            assertTrue(obtainedDates.contains(date));
        }
        expectedDates.clear();
        this.eventoService.deleteById(event.getId());

        //RECURRENCIA MENSUAL
        recurrencia = "M1";
        fechaInicio = LocalDateTime.of(2020, 1, 28, 16, 0);
        event = setUpEvento(user, recurrencia, fechaInicio, fechaFin);

        for(int month = 1; month <= 6; month++){
            expectedDates.add(LocalDateTime.of(2020, month, 28, 16, 0));
        }

        // Valid request -> status 200 expected
        resultActions = this.mockMvc.perform(get(uri).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mvcResult = resultActions.andReturn();
        response = mvcResult.getResponse().getContentAsString();

        events = objectMapper.readValue(response, javaList);

        obtainedEvent = events.get(0);
        assertNotNull(events);
        assertEquals(obtainedEvent.getNombre(), event.getNombre());
        assertEquals(obtainedEvent.getDescripcion(), event.getDescripcion());
        obtainedDates = obtainedEvent.getRepeticiones();

        assertTrue(obtainedDates.size() == expectedDates.size());
        for(LocalDateTime date : expectedDates){
            assertTrue(obtainedDates.contains(date));
        }
        expectedDates.clear();
        this.eventoService.deleteById(event.getId());

        //RECURRENCIA ANUAL
        recurrencia = "A1";
        fechaInicio = LocalDateTime.of(2016, 6, 30, 16, 0);
        event = setUpEvento(user, recurrencia, fechaInicio, fechaFin);

        for(int year = 2016; year <= 2020; year++){
            expectedDates.add(LocalDateTime.of(year, 6, 30, 16, 0));
        }

        // Valid request -> status 200 expected
        resultActions = this.mockMvc.perform(get(uri).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mvcResult = resultActions.andReturn();
        response = mvcResult.getResponse().getContentAsString();

        events = objectMapper.readValue(response, javaList);

        obtainedEvent = events.get(0);
        assertNotNull(events);
        assertEquals(obtainedEvent.getNombre(), event.getNombre());
        assertEquals(obtainedEvent.getDescripcion(), event.getDescripcion());
        obtainedDates = obtainedEvent.getRepeticiones();

        assertTrue(obtainedDates.size() == expectedDates.size());
        for(LocalDateTime date : expectedDates){
            assertTrue(obtainedDates.contains(date));
        }
        expectedDates.clear();
        this.eventoService.deleteById(event.getId());

        //RECURRENCIA ESPECIAL - LUNES Y MIERCOLES CADA SEMANA
        recurrencia = "E5.S1";
        fechaInicio = LocalDateTime.of(2020, 6, 1, 16, 0);
        event = setUpEvento(user, recurrencia, fechaInicio, fechaFin);

        for(int day = 1; day <= 30; day+=7){
            expectedDates.add(LocalDateTime.of(2020, 6, day, 16, 0));
        }

        for(int day = 3; day <= 30; day+=7){
            expectedDates.add(LocalDateTime.of(2020, 6, day, 16, 0));
        }

        // Valid request -> status 200 expected
        resultActions = this.mockMvc.perform(get(uri).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mvcResult = resultActions.andReturn();
        response = mvcResult.getResponse().getContentAsString();

        events = objectMapper.readValue(response, javaList);

        obtainedEvent = events.get(0);
        assertNotNull(events);
        assertEquals(obtainedEvent.getNombre(), event.getNombre());
        assertEquals(obtainedEvent.getDescripcion(), event.getDescripcion());
        obtainedDates = obtainedEvent.getRepeticiones();

        assertTrue(obtainedDates.size() == expectedDates.size());
        for(LocalDateTime date : expectedDates){
            assertTrue(obtainedDates.contains(date));
        }
        expectedDates.clear();
        this.eventoService.deleteById(event.getId());

        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void getEvento() throws Exception {
        String uri = "/events/getEvent/";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = setUpUsuario();
        else user = this.usuarioService.findById("test_user").get();
        Evento event = setUpEvento(user);
        String token = getToken(user);

        ResultActions resultActions = this.mockMvc.perform(get(uri + event.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        Evento obtainedEvent = this.objectMapper.readValue(response, Evento.class);
        assertNotNull(obtainedEvent);

        resultActions = this.mockMvc.perform(get(uri + (event.getId() + 1)).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mvcResult = resultActions.andReturn();
        response = mvcResult.getResponse().getContentAsString();
        assertTrue(response.isEmpty());

        this.eventoService.deleteById(event.getId());
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void replaceEvento() throws Exception {
        String uri = "/events/updateEvent/";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = setUpUsuario();
        else user = this.usuarioService.findById("test_user").get();
        Evento event = setUpEvento(user);
        String token = getToken(user);

        RequestEvento requestEvento = new RequestEvento();
        requestEvento.setUsuarioE(user);
        requestEvento.setNombre(event.getNombre() + " v2");
        requestEvento.setDescripcion(event.getDescripcion());
        requestEvento.setPrioridad(event.getPrioridad());
        requestEvento.setEtiqueta(event.getEtiqueta());
        requestEvento.setDuracion(event.getDuracion());
        requestEvento.setRecurrencia(event.getRecurrencia());
        requestEvento.setRecordatorio(event.getRecordatorio());
        requestEvento.setFechaInicio(event.getFechaInicio());
        requestEvento.setFechaFin(event.getFechaFin());

        // Valid request -> status 200 expected
        ResultActions resultActions = this.mockMvc.perform(put(uri + event.getId()).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(requestEvento)))
                .andExpect(status().isOk());
        MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        Evento obtainedEvent = objectMapper.readValue(response, Evento.class);
        assertNotNull(obtainedEvent);
        assertEquals(obtainedEvent.getUsuario().getUsuario(), user.getUsuario());
        assertEquals(obtainedEvent.getNombre(), requestEvento.getNombre());
        assertEquals(obtainedEvent.getDescripcion(), requestEvento.getDescripcion());

        this.mockMvc.perform(put(uri + (event.getId() + 1)).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(requestEvento)))
                .andExpect(status().isBadRequest());

        this.eventoService.deleteById(event.getId());
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void addNewEvento() throws Exception {
        String uri = "/events/addEvent";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = setUpUsuario();
        else user = this.usuarioService.findById("test_user").get();
        String token = getToken(user);

        RequestEvento requestEvento = new RequestEvento();
        requestEvento.setUsuarioE(user);
        requestEvento.setNombre("Test Event");
        requestEvento.setDescripcion("Event description");
        requestEvento.setPrioridad(1);
        requestEvento.setEtiqueta("Event label");
        requestEvento.setDuracion(1);
        requestEvento.setRecurrencia("D2");
        requestEvento.setRecordatorio(1);
        requestEvento.setFechaInicio(LocalDateTime.now().minusWeeks(3));
        requestEvento.setFechaFin(LocalDateTime.now().plusWeeks(2));

        this.mockMvc.perform(post(uri).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(requestEvento)))
                    .andExpect(status().isCreated());

        List<Evento> events = (List<Evento>) this.eventoService.findAll();
        assertFalse(events.isEmpty());
        assertEquals(events.get(0).getUsuario().getUsuario(), requestEvento.getUsuarioE().getUsuario());
        assertEquals(events.get(0).getNombre(), requestEvento.getNombre());
        assertEquals(events.get(0).getDescripcion(), requestEvento.getDescripcion());

        this.eventoService.deleteById(events.get(0).getId());
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void deleteEvento() throws Exception {
        String uri = "/events/deleteEvent/";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = setUpUsuario();
        else user = this.usuarioService.findById("test_user").get();
        Evento event = setUpEvento(user);
        String token = getToken(user);

        this.mockMvc.perform(delete(uri + event.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isAccepted());

        this.mockMvc.perform(delete(uri + (event.getId() + 1)).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        this.usuarioService.deleteById(user.getUsuario());
    }

}
