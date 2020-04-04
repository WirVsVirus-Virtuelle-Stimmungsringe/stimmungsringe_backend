package de.wirvsvirus.hack.mock;

import com.google.common.collect.Lists;
import de.wirvsvirus.hack.model.Role;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MockFactory {

    public static List<User> allUsers() {
        final List<User> users = new ArrayList<>();

        {
            User user = new User(UUID.fromString("cafecafe-b855-46ba-b907-321d2d38beef"));
            user.setName("Daniela");
            user.setRoles(Lists.newArrayList(Role.ARBEITNEHMER, Role.ELTERNTEIL, Role.ME_TIME));
            users.add(user);
        }

        {
            User user = new User(UUID.fromString("12340000-b855-46ba-b907-321d2d38feeb"));
            user.setName("Frida");
            user.setRoles(Lists.newArrayList(Role.KIND));
            users.add(user);
        }

        {
            User user = new User(UUID.fromString("deadbeef-b855-46ba-b907-321d01010101"));
            user.setName("Otto");
            user.setRoles(Lists.newArrayList(Role.ARBEITNEHMER, Role.PARTNER));
            users.add(user);
        }

        return users;
    }

    public static Sentiment sentimentByUser(final UUID userId) {
        if (UUID.fromString("cafecafe-b855-46ba-b907-321d2d38beef").equals(userId)) {
            return Sentiment.sunnyWithClouds;
        }
        if (UUID.fromString("12340000-b855-46ba-b907-321d2d38feeb").equals(userId)) {
            return Sentiment.thundery;
        }
        if (UUID.fromString("deadbeef-b855-46ba-b907-321d01010101").equals(userId)) {
            return Sentiment.cloudyNight;
        }

        return Sentiment.cloudy;
    }
}
