package de.wirvsvirus.hack.service;

import de.wirvsvirus.hack.exception.PushMessageNotSendException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

//@SpringBootTest
public class PushNotificationServiceTest {

    private static final String RECEIPT_ID = "cfLYQIhkdss:APA91bEp0pWohbRDvKnso1Q0qbmbD96yNZiWEeDPzHdj-j04B1x1x7mL_AAlGpLaWluRbiYdNtHiruoYxX4KndOnNSgIhVRv_JV10gLUHJZbNY6l00x9NG6qq9x5V9St40Rdx1CEuI7m";

    @Autowired
    private PushNotificationService pushNotificationService;

    //@Test
    public void sendPushNotificationTest() throws PushMessageNotSendException {
        this.pushNotificationService.sendMessage(RECEIPT_ID, "New Message", "Body", Optional.empty(), Optional.empty());
    }
}
