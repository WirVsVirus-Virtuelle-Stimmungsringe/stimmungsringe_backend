package de.wirvsvirus.hack.service.impl;

import de.wirvsvirus.hack.model.*;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import de.wirvsvirus.hack.service.PushNotificationService;
import de.wirvsvirus.hack.service.dto.DeviceType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class PushNotificationServiceImpl implements PushNotificationService {

    @Autowired
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
        log.info("Nofitication Auth Key is set: " + StringUtils.abbreviate(this.notificationAuthKey, 10));
    }

    @Override
    public void registerFcmTokenForUser(final UUID userId,
        final String deviceIdentifier, final DeviceType deviceType,
        final String fcmToken) {
        log.info("Register FCM Token for user {}: {}", userId,
                StringUtils.abbreviate(fcmToken, 8));

        final Device device = new Device();
        device.setUserId(userId);
        device.setDeviceIdentifier(deviceIdentifier);
        device.setDeviceType(deviceType);
        device.setFcmToken(fcmToken);
        onboardingRepository.addDevice(device);

    }

    @Override
    public String getSenderId() {
        return this.notificationSenderId;
    }

    @Override
    public void sendMessage(String to, String title, String body, Optional<String> collapseId, Optional<URI> imageUri) {
        final RestTemplate restTemplate = new RestTemplate();
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.AUTHORIZATION, "key=" + this.notificationAuthKey);

        final NotificationMessage request = buildNotificationMessage(to, title,
                body, collapseId, imageUri);

        final HttpEntity<NotificationMessage> requestEntity = new HttpEntity<>(
                request, headers);
        final ResponseEntity<String> responseEntity = restTemplate.exchange(this.notificationServiceUrl, HttpMethod.POST, requestEntity, String.class);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            // do nothing
        }
    }

    private NotificationMessage buildNotificationMessage(
            final String to, final String title, final String body, final Optional<String> collapseIdOptional, final Optional<URI> imageUriOptional) {
        final NotificationMessage message = NotificationMessage.builder()
            .to(to)
            .notification(Notification.builder()
                .title(title)
                .body(body).build())
            .androidDeliveryOptions(NotificationAndroidDeliveryOptions.builder()
                .priority(NotificationAndroidDeliveryOptions.Priority.HIGH)
                .build())
            .data(NotificationData.builder()
                .clickAction("FLUTTER_NOTIFICATION_CLICK")
                .id("1")
                .status("done")
                .build())
            .build();

        collapseIdOptional.ifPresent(collapseId -> {
            message.getAndroidDeliveryOptions().setCollapseKey(collapseId);
            message.setIosDeliveryOptions(NotificationIosOptions.builder()
                .headers(NotificationIosDeliveryHeaders.builder()
                    .apnsCollapseId(collapseId)
                    .build())
                .build());
        });

        imageUriOptional.ifPresent(imageUri -> {
            final NotificationIosOptions iosDeliveryOptions =
                message.getIosDeliveryOptions() != null
                    ? message.getIosDeliveryOptions()
                    : NotificationIosOptions.builder().build();

            iosDeliveryOptions.setPayload(NotificationIosPayload.builder()
                .notificationIosApsPayload(NotificationIosApsPayload.builder()
                    .mutableContent(NotificationIosApsPayload.MutableContent.TRUE)
                    .build())
                .build());
            iosDeliveryOptions.setFcmOptions(NotificationIosFcmOptions.builder()
                .image(imageUri)
                .build());
        });

        return message;
    }
}
