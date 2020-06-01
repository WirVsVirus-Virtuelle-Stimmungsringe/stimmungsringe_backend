package de.wirvsvirus.hack.rest.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AvailableAvatarsResponse {
    private List<StockAvatarResponse> stockAvatars;

    @Data
    @Builder
    public static class StockAvatarResponse {
        private final String avatarName;
        private final String avatarUrl;
    }
}
