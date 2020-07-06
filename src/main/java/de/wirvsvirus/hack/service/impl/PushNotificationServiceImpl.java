package de.wirvsvirus.hack.service.impl;

import de.wirvsvirus.hack.exception.PushMessageNotSendException;
import de.wirvsvirus.hack.model.Device;
import de.wirvsvirus.hack.model.Notification;
import de.wirvsvirus.hack.model.NotificationData;
import de.wirvsvirus.hack.model.NotificationMessage;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import de.wirvsvirus.hack.service.PushNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.UUID;

@Service
@Slf4j
public class PushNotificationServiceImpl implements PushNotificationService {

    private OnboardingRepository onboardingRepository;

    @Value("${notification.service.url:}")
    private String notificationServiceUrl;

    @Value("${notification.sender.id:}")
    private String notificationSenderId;

    @Value("${notification.auth.key:}")
    private String notificationAuthKey;

    @PostConstruct
    public void initialize() {
        log.info("Notification Service Url: " + this.notificationServiceUrl);
        log.info("Notification Sender Id: " + this.notificationSenderId);
        log.info("Nofitication Auth Key is set: " + StringUtils.isNoneBlank(this.notificationAuthKey));
    }

    @Override
    public void registerFcmTokenForUser(final UUID userId, final String deviceIdentifier, final String fcmToken) {
        log.info("Register FCM Token for user {}: {}", userId,
                StringUtils.abbreviate(fcmToken, 8));

        final Device device = new Device();
        device.setUserId(userId);
        device.setDeviceIdentifier(deviceIdentifier);
        device.setFcmToken(fcmToken);
        onboardingRepository.addDevice(device);

    }

    @Override
    public String getSenderId() {
        return this.notificationSenderId;
    }

    @Override
    public void sendMessage(String receiptId, String title, String body) throws PushMessageNotSendException {
        final RestTemplate restTemplate = new RestTemplate();
        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.AUTHORIZATION, "key=" + this.notificationAuthKey);

        final HttpEntity<NotificationMessage> requestEntity = new HttpEntity<>(buildNotificationMessage(receiptId, title, body), headers);
        final ResponseEntity<String> responseEntity = restTemplate.exchange(this.notificationServiceUrl, HttpMethod.POST, requestEntity, String.class);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new PushMessageNotSendException(responseEntity.getBody());
        }
    }

    private NotificationMessage buildNotificationMessage(final String receiptId, final String title, final String body) {
        return NotificationMessage.builder()
                .to(receiptId)
                .data(NotificationData.builder()
                        .notification(Notification.builder()
                                .title(title)
                                .body(body)
                                .icon("")
                                .build())
                        .build())
                .build();
    }
}
