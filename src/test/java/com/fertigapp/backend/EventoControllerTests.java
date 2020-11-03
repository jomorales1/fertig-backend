package com.fertigapp.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fertigApp.backend.BackendApplication;
import com.fertigApp.backend.model.Evento;
import com.fertigApp.backend.model.Usuario;
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

    Evento createEvent(Usuario user) {
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
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        Evento event = createEvent(user);

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

//    @Test
//    void getAllEventosByUsuario() throws Exception {
//        String uri = "/events/getEvents";
//        Usuario user;
//        if (this.usuarioService.findById("test_user").isEmpty())
//            user = createUser();
//        else user = this.usuarioService.findById("test_user").get();
//        Evento event = createEvent(user);
//        String token = getToken(user);
//
//        ResultActions resultActions = this.mockMvc.perform(get(uri).header("Authorization", "Bearer " + token))
//                .andExpect(status().isOk());
//        MvcResult mvcResult = resultActions.andReturn();
//        String response = mvcResult.getResponse().getContentAsString();
//        CollectionType javaList = objectMapper.getTypeFactory().constructCollectionType(List.class, Evento.class);
//        List<Evento> events = objectMapper.readValue(response, javaList);
//        assertNotNull(events);
//        assertEquals(events.get(0).getUsuario().getUsuario(), user.getUsuario());
//        assertEquals(events.get(0).getNombre(), event.getNombre());
//        assertEquals(events.get(0).getDescripcion(), event.getDescripcion());
//
//        this.eventoService.deleteById(event.getId());
//        this.usuarioService.deleteById(user.getUsuario());
//    }

    @Test
    void getEvento() throws Exception {
        String uri = "/events/getEvent/";
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        Evento event = createEvent(user);
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
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        Evento event = createEvent(user);
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
            user = createUser();
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
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        Evento event = createEvent(user);
        String token = getToken(user);

        this.mockMvc.perform(delete(uri + event.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isAccepted());

        this.mockMvc.perform(delete(uri + (event.getId() + 1)).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        this.usuarioService.deleteById(user.getUsuario());
    }

}
