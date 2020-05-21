package de.wirvsvirus.hack.rest.dto;

import de.wirvsvirus.hack.model.StockAvatar;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AvailableAvatarsResponse {
    private List<StockAvatar> stockAvatars;
}
