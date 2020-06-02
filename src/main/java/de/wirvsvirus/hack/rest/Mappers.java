package de.wirvsvirus.hack.rest;

import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.rest.dto.UserMinimalResponse;

import java.util.function.Function;

public final class Mappers {

    public static UserMinimalResponse mapResponseFromDomain(User user, Function<User, String> avatarUrlFromUser) {
        return UserMinimalResponse.builder()
                .userId(user.getUserId())
                .displayName(user.getName())
                .hasName(user.hasName())
                .avatarUrl(avatarUrlFromUser.apply(user))
                .build();
    }

    private Mappers() {
    }
}
