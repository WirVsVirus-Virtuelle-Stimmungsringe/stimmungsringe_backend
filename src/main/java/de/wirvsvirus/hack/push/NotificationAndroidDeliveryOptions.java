package de.wirvsvirus.hack.push;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationAndroidDeliveryOptions {
    Priority priority;
    String collapseKey;

    /**
     * see https://firebase.google.com/docs/cloud-messaging/concept-options?hl=en#setting-the-priority-of-a-message
     */
    public enum Priority {
        /**
         *  Normal priority messages are delivered immediately when the app is in the foreground.
         */
        NORMAL,
        /**
         * FCM attempts to deliver high priority messages immediately, allowing FCM to wake a sleeping device when necessary and to run some limited processing
         */
        HIGH;

        @JsonValue
        public String getPriority() {
            return name().toLowerCase();
        }
    }
}
