package com.fertigapp.backend;

import com.fertigapp.backend.firebase.NotificationSystem;
import com.fertigapp.backend.payload.response.AbstractRecurrenteResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = BackendApplication.class)
@AutoConfigureMockMvc
class NotificationSystemTests {

    @Autowired
    private NotificationSystem notificationSystem;

    @Autowired
    private TareaService tareaService;

    @Autowired
    private RutinaService rutinaService;

    @Autowired
    private EventoService eventoService;

    @Autowired
    private CompletadaService completadaService;

    @Autowired
    private TareaDeUsuarioService tareaDeUsuarioService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private FirebaseNTService firebaseNTService;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    public Tarea setUpTarea(Usuario user) {
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
        task.setFechaFin(OffsetDateTime.now().plusSeconds(15));
        task.setHecha(false);
        task.setRecordatorio(2);
        task = this.tareaService.save(task);

        TareaDeUsuario tareaDeUsuario = new TareaDeUsuario();
        tareaDeUsuario.setUsuario(user);
        tareaDeUsuario.setTarea(task);
        tareaDeUsuario.setAdmin(true);
        this.tareaDeUsuarioService.save(tareaDeUsuario);

        return task;
    }

    public void setUpToken(Usuario user) {
        FirebaseNotificationToken notificationToken = new FirebaseNotificationToken();
        notificationToken.setToken("token");
        notificationToken.setUsuarioF(user);
        this.firebaseNTService.save(notificationToken);
    }

    Rutina setUpRutina(Usuario user) {
        Rutina routine = new Rutina();

        routine.setUsuario(user);
        routine.setNombre("test_routine");
        routine.setDescripcion("test_routine_description");
        routine.setPrioridad(2);
        routine.setEtiqueta("test_routine_tag");
        routine.setDuracion(90);
        routine.setFechaInicio(OffsetDateTime.now().plusSeconds(15));
        routine.setFechaFin(OffsetDateTime.now().plusSeconds(30));
        routine.setRecurrencia("D2");
        routine.setRecordatorio(60);

        Completada completada = new Completada();
        completada.setRutinaC(routine);
        completada.setFecha(
                AbstractRecurrenteResponse.findSiguiente(routine.getFechaInicio(),
                        routine.getFechaFin(),
                        routine.getRecurrencia(),
                        OffsetDateTime.now()));
        completada.setFechaAjustada(null);
        completada.setHecha(false);
        Rutina saved = this.rutinaService.save(routine);

        this.completadaService.save(completada);
        return saved;
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
        evento.setFechaInicio(OffsetDateTime.now().plusSeconds(15));
        evento.setFechaFin(OffsetDateTime.now().plusSeconds(30));

        return this.eventoService.save(evento);
    }

    @Test
    void contextLoads() {
        assertTrue(true);
    }

    @Test
    void scheduleTaskNotification() throws InterruptedException {
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        Tarea task = setUpTarea(user);
        setUpToken(user);

        this.notificationSystem.scheduleTaskNotification(user.getUsuario(), task.getId());
        TimeUnit.SECONDS.sleep(20);
        this.notificationSystem.cancelScheduledTaskNotification(user.getUsuario(), task.getId());

        this.tareaDeUsuarioService.deleteAllByTarea(task);
        this.tareaService.deleteById(task.getId());
        this.firebaseNTService.deleteById("token");
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void scheduleRoutineNotification() throws InterruptedException {
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        Rutina rutina = setUpRutina(user);
        setUpToken(user);

        this.notificationSystem.scheduleRoutineNotification(user.getUsuario(), rutina.getId());
        TimeUnit.SECONDS.sleep(40);
        this.notificationSystem.cancelScheduledRoutineNotification(rutina.getId());

        this.completadaService.deleteAllByRutina(rutina);
        this.rutinaService.deleteById(rutina.getId());
        this.firebaseNTService.deleteById("token");
        this.usuarioService.deleteById(user.getUsuario());
    }

    @Test
    void scheduleEventNotification() throws InterruptedException {
        Usuario user;
        if (this.usuarioService.findById("test_user").isEmpty())
            user = createUser();
        else user = this.usuarioService.findById("test_user").get();
        Evento event = setUpEvento(user);
        setUpToken(user);

        this.notificationSystem.scheduleEventNotification(user.getUsuario(), event.getId());
        TimeUnit.SECONDS.sleep(40);
        this.notificationSystem.cancelScheduledEventNotification(event.getId());

        this.eventoService.deleteById(event.getId());
        this.firebaseNTService.deleteById("token");
        this.usuarioService.deleteById(user.getUsuario());
    }

}
