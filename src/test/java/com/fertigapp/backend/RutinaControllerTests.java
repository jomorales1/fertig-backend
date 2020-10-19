package com.fertigapp.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fertigApp.backend.BackendApplication;
import com.fertigApp.backend.model.Rutina;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.requestModels.LoginRequest;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = BackendApplication.class)
@AutoConfigureMockMvc
public class RutinaControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RutinaService rutinaService;

    private final Jackson2ObjectMapperBuilder mapperBuilder = new Jackson2ObjectMapperBuilder();

    private final ObjectMapper objectMapper = mapperBuilder.build();

    private final Date fechaIncio = new Date();
    private final Date fechaFin = new Date();

    public Usuario setUpUsuario() {
        Usuario user = new Usuario();

        user.setUsuario("test_user");
        user.setCorreo("test@email.com");
        user.setNombre("Test User");
        user.setPassword(passwordEncoder.encode("testing"));
        user.setRutinas(new ArrayList<>());

        this.usuarioService.save(user);
        return user;
    }

    public Rutina setUpRutina(Usuario user) {
        Rutina routine = new Rutina();

        routine.setUsuario(setUpUsuario());
        routine.setNombre("test_routine");
        routine.setDescripcion("test_routine_description");
        routine.setPrioridad(2);
        routine.setEtiqueta("test_routine_tag");
        routine.setEstimacion(90);
        routine.setFechaInicio(this.fechaIncio);
        routine.setFechaFin(this.fechaFin);
        routine.setRecurrencia("codification");
        routine.setRecordatorio(60);

        return rutinaService.save(routine);
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
    public void contextLoads(){
        assertTrue(true);
    }


    @Test
    @WithMockUser(value = "ADMIN")
    public void getAllRutina() throws Exception {
        String uri = "/routines";
        Usuario user;
        user = (usuarioService.findById("test_user").isEmpty()) ? setUpUsuario() : usuarioService.findById("test_user").get();
        Rutina rutina = setUpRutina(user);

        ResultActions resultActions = this.mockMvc.perform(get(uri));
        assertThat(resultActions.andExpect(status().isOk()));
        MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        CollectionType javaList = objectMapper.getTypeFactory().constructCollectionType(List.class, Rutina.class);
        List<Rutina> rutinas = objectMapper.readValue(response, javaList);
        assertTrue(rutinas != null);
        this.rutinaService.deleteById(rutina.getId());
        this.usuarioService.deleteById(user.getUsuario());
    }

//    @Test
//    public void getAllRutinasByUsuario() throws Exception {
//        String uri = "/routines/getRoutines";
//        Usuario user;
//        List<Rutina> rutinas = new ArrayList<>();
//
//        user = (usuarioService.findById("test_user").isEmpty()) ? setUpUsuario() : usuarioService.findById("test_user").get();
//        for(int i=0; i<1; i++)
//            rutinas.add(setUpRutina(user));
//
//        String token = getToken(user);
//        ResultActions resultActions = this.mockMvc.perform(get(uri).header("Authorization", "Bearer " + token));
//        assertThat(resultActions.andExpect(status().isOk()));
//        MvcResult mvcResult = resultActions.andReturn();
//        String response = mvcResult.getResponse().getContentAsString();
//        CollectionType javaList = objectMapper.getTypeFactory().constructCollectionType(List.class, Rutina.class);
//        List<Rutina> rutinasObtained = objectMapper.readValue(response, javaList);
//        assertNotNull(rutinasObtained);
//
//        for(int i=0; i<1; i++){
//            assertEquals(rutinasObtained.get(i).getNombre(), rutinas.get(i).getNombre());
//            assertEquals(rutinasObtained.get(i).getDescripcion(), rutinas.get(i).getDescripcion());
//            assertEquals(rutinasObtained.get(i).getPrioridad(),rutinas.get(i).getPrioridad());
//            assertEquals(rutinasObtained.get(i).getEtiqueta(), rutinas.get(i).getEtiqueta());
//            assertEquals(rutinasObtained.get(i).getEstimacion(), rutinas.get(i).getEstimacion());
//            assertTrue(rutinasObtained.get(i).getFechaInicio().compareTo(rutinas.get(i).getFechaInicio()) < 10);
//            assertTrue(rutinasObtained.get(i).getFechaFin().compareTo(rutinas.get(i).getFechaFin()) < 10);
//            assertEquals(rutinasObtained.get(i).getRecurrencia(), rutinas.get(i).getRecurrencia());
//            assertEquals(rutinasObtained.get(i).getRecordatorio(), rutinas.get(i).getRecordatorio());
//            rutinaService.deleteById(rutinas.get(i).getId());
//        }
//        usuarioService.deleteById("test_user");
//    }

    @Test
    public void getRutina() throws Exception {
        String uri = "/routines/getRoutine";
        Usuario user;
        Rutina rutina;
        user = (usuarioService.findById("test_user").isEmpty()) ? setUpUsuario() : usuarioService.findById("test_user").get();
        rutina = setUpRutina(user);

        String token = getToken(user);
        ResultActions resultActions = this.mockMvc.perform(get(uri+"/"+rutina.getId()).header("Authorization", "Bearer " + token));
        assertThat(resultActions.andExpect(status().isOk()));
        MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        Rutina rutinaObtained = objectMapper.readValue(response, Rutina.class);
        assertEquals(rutinaObtained.getNombre(), rutina.getNombre());
        assertEquals(rutinaObtained.getDescripcion(), rutina.getDescripcion());
        assertEquals(rutinaObtained.getPrioridad(),rutina.getPrioridad());
        assertEquals(rutinaObtained.getEtiqueta(), rutina.getEtiqueta());
        assertEquals(rutinaObtained.getEstimacion(), rutina.getEstimacion());
        assertTrue(rutinaObtained.getFechaInicio().compareTo(rutina.getFechaInicio()) < 10);
        assertTrue(rutinaObtained.getFechaFin().compareTo(rutina.getFechaFin()) < 10);
        assertEquals(rutinaObtained.getRecurrencia(), rutina.getRecurrencia());
        assertEquals(rutinaObtained.getRecordatorio(), rutina.getRecordatorio());

        rutinaService.deleteById(rutina.getId());
        usuarioService.deleteById("test_user");
    }
}
