package com.fertigapp.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fertigApp.backend.BackendApplication;
import com.fertigApp.backend.model.Completada;
import com.fertigApp.backend.model.Rutina;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.requestModels.LoginRequest;
import com.fertigApp.backend.requestModels.RequestCompletada;
import com.fertigApp.backend.services.CompletadaService;
import com.fertigApp.backend.services.RutinaService;
import com.fertigApp.backend.services.UsuarioService;
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

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = BackendApplication.class)
@AutoConfigureMockMvc
class CompletadaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RutinaService rutinaService;

    @Autowired
    private CompletadaService completadaService;

    private final Jackson2ObjectMapperBuilder mapperBuilder = new Jackson2ObjectMapperBuilder();

    private final ObjectMapper objectMapper = mapperBuilder.build();

    public Usuario setUpUsuario() {
        Usuario user;
        if(usuarioService.existsById("test_user")){
            user = usuarioService.findById("test_user").get();
        } else {
            user = new Usuario();

            user.setUsuario("test_user");
            user.setCorreo("test@email.com");
            user.setNombre("Test User");
            user.setPassword(passwordEncoder.encode("testing"));

            this.usuarioService.save(user);
        }
        return user;
    }

    public Rutina setUpRutina(Usuario user, Date fechaIncio, Date fechaFin) {
        Rutina routine = new Rutina();

        routine.setUsuario(setUpUsuario());
        routine.setNombre("test_routine");
        routine.setDescripcion("test_routine_description");
        routine.setPrioridad(2);
        routine.setEtiqueta("test_routine_tag");
        routine.setDuracion(90);
        routine.setFechaInicio(fechaIncio);
        routine.setFechaFin(fechaFin);
        routine.setRecurrencia("codification");
        routine.setRecordatorio(60);

        return rutinaService.save(routine);
    }

    public Completada setUpCompletada(int idRutina, Date fecha){
        Completada completada = new Completada();
        completada.setRutina(rutinaService.findById(idRutina).get());
        completada.setFecha(fecha);
        completadaService.save(completada);
        return completada;
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

    @Test
    void contextLoads() {
        assertTrue(true);
    }

    @Test
    void AddNewCompletada() throws Exception {
        String uri = "/completed/addCompleted/";
        Usuario usuario = setUpUsuario();
        Rutina rutina = setUpRutina(usuario, new Date(0), new Date(3600*24*30));
        RequestCompletada requestCompletada = new RequestCompletada(rutina.getId(), new Date(0));
        String token = getToken(usuario);


        // Valid request -> status 201 expected
        this.mockMvc.perform(post(uri).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(requestCompletada)))
                .andExpect(status().isCreated());

        // Invalid request (id rutina not found) -> status 400 expected
        requestCompletada.setRutina(rutina.getId() + 1);
        this.mockMvc.perform(post(uri).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(requestCompletada)))
                .andExpect(status().isBadRequest());

        completadaService.deleteAllByRutina(rutina);
        rutinaService.deleteById(rutina.getId());
        usuarioService.deleteById("test_user");
    }

    @Test
    void getAllCompletadasByRutina() throws Exception {
        String uri = "/completed/getCompleted/";
        Usuario usuario = setUpUsuario();
        Rutina rutina = setUpRutina(usuario, new Date(0), new Date(3600*24*30));
        Completada completada = setUpCompletada(rutina.getId(), new Date(0));
        String token = getToken(usuario);

        // Valid request -> status 200 expected
        ResultActions resultActions = this.mockMvc.perform(get(uri + String.valueOf(rutina.getId()))
                .header("Authorization", "Bearer " + token)).andExpect(status().isOk());
        MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        CollectionType javaList = objectMapper.getTypeFactory().constructCollectionType(List.class, Completada.class);
        List<Completada> completadas = objectMapper.readValue(response, javaList);
        assertFalse(completadas.isEmpty());
        assertEquals(completadas.get(0).getRutina().getId(), completada.getRutina().getId());
        assertEquals(completadas.get(0).getFecha(), completada.getFecha());

        // Invalid request (id rutina not found) -> status 400 expected
        resultActions = this.mockMvc.perform(get(uri + String.valueOf(rutina.getId()+1))
                .header("Authorization", "Bearer " + token)).andExpect(status().isOk());
        mvcResult = resultActions.andReturn();
        response = mvcResult.getResponse().getContentAsString();
        assertTrue(response.isEmpty());

        completadaService.deleteAllByRutina(rutina);
        rutinaService.deleteById(rutina.getId());
        usuarioService.deleteById("test_user");
    }

    @Test
    void getCompleted() throws Exception {
        String uri = "/completed/getOneCompleted/";
        Usuario usuario = setUpUsuario();
        Rutina rutina = setUpRutina(usuario, new Date(0), new Date(3600*24*30));
        Completada completada = setUpCompletada(rutina.getId(), new Date(0));
        String token = getToken(usuario);

        // Valid request -> status 200 expected
        ResultActions resultActions = this.mockMvc.perform(get(uri + String.valueOf(completada.getId()))
                .header("Authorization", "Bearer " + token)).andExpect(status().isOk());
        MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        Completada completadaResponse = objectMapper.readValue(response, Completada.class);
        assertNotNull(completadaResponse);
        assertEquals(completadaResponse.getRutina().getId(), completada.getRutina().getId());
        assertEquals(completadaResponse.getFecha(), completada.getFecha());

        // Invalid request (id rutina not found) -> status 400 expected
        resultActions = this.mockMvc.perform(get(uri + String.valueOf(completada.getId()+1))
                .header("Authorization", "Bearer " + token)).andExpect(status().isOk());
        mvcResult = resultActions.andReturn();
        response = mvcResult.getResponse().getContentAsString();
        assertTrue(response.isEmpty());

        completadaService.deleteAllByRutina(rutina);
        rutinaService.deleteById(rutina.getId());
        usuarioService.deleteById("test_user");
    }
}
