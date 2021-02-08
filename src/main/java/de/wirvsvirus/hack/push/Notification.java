package de.wirvsvirus.hack.push;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Notification {

    String title;
    String body;
//    private String icon;

}
