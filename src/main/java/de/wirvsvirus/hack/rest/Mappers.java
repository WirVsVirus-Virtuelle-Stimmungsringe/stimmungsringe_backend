package de.wirvsvirus.hack.rest;

import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.rest.AvatarUrlResolver.AvatarUrls;
import de.wirvsvirus.hack.rest.dto.UserMinimalResponse;
import java.util.function.Function;

public final class Mappers {

  public static UserMinimalResponse mapResponseFromDomain(
      final User user, final Function<User, AvatarUrls> avatarUrlsFromUser) {
    final AvatarUrls avatarUrls = avatarUrlsFromUser.apply(user);
    return UserMinimalResponse.builder()
        .userId(user.getUserId())
        .hasName(user.hasName())
        .displayName(user.getName())
        .avatarUrl(avatarUrls.getAvatarUrl())
        .avatarSvgUrl(avatarUrls.getAvatarSvgUrl().orElse(null))
        .build();
  }

  private Mappers() {
  }
}
