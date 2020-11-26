package com.fertigapp.backend.firebase;

import com.fertigapp.backend.recurrentstrategy.EventoRecurrentEntityStrategy;
import com.fertigapp.backend.recurrentstrategy.RutinaRecurrentEntityStrategy;
import com.fertigapp.backend.model.*;
import com.fertigapp.backend.requestmodels.PushNotificationRequest;
import com.fertigapp.backend.services.*;
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

    private final TareaService tareaService;

    private final RutinaService rutinaService;

    private final EventoService eventoService;

    private final FirebaseNTService firebaseNTService;

    private final HashMap<Integer, List<NotificationEvent>> scheduledTasks;
    private final HashMap<Integer, NotificationEvent> scheduledRoutines;
    private final HashMap<Integer, NotificationEvent> scheduledEvents;

    public NotificationSystem(PushNotificationService notificationService, ThreadPoolTaskScheduler taskScheduler, UsuarioService usuarioService, TareaService tareaService, RutinaService rutinaService, EventoService eventoService, FirebaseNTService firebaseNTService) {
        this.pushNotificationService = notificationService;
        this.taskScheduler = taskScheduler;
        this.usuarioService = usuarioService;
        this.tareaService = tareaService;
        this.rutinaService = rutinaService;
        this.eventoService = eventoService;
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

    public void scheduleTaskNotification(String username, Integer idTarea) {
        Optional<Tarea> optionalTarea = this.tareaService.findById(idTarea);
        Tarea tarea = optionalTarea.orElse(new Tarea());
        Date date = nextDate(tarea.getFechaFin(), tarea.getRecordatorio());
        NotificationEvent event = new NotificationEvent(username, this.taskScheduler.schedule(new TaskNotification(username, idTarea), date));
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

    public void cancelAllScheduledTaskNotifications() {
        for (Integer id : this.scheduledTasks.keySet()) {
            for (NotificationEvent event : this.scheduledTasks.get(id)) {
                event.getScheduled().cancel(false);
            }
        }
    }

    public void scheduleRoutineNotification(String username, Integer idRutina) {
        Optional<Rutina> optionalRutina = rutinaService.findById(idRutina);
        Rutina rutina = optionalRutina.orElse(new Rutina());
        RutinaRecurrentEntityStrategy rutinaRecurrentEntityStrategy = new RutinaRecurrentEntityStrategy(rutina);
        OffsetDateTime next = rutinaRecurrentEntityStrategy.findSiguiente(OffsetDateTime.now());
        Date date = nextDate(next, rutina.getRecordatorio());
        NotificationEvent event = new NotificationEvent(username, this.taskScheduler.schedule(new RoutineNotification(username, idRutina), date));
        if (!this.scheduledRoutines.containsKey(rutina.getId()))
            this.scheduledRoutines.put(rutina.getId(), event);
        else this.scheduledRoutines.replace(rutina.getId(), event);
    }

    public void cancelScheduledRoutineNotification(Integer id) {
        if (!this.scheduledRoutines.containsKey(id)) return;
        this.scheduledRoutines.get(id).getScheduled().cancel(true);
        this.scheduledRoutines.remove(id);
    }

    public void cancelAllScheduledRoutineNotifications() {
        for (Integer id : this.scheduledRoutines.keySet()) {
            this.scheduledRoutines.get(id).getScheduled().cancel(true);
        }
    }

    public void scheduleEventNotification(String username, Integer idEvento) {
        Optional<Evento> optionalEvento = this.eventoService.findById(idEvento);
        Evento evento = optionalEvento.orElse(new Evento());
        OffsetDateTime next = evento.getFechaInicio();
        Date date = nextDate(next, evento.getRecordatorio());
        NotificationEvent event = new NotificationEvent(username, this.taskScheduler.schedule(new EventNotification(username, idEvento), date));
        if (!this.scheduledEvents.containsKey(evento.getId()))
            this.scheduledEvents.put(evento.getId(), event);
        else this.scheduledEvents.replace(evento.getId(), event);
    }

    public void cancelScheduledEventNotification(Integer id) {
        if (!this.scheduledEvents.containsKey(id)) return;
        this.scheduledEvents.get(id).getScheduled().cancel(true);
        this.scheduledEvents.remove(id);
    }

    public void cancelAllScheduledEventNotifications() {
        for (Integer id : this.scheduledEvents.keySet()) {
            this.scheduledEvents.get(id).getScheduled().cancel(true);
        }
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
        private final Integer idTarea;

        TaskNotification(String username, Integer idTarea) {
            this.username = username;
            this.idTarea = idTarea;
        }

        @Override
        public void run() {
            Optional<Usuario> optionalUsuario = usuarioService.findById(this.username);
            Usuario usuario = optionalUsuario.orElse(new Usuario());
            Optional<Tarea> optionalTarea = tareaService.findById(this.idTarea);
            Tarea tarea = optionalTarea.orElse(new Tarea());
            List<FirebaseNotificationToken> notificationTokens = (List<FirebaseNotificationToken>) firebaseNTService.findAllByUsuario(usuario);
            for (FirebaseNotificationToken token : notificationTokens) {
                PushNotificationRequest notificationRequest = new PushNotificationRequest();
                notificationRequest.setTitle("Recordatorio de tarea");
                notificationRequest.setMessage("Tu tarea " + '"' + tarea.getNombre() + '"' + " vence en " + tarea.getRecordatorio() + " minutos, no lo olvides!");
                notificationRequest.setTopic("Recordatorio");
                notificationRequest.setToken(token.getToken());
                pushNotificationService.sendPushNotificationToToken(notificationRequest);
            }
        }
    }

    class RoutineNotification implements Runnable {

        private final String username;
        private final Integer idRutina;

        RoutineNotification(String username, Integer idRutina) {
            this.username = username;
            this.idRutina = idRutina;
        }

        @Override
        public void run() {
            Optional<Rutina> optionalRutina = rutinaService.findById(this.idRutina);
            Rutina rutina = optionalRutina.orElse(new Rutina());
            RutinaRecurrentEntityStrategy rutinaRecurrentEntityStrategy = new RutinaRecurrentEntityStrategy(rutina);
            OffsetDateTime next = rutinaRecurrentEntityStrategy.findSiguiente(OffsetDateTime.now());
            Date date = nextDate(next, rutina.getRecordatorio());
            Optional<Usuario> optionalUsuario = usuarioService.findById(this.username);
            Usuario usuario = optionalUsuario.orElse(new Usuario());
            List<FirebaseNotificationToken> notificationTokens = (List<FirebaseNotificationToken>) firebaseNTService.findAllByUsuario(usuario);
            for (FirebaseNotificationToken token : notificationTokens) {
                PushNotificationRequest notificationRequest = new PushNotificationRequest();
                notificationRequest.setTitle("Recordatorio de rutina");
                notificationRequest.setMessage("Tu rutina " + '"' + rutina.getNombre() + '"' + " empieza en " + rutina.getRecordatorio() + " minutos, no lo olvides!");
                notificationRequest.setTopic("Recordatorio");
                notificationRequest.setToken(token.getToken());
                pushNotificationService.sendPushNotificationToToken(notificationRequest);
            }
            NotificationEvent event = new NotificationEvent(this.username, taskScheduler.schedule(new RoutineNotification(this.username, this.idRutina), date));
            scheduledRoutines.replace(rutina.getId(), event);
        }
    }

    class EventNotification implements Runnable {

        private final String username;
        private final Integer idEvento;

        EventNotification(String username, Integer idEvento) {
            this.username = username;
            this.idEvento = idEvento;
        }

        @Override
        public void run() {
            Optional<Usuario> optionalUsuario = usuarioService.findById(this.username);
            Usuario usuario = optionalUsuario.orElse(new Usuario());
            Optional<Evento> optionalEvento = eventoService.findById(this.idEvento);
            Evento evento = optionalEvento.orElse(new Evento());
            EventoRecurrentEntityStrategy eventoRecurrentEntityStrategy = new EventoRecurrentEntityStrategy(evento);
            OffsetDateTime next = eventoRecurrentEntityStrategy.findSiguiente(OffsetDateTime.now());
            Date date = nextDate(next, evento.getRecordatorio());
            List<FirebaseNotificationToken> notificationTokens = (List<FirebaseNotificationToken>) firebaseNTService.findAllByUsuario(usuario);
            for (FirebaseNotificationToken token : notificationTokens) {
                PushNotificationRequest notificationRequest = new PushNotificationRequest();
                notificationRequest.setTitle("Recordatorio de evento");
                notificationRequest.setMessage("Tu evento " + '"' + evento.getNombre() + '"' + " inicia en " + evento.getRecordatorio() + " minutos, no lo olvides!");
                notificationRequest.setTopic("Recordatorio");
                notificationRequest.setToken(token.getToken());
                pushNotificationService.sendPushNotificationToToken(notificationRequest);
            }
            NotificationEvent notificationEvent = new NotificationEvent(this.username, taskScheduler.schedule(new EventNotification(this.username, this.idEvento), date));
            scheduledEvents.replace(evento.getId(), notificationEvent);
        }
    }

}
