package de.wirvsvirus.hack.rest.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConfigurationResponse {

    private String notificationServiceSenderId;

}
