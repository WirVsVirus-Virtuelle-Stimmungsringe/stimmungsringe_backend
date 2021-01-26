package de.wirvsvirus.hack.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Notification {

    String title;
    String body;
//    private String icon;

}
