package com.fertigApp.backend.firebase;

import com.fertigApp.backend.model.*;
import com.fertigApp.backend.payload.response.AbstractRecurrenteResponse;
import com.fertigApp.backend.requestModels.PushNotificationRequest;
import com.fertigApp.backend.services.FirebaseNTService;
import com.fertigApp.backend.services.PushNotificationService;
import com.fertigApp.backend.services.UsuarioService;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

@Service
public class NotificationSystem {

    private final PushNotificationService pushNotificationService;

    private final ThreadPoolTaskScheduler taskScheduler;

    private final UsuarioService usuarioService;

    private final FirebaseNTService firebaseNTService;

    private final HashMap<Integer, NotificationEvent> scheduledTasks;
    private final HashMap<Integer, NotificationEvent> scheduledRoutines;
    private final HashMap<Integer, NotificationEvent> scheduledEvents;

    public NotificationSystem(PushNotificationService notificationService, ThreadPoolTaskScheduler taskScheduler, UsuarioService usuarioService, FirebaseNTService firebaseNTService) {
        this.pushNotificationService = notificationService;
        this.taskScheduler = taskScheduler;
        this.usuarioService = usuarioService;
        this.firebaseNTService = firebaseNTService;
        this.scheduledTasks = new HashMap<>();
        this.scheduledRoutines = new HashMap<>();
        this.scheduledEvents = new HashMap<>();
    }

    private Calendar nextDate(OffsetDateTime fechaFin, Integer recordatorio) {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.SECOND, fechaFin.getSecond());
        now.set(Calendar.MINUTE, fechaFin.getMinute());
        now.set(Calendar.HOUR_OF_DAY, fechaFin.getHour());
        now.set(Calendar.DAY_OF_MONTH, fechaFin.getDayOfMonth());
        now.set(Calendar.MONTH, fechaFin.getMonth().getValue());
        now.set(Calendar.YEAR, fechaFin.getYear());
        now.add(Calendar.MINUTE, - recordatorio);
        return now;
    }

    public void scheduleTaskNotification(String username, Tarea tarea) {
        Calendar date = nextDate(tarea.getFechaFin(), tarea.getRecordatorio());
        NotificationEvent event = new NotificationEvent(this.taskScheduler.schedule(new TaskNotification(username, tarea), date.getTime()));
        if (!this.scheduledTasks.containsKey(tarea.getId()))
            this.scheduledTasks.put(tarea.getId(), event);
        else this.scheduledTasks.replace(tarea.getId(), event);
    }

    public void cancelScheduledTaskNotification(Integer id) {
        if (!this.scheduledTasks.containsKey(id)) return;
        this.scheduledTasks.remove(id);
    }

    public void scheduleRoutineNotification(String username, Rutina rutina) {
        OffsetDateTime next = AbstractRecurrenteResponse.findSiguiente(rutina.getFechaInicio(),
                rutina.getFechaFin(), rutina.getRecurrencia(), rutina.getDuracion(),
                rutina.getFranjaInicio(), rutina.getFranjaFin());
        Calendar date = nextDate(next, rutina.getRecordatorio());
        NotificationEvent event = new NotificationEvent(this.taskScheduler.schedule(new RoutineNotification(username, rutina), date.getTime()));
        if (!this.scheduledRoutines.containsKey(rutina.getId()))
            this.scheduledRoutines.put(rutina.getId(), event);
        else this.scheduledRoutines.replace(rutina.getId(), event);
    }

    public void cancelScheduledRoutineNotification(Integer id) {
        if (!this.scheduledRoutines.containsKey(id)) return;
        this.scheduledRoutines.remove(id);
    }

    public void scheduleEventNotification(String username, Evento evento) {
        OffsetDateTime next = evento.getFechaInicio();
        Calendar date = nextDate(next, evento.getRecordatorio());
        NotificationEvent event = new NotificationEvent(this.taskScheduler.schedule(new EventNotification(username, evento), date.getTime()));
        if (!this.scheduledEvents.containsKey(evento.getId()))
            this.scheduledEvents.put(evento.getId(), event);
        else this.scheduledEvents.replace(evento.getId(), event);
    }

    public void cancelScheduledEventNotification(Integer id) {
        if (!this.scheduledEvents.containsKey(id)) return;
        this.scheduledEvents.remove(id);
    }

    static class NotificationEvent {

        private final ScheduledFuture<?> scheduled;

        NotificationEvent(ScheduledFuture<?> scheduled) {
            this.scheduled = scheduled;
        }

        public ScheduledFuture<?> getScheduled() {
            return scheduled;
        }

    }

    class TaskNotification implements Runnable {

        private final String username;
        private final Tarea tarea;

        TaskNotification(String username, Tarea tarea) {
            this.username = username;
            this.tarea = tarea;
        }

        @Override
        public void run() {
            Optional<Usuario> optionalUsuario = usuarioService.findById(this.username);
            Usuario usuario = optionalUsuario.orElse(new Usuario());
            List<FirebaseNotificationToken> notificationTokens = (List<FirebaseNotificationToken>) firebaseNTService.findAllByUsuario(usuario);
            for (FirebaseNotificationToken token : notificationTokens) {
                PushNotificationRequest notificationRequest = new PushNotificationRequest();
                notificationRequest.setTitle("Recordatorio de tarea");
                notificationRequest.setMessage("Tu tarea " + "[" + this.tarea.getNombre() + "]" + " vence en la siguiente fecha " + this.tarea.getFechaFin().toString() + ", no lo olvides!");
                notificationRequest.setTopic("Recordatorio");
                notificationRequest.setToken(token.getToken());
                pushNotificationService.sendPushNotificationToToken(notificationRequest);
            }
        }
    }

    class RoutineNotification implements Runnable {

        private final String username;
        private final Rutina rutina;

        RoutineNotification(String username, Rutina rutina) {
            this.username = username;
            this.rutina = rutina;
        }

        @Override
        public void run() {
            OffsetDateTime next = AbstractRecurrenteResponse.findSiguiente(this.rutina.getFechaInicio(),
                    this.rutina.getFechaFin(), this.rutina.getRecurrencia(), this.rutina.getDuracion(),
                    this.rutina.getFranjaInicio(), this.rutina.getFranjaFin());
            Calendar date = nextDate(next, this.rutina.getRecordatorio());
            Optional<Usuario> optionalUsuario = usuarioService.findById(this.username);
            Usuario usuario = optionalUsuario.orElse(new Usuario());
            List<FirebaseNotificationToken> notificationTokens = (List<FirebaseNotificationToken>) firebaseNTService.findAllByUsuario(usuario);
            for (FirebaseNotificationToken token : notificationTokens) {
                PushNotificationRequest notificationRequest = new PushNotificationRequest();
                notificationRequest.setTitle("Recordatorio de rutina");
                notificationRequest.setMessage("Tu rutina " + "[" + rutina + "]" + " empieza en la siguiente fecha " + date.toString() + ", no lo olvides!");
                notificationRequest.setTopic("Recordatorio");
                notificationRequest.setToken(token.getToken());
                pushNotificationService.sendPushNotificationToToken(notificationRequest);
            }
            NotificationEvent event = new NotificationEvent(taskScheduler.schedule(new RoutineNotification(this.username, this.rutina), date.getTime()));
            scheduledRoutines.replace(this.rutina.getId(), event);
        }
    }

    class EventNotification implements Runnable {

        private final String username;
        private final Evento evento;

        EventNotification(String username, Evento evento) {
            this.username = username;
            this.evento = evento;
        }

        @Override
        public void run() {
            Optional<Usuario> optionalUsuario = usuarioService.findById(this.username);
            Usuario usuario = optionalUsuario.orElse(new Usuario());
            List<FirebaseNotificationToken> notificationTokens = (List<FirebaseNotificationToken>) firebaseNTService.findAllByUsuario(usuario);
            for (FirebaseNotificationToken token : notificationTokens) {
                PushNotificationRequest notificationRequest = new PushNotificationRequest();
                notificationRequest.setTitle("Recordatorio de evento");
                notificationRequest.setMessage("Tu evento " + "[" + this.evento.getNombre() + "]" + " inicia en la siguiente fecha " + this.evento.getFechaInicio().toString() + ", no lo olvides!");
                notificationRequest.setTopic("Recordatorio");
                notificationRequest.setToken(token.getToken());
                pushNotificationService.sendPushNotificationToToken(notificationRequest);
            }
        }
    }

}
