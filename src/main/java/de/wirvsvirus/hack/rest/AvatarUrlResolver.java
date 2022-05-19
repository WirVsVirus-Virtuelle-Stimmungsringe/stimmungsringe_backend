package de.wirvsvirus.hack.rest;

import de.wirvsvirus.hack.model.StockAvatar;
import de.wirvsvirus.hack.model.User;
import java.util.Optional;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

public class AvatarUrlResolver {

  private static final String FALLBACK_AVATAR_PATH =
      AvatarController.CONTROLLER_PATH + AvatarController.FALLBACK_AVATAR_ENDPOINT;
  private static final String STOCK_AVATAR_PATH =
      AvatarController.CONTROLLER_PATH + AvatarController.STOCK_AVATAR_ENDPOINT;
  private static final String STOCK_AVATAR_SVG_PATH =
      AvatarController.CONTROLLER_PATH + AvatarController.STOCK_AVATAR_SVG_ENDPOINT;

  public static AvatarUrls getUserAvatarUrls(final User user) {
    final StockAvatar stockAvatar = user.getStockAvatar();
    if (stockAvatar == null) {
      return AvatarUrls.builder().avatarUrl(FALLBACK_AVATAR_PATH).build();
    }

    return getStockAvatarUrls(stockAvatar);
  }

  public static AvatarUrls getStockAvatarUrls(final StockAvatar stockAvatar) {
    return AvatarUrls.builder()
        .avatarUrl(String.format("%s/%s", STOCK_AVATAR_PATH, stockAvatar.name()))
        .avatarSvgUrl(
            stockAvatar.isSvgImage
                ? Optional.of(String.format("%s/%s", STOCK_AVATAR_SVG_PATH, stockAvatar.name()))
                : Optional.empty())
        .build();
  }

  @Value
  @Builder
  public static class AvatarUrls {

    @NonNull
    String avatarUrl;

    @Builder.Default
    Optional<String> avatarSvgUrl = Optional.empty();
  }
}
