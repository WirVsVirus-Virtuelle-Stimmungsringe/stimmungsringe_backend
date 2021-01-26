package de.wirvsvirus.hack.rest.dto;

import de.wirvsvirus.hack.model.StockAvatar;
import lombok.Data;

@Data
public class UpdateUserSettingsRequest {

    private String name;

    private StockAvatar stockAvatar;

}
