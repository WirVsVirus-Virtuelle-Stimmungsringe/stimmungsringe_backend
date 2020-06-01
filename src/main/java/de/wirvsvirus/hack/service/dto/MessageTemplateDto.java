package de.wirvsvirus.hack.service.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@ToString
@Builder
@Data
public class MessageTemplateDto {

    private boolean used;
    private String text;
}
