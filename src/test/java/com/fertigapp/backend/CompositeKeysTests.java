package com.fertigapp.backend;
import com.fertigapp.backend.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = BackendApplication.class)
class CompositeKeysTests {

    @Test
    void contextLoads() {
        assertTrue(true);
    }

    @Test
    void idPreferido() throws Exception {
        IdPreferido idPreferido = new IdPreferido("sonido", "user");
        String sonido = idPreferido.getIdSonido();
        String user = idPreferido.getUsuario();
        idPreferido.setIdSonido("sonido2");
        idPreferido.setUsuario("user2");
        IdPreferido idPreferido1 = new IdPreferido();
        boolean compare = idPreferido.equals(idPreferido1);
        assertFalse(compare);
        int hash = idPreferido.hashCode();
    }

    @Test
    void idTareaUsuario() throws Exception {
        IdTareaUsuario idTareaUsuario = new IdTareaUsuario("user", 1);
        String user = idTareaUsuario.getUsuario();
        Integer tarea = idTareaUsuario.getTarea();
        idTareaUsuario.setUsuario("user2");
        idTareaUsuario.setTarea(2);
        IdTareaUsuario idTareaUsuario1 = new IdTareaUsuario();
        boolean compare = idTareaUsuario.equals(idTareaUsuario1);
        assertFalse(compare);
        int hash = idTareaUsuario.hashCode();
    }

    @Test
    void idTiempo() throws Exception {
        TareaDeUsuario tareaDeUsuario = new TareaDeUsuario();
        OffsetDateTime dateTime = OffsetDateTime.now();
        IdTiempo idTiempo = new IdTiempo(tareaDeUsuario, dateTime);
        TareaDeUsuario tareaDeUsuario1 = idTiempo.getTareaDeUsuario();
        OffsetDateTime dateTime1 = idTiempo.getFecha();
        int hash = idTiempo.hashCode();
        IdTiempo idTiempo1 = new IdTiempo();
        boolean compare = idTiempo.equals(idTiempo1);
        assertFalse(compare);
        Tiempo tiempo = new Tiempo();
        tiempo.setId(idTiempo);
        TareaDeUsuario tareaDeUsuario2 = tiempo.getTareaDeUsuario();
        OffsetDateTime dateTime2 = tiempo.getFecha();
    }

}
