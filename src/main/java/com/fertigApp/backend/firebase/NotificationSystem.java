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

    private final HashMap<Integer, List<NotificationEvent>> scheduledTasks;
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

    private Date nextDate(OffsetDateTime fechaFin, Integer recordatorio) {
        Calendar date = new GregorianCalendar(fechaFin.getYear(), fechaFin.getMonthValue() - 1, fechaFin.getDayOfMonth(),
            fechaFin.getHour(), fechaFin.getMinute());
        date.add(Calendar.MINUTE, - recordatorio);
        return date.getTime();
    }

    public void scheduleTaskNotification(String username, Tarea tarea) {
        Date date = nextDate(tarea.getFechaFin(), tarea.getRecordatorio());
        NotificationEvent event = new NotificationEvent(username, this.taskScheduler.schedule(new TaskNotification(username, tarea), date));
        if (!this.scheduledTasks.containsKey(tarea.getId()))
            this.scheduledTasks.put(tarea.getId(), new ArrayList<>());
        this.scheduledTasks.get(tarea.getId()).add(event);
    }

    public void cancelScheduledTaskNotification(String username, Integer id) {
        if (!this.scheduledTasks.containsKey(id)) return;
        for (NotificationEvent event : this.scheduledTasks.get(id)) {
            if (event.getUsername().equals(username)) {
                event.getScheduled().cancel(false);
                break;
            }
        }
    }

    public void scheduleRoutineNotification(String username, Rutina rutina) {
        OffsetDateTime next = AbstractRecurrenteResponse.findSiguiente(rutina.getFechaInicio(),
                rutina.getFechaFin(), rutina.getRecurrencia(), rutina.getDuracion(),
                rutina.getFranjaInicio(), rutina.getFranjaFin());
        Date date = nextDate(next, rutina.getRecordatorio());
        NotificationEvent event = new NotificationEvent(username, this.taskScheduler.schedule(new RoutineNotification(username, rutina), date));
        if (!this.scheduledRoutines.containsKey(rutina.getId()))
            this.scheduledRoutines.put(rutina.getId(), event);
        else this.scheduledRoutines.replace(rutina.getId(), event);
    }

    public void cancelScheduledRoutineNotification(Integer id) {
        if (!this.scheduledRoutines.containsKey(id)) return;
        this.scheduledRoutines.get(id).getScheduled().cancel(true);
        this.scheduledRoutines.remove(id);
    }

    public void scheduleEventNotification(String username, Evento evento) {
        OffsetDateTime next = evento.getFechaInicio();
        Date date = nextDate(next, evento.getRecordatorio());
        NotificationEvent event = new NotificationEvent(username, this.taskScheduler.schedule(new EventNotification(username, evento), date));
        if (!this.scheduledEvents.containsKey(evento.getId()))
            this.scheduledEvents.put(evento.getId(), event);
        else this.scheduledEvents.replace(evento.getId(), event);
    }

    public void cancelScheduledEventNotification(Integer id) {
        if (!this.scheduledEvents.containsKey(id)) return;
        this.scheduledEvents.get(id).getScheduled().cancel(true);
        this.scheduledEvents.remove(id);
    }

    static class NotificationEvent {

        private final String username;

        private final ScheduledFuture<?> scheduled;

        NotificationEvent(String username, ScheduledFuture<?> scheduled) {
            this.username = username;
            this.scheduled = scheduled;
        }

        public String getUsername() {
            return username;
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
                notificationRequest.setMessage("Tu tarea " + '"' + this.tarea.getNombre() + '"' + " vence en " + this.tarea.getRecordatorio() + " minutos, no lo olvides!");
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
            Date date = nextDate(next, this.rutina.getRecordatorio());
            Optional<Usuario> optionalUsuario = usuarioService.findById(this.username);
            Usuario usuario = optionalUsuario.orElse(new Usuario());
            List<FirebaseNotificationToken> notificationTokens = (List<FirebaseNotificationToken>) firebaseNTService.findAllByUsuario(usuario);
            for (FirebaseNotificationToken token : notificationTokens) {
                PushNotificationRequest notificationRequest = new PushNotificationRequest();
                notificationRequest.setTitle("Recordatorio de rutina");
                notificationRequest.setMessage("Tu rutina " + '"' + this.rutina.getNombre() + '"' + " empieza en " + this.rutina.getRecordatorio() + " minutos, no lo olvides!");
                notificationRequest.setTopic("Recordatorio");
                notificationRequest.setToken(token.getToken());
                pushNotificationService.sendPushNotificationToToken(notificationRequest);
            }
            NotificationEvent event = new NotificationEvent(this.username, taskScheduler.schedule(new RoutineNotification(this.username, this.rutina), date));
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
                notificationRequest.setMessage("Tu evento " + '"' + this.evento.getNombre() + '"' + " inicia en " + this.evento.getRecordatorio() + " minutos, no lo olvides!");
                notificationRequest.setTopic("Recordatorio");
                notificationRequest.setToken(token.getToken());
                pushNotificationService.sendPushNotificationToToken(notificationRequest);
            }
        }
    }

}
