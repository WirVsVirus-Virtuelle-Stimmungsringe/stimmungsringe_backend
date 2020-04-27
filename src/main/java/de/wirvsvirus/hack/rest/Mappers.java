package de.wirvsvirus.hack.rest;

import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.rest.dto.UserMinimalResponse;

public final class Mappers {

    public static UserMinimalResponse mapResponseFromDomain(User user) {
        return UserMinimalResponse.builder()
                .userId(user.getUserId())
                .displayName(user.getName())
                .hasName(user.hasName())
                .build();
    }

    private Mappers() {
    }
}
