package com.fertigApp.backend.services;

import com.fertigApp.backend.firebase.FCMService;
import com.fertigApp.backend.requestModels.PushNotificationRequest;
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
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
