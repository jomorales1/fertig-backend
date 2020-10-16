package com.fertigapp.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fertigApp.backend.BackendApplication;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.requestModels.RequestUsuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
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
public class UsuarioControllerTests {

    private int port = 8090;

    @Autowired
    private MockMvc mockMvc;

    private Jackson2ObjectMapperBuilder mapperBuilder = new Jackson2ObjectMapperBuilder();

    private ObjectMapper objectMapper = mapperBuilder.build();

    @Test
    public void contextLoads() {
        assertTrue(true);
    }

    @Test
    @WithMockUser(value = "ADMIN")
    public void getAllUsuarios() throws Exception {
        String uri = "/users/getAllUsers";
        ResultActions resultActions = this.mockMvc.perform(get(uri));
        assertThat(resultActions.andExpect(status().isOk()));
        MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        CollectionType javaList = objectMapper.getTypeFactory().constructCollectionType(List.class, Usuario.class);
        List<Usuario> users = objectMapper.readValue(response, javaList);
        assertTrue(users != null);
    }

//    @Test
//    public void getUsuario() throws Exception {
//        String uriGet = "/users/getById/";
//        String uri = "/users/getAllUsers";
//        ResultActions resultActions = this.mockMvc.perform(get(uri));
//        assertThat(resultActions.andExpect(status().isOk()));
//        MvcResult mvcResult = resultActions.andReturn();
//        String response = mvcResult.getResponse().getContentAsString();
//        CollectionType javaList = objectMapper.getTypeFactory().constructCollectionType(List.class, Usuario.class);
//        List<Usuario> users = objectMapper.readValue(response, javaList);
//        if (users.size() > 0) {
//            Usuario usuarioInDB = users.get(0);
//            ResultActions res = this.mockMvc.perform(get(uriGet + usuarioInDB.getUsuario()));
//            assertThat(res.andExpect(status().isOk()));
//            MvcResult mvcRes = res.andReturn();
//            String resp = mvcRes.getResponse().getContentAsString();
//            Usuario userObtained = objectMapper.readValue(resp, Usuario.class);
//            assertTrue(usuarioInDB.getCorreo().equals(userObtained.getCorreo()));
//        }
//        ResultActions resultActions1 = this.mockMvc.perform(get(uriGet + "pepitooo"));
//        assertThat(resultActions1.andExpect(status().isOk()));
//        MvcResult mvcResult1 = resultActions1.andReturn();
//        String response1 = mvcResult1.getResponse().getContentAsString();
//        assertTrue(response1.isEmpty());
//    }

//    @Test
//    public void replaceUsuario() throws Exception {
//
////        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
////        params.add("grant_type", "password");
////        params.add("client_id", "cliente");
////        params.add("username", "jomorales");
////        params.add("password", "testing");
////
////        ResultActions result
////                = mockMvc.perform(get("/oauth/token")
////                .params(params)
////                .with(httpBasic("cliente","secret"))
////                .accept("application/json;charset=UTF-8"))
////                .andExpect(status().isOk())
////                .andExpect(content().contentType("application/json;charset=UTF-8"));
////
////        String resultString = result.andReturn().getResponse().getContentAsString();
////
////        JacksonJsonParser jsonParser = new JacksonJsonParser();
////
////        String token = jsonParser.parseMap(resultString).get("access_token").toString();
//        String p = "50ef00ea-78cb-4e1f-b6ab-a7c3f5637799";
//
//        String uri = "/users/update";
//        String searchUri = "/users/getById/";
//        RequestUsuario requestUsuario = new RequestUsuario();
//        requestUsuario.setUsuario("jomorales");
//        requestUsuario.setNombre("Jorge Aurelio Morales Manrique");
//        requestUsuario.setCorreo("jomorales@unal.edu.co");
//        requestUsuario.setPassword("testing");
//        ResultActions resultActions = this.mockMvc.perform(put(uri).header("Authorization", "Bearer " + p)
//                .contentType(MediaType.APPLICATION_JSON).content(objectMapper
//                        .writeValueAsString(requestUsuario)));
//        assertThat(resultActions.andExpect(status().isOk()));
//        ResultActions findUpdatedUser = this.mockMvc.perform(get(searchUri + requestUsuario.getUsuario()));
//        assertThat(findUpdatedUser.andExpect(status().isOk()));
//        MvcResult mvcResult = findUpdatedUser.andReturn();
//        String response = mvcResult.getResponse().getContentAsString();
//        Usuario userObtained = objectMapper.readValue(response, Usuario.class);
//        assertTrue(userObtained.getUsuario().equals(requestUsuario.getUsuario()));
//        assertTrue(userObtained.getCorreo().equals(requestUsuario.getCorreo()));
//        assertTrue(userObtained.getNombre().equals(requestUsuario.getNombre()));
//    }

//    @Test
//    public void addNewUsuario() throws Exception {
//        String uri = "/users/addUser";
//        RequestUsuario requestUsuario = new RequestUsuario();
//        requestUsuario.setUsuario("jomorales");
//        requestUsuario.setNombre("Jorge M");
//        requestUsuario.setCorreo("correo@gmail.com");
//        requestUsuario.setPassword("password");
//        ResultActions resultActions = this.mockMvc.perform(post(uri).contentType(MediaType.APPLICATION_JSON).content(objectMapper
//                .writeValueAsString(requestUsuario)));
//        assertThat(resultActions.andExpect(status().isBadRequest()));
//
//        requestUsuario.setUsuario("valid_username");
//        requestUsuario.setCorreo("jomorales@unal.edu.co");
//        resultActions = this.mockMvc.perform(post(uri).contentType(MediaType.APPLICATION_JSON).content(objectMapper
//                .writeValueAsString(requestUsuario)));
//        assertThat(resultActions.andExpect(status().isConflict()));
//
//        requestUsuario.setUsuario("valid_username");
//        requestUsuario.setCorreo("valid_email@email.com");
//        resultActions = this.mockMvc.perform(post(uri).contentType(MediaType.APPLICATION_JSON).content(objectMapper
//                .writeValueAsString(requestUsuario)));
//        assertThat(resultActions.andExpect(status().isCreated()));
//        this.mockMvc.perform(delete("/users/deleteById/valid_username"));
//    }
//
//    @Test
//    public void deleteUsuario() throws Exception {
//        String uri = "/users/deleteById/";
//        String id = "valid_username";
//        ResultActions resultActions = this.mockMvc.perform(delete(uri + id));
//        assertThat(resultActions.andExpect(status().isAccepted()));
//        id = "pepitoooo";
//        resultActions = this.mockMvc.perform(delete(uri + id));
//        assertThat(resultActions.andExpect(status().isBadRequest()));
//    }

}

