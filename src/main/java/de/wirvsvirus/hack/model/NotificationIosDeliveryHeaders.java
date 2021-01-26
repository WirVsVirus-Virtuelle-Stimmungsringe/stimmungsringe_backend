package de.wirvsvirus.hack.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationIosDeliveryHeaders {
    @JsonProperty("apns-priority")
    Priority priority;
    @JsonProperty("apns-collapse-id")
    String apnsCollapseId;

    public enum Priority {
        /**
         * This priority level will throttle and deliver messages in bursts, takes power considerations into account.
         */
        NORMAL("5"),

        /**
         * This priority level delivers the message immediately.
         */
        HIGH("10");

        private final String priorityValue;

        Priority(final String priorityValue) {
            this.priorityValue = priorityValue;
        }

        @JsonValue
        public String getPriorityValue() {
            return priorityValue;
        }
    }
}
