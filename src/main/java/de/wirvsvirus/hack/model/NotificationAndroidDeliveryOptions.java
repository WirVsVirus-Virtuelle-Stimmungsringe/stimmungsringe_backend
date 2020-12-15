package de.wirvsvirus.hack.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationAndroidDeliveryOptions {
    Priority priority;
    String collapseKey;

    public enum Priority {
        /**
         * Default for data messages.
         */
        NORMAL,

        /**
         * Should be used for notifications.
         */
        HIGH;

        @JsonValue
        public String getPriority() {
            return name().toLowerCase();
        }
    }
}
