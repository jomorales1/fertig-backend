package com.fertigapp.backend.services;

import com.fertigapp.backend.firebase.FCMService;
import com.fertigapp.backend.requestModels.PushNotificationRequest;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class PushNotificationService {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(PushNotificationService.class);
    private final FCMService fcmService;

    public PushNotificationService(FCMService fcmService) {
        this.fcmService = fcmService;
    }

    public void sendPushNotificationToToken(PushNotificationRequest request) {
        try {
            this.fcmService.sendMessageToToken(request);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage());
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            LOGGER.error(e.getMessage());
        }
    }

}
