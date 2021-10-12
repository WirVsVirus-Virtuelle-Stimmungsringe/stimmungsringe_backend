package de.wirvsvirus.hack.rest;

import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.rest.dto.UserMinimalResponse;
import java.util.function.Function;

public final class Mappers {

    public static UserMinimalResponse mapResponseFromDomain(
        final User user, final Function<User, String> avatarUrlFromUser) {
        return UserMinimalResponse.builder()
                .userId(user.getUserId())
                .hasName(user.hasName())
                .displayName(user.getName())
                .avatarUrl(avatarUrlFromUser.apply(user))
                .build();
    }

    private Mappers() {
    }
}
