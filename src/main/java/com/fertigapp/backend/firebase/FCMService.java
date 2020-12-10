package com.fertigapp.backend.firebase;

import com.fertigapp.backend.requestmodels.PushNotificationRequest;
import com.google.firebase.messaging.*;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

@Service
public class FCMService {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(FCMService.class);

    private static final String FERTIG_URL = "https://zealous-hill-0e784f50f.azurestaticapps.net";
    private static final String IMAGE_URL = "https://www.flaticon.es/svg/static/icons/svg/1069/1069867.svg";

    public void sendMessageToToken(PushNotificationRequest request)
            throws InterruptedException, ExecutionException {
        Message message = getPreconfiguredMessageToToken(request);
        String response = sendAndGetResponse(message);
        LOGGER.info("Sent message to token. Device token: {},{}",request.getToken(),response);
    }

    private String sendAndGetResponse(Message message) throws InterruptedException, ExecutionException {
        return FirebaseMessaging.getInstance().sendAsync(message).get();
    }

    private AndroidConfig getAndroidConfig(String topic) {
        return AndroidConfig.builder()
                .setTtl(Duration.ofMinutes(2).toMillis()).setCollapseKey(topic)
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(AndroidNotification.builder().setSound(NotificationParameter.SOUND.getValue())
                        .setColor(NotificationParameter.COLOR.getValue()).setTag(topic).setIcon(IMAGE_URL).build()).build();
    }

    private ApnsConfig getApnsConfig(String topic) {
        return ApnsConfig.builder()
                .setAps(Aps.builder().setCategory(topic).setThreadId(topic).build())
                .setFcmOptions(ApnsFcmOptions.builder().setImage(IMAGE_URL).build()).build();
    }

    private WebpushConfig getWebpushConfig(String title, String body) {
        return WebpushConfig.builder().setNotification(
                WebpushNotification.builder().setTitle(title).setBody(body).setIcon(IMAGE_URL).build())
                .setFcmOptions(WebpushFcmOptions.builder().setLink(FERTIG_URL).build()).build();
    }

    private Message getPreconfiguredMessageToToken(PushNotificationRequest request) {
        return getPreconfiguredMessageBuilder(request).setToken(request.getToken()).build();
    }

    private Message.Builder getPreconfiguredMessageBuilder(PushNotificationRequest request) {
        AndroidConfig androidConfig = getAndroidConfig(request.getTopic());
        ApnsConfig apnsConfig = getApnsConfig(request.getTopic());
        WebpushConfig webpushConfig = getWebpushConfig(request.getTitle(), request.getMessage());
        return Message.builder()
                .setApnsConfig(apnsConfig).setAndroidConfig(androidConfig).setWebpushConfig(webpushConfig);
    }

}
