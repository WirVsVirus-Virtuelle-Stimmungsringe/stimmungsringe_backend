package de.wirvsvirus.hack.rest;

import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.rest.dto.UserMinimalResponse;

import java.util.Map;

public final class Mappers {

    public static UserMinimalResponse mapResponseFromDomain(User user) {
        final UserMinimalResponse response = new UserMinimalResponse();
        response.setUserId(user.getId());
        response.setDisplayName(user.getName());
        return response;
    }

    private Mappers() {
    }
}
