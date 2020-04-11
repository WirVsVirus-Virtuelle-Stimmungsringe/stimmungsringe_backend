package de.wirvsvirus.hack.mock;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import de.wirvsvirus.hack.model.Role;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;

import java.util.*;

public class MockFactory {

    public static final Map<UUID, User> allUsers = new HashMap<>();
    public static final Set<String> allGroups = new HashSet<>();
    public static final Map<UUID, String> groupByUserId = new HashMap<>();

    static {
        final List<User> users = new ArrayList<>();

        {
            User user = createUser("cafecafe-b855-46ba-b907-321d2d38beef");
            user.setName("Daniela");
            user.setRoles(Lists.newArrayList(Role.ARBEITNEHMER, Role.ELTERNTEIL, Role.ME_TIME));
            users.add(user);
        }

        {
            User user = createUser("12340000-b855-46ba-b907-321d2d38feeb");
            user.setName("Frida");
            user.setRoles(Lists.newArrayList(Role.KIND));
            users.add(user);
        }

        {
            User user = createUser("deadbeef-b855-46ba-b907-321d01010101");
            user.setName("Otto");
            user.setRoles(Lists.newArrayList(Role.ARBEITNEHMER, Role.PARTNER));
            users.add(user);
        }

        {
            User user = createUser("abbaabba-3333-46ba-b907-321d01055555");
            user.setName("Stefan");
            user.setRoles(Lists.newArrayList(Role.ARBEITNEHMER, Role.PARTNER));
            users.add(user);
        }

        users.forEach(user -> {
            allUsers.put(user.getUserId(), user);
        });
    }

    private static User createUser(final String userId) {
        return new User(UUID.fromString(userId), userId.substring(0, 4));
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
