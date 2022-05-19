package de.wirvsvirus.hack.rest.dto;

import java.util.List;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class AvailableAvatarsResponse {

  @NonNull
  List<StockAvatarResponse> stockAvatars;

  @Value
  @Builder
  public static class StockAvatarResponse {

    @NonNull
    String avatarName;

    @NonNull
    String avatarUrl;

    @Nullable
    String avatarSvgUrl;
  }
}
