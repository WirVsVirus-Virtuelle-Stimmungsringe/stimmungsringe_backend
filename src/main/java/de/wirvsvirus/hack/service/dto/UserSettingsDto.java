package de.wirvsvirus.hack.service.dto;

import de.wirvsvirus.hack.model.StockAvatar;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@ToString
@Builder
@Data
public class UserSettingsDto {

    private String name;
    private StockAvatar stockAvatar;

}
