package de.wirvsvirus.hack.rest.dto;

import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AvailableAvatarsResponse {

  List<StockAvatarResponse> stockAvatars;

  @Value
  @Builder
  public static class StockAvatarResponse {

    String avatarName;
    String avatarUrl;
    // nullable
    String avatarSvgUrl;
  }
}
