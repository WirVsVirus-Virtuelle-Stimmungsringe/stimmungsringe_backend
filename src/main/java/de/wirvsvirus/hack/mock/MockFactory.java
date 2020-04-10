package de.wirvsvirus.hack.mock;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import de.wirvsvirus.hack.model.Role;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;
import one.util.streamex.StreamEx;

import java.nio.ShortBuffer;
import java.util.*;

public class MockFactory {

    private static final ImmutableList<User> allUsers;

    public static final Set<String> allGroups = new HashSet<>();
    public static final Map<UUID, String> groupByUserId = new HashMap<>();

    static {
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

        {
            User user = new User(UUID.fromString("abbaabba-3333-46ba-b907-321d01055555"));
            user.setName("Stefan");
            user.setRoles(Lists.newArrayList(Role.ARBEITNEHMER, Role.PARTNER));
            users.add(user);
        }

        allUsers = ImmutableList.copyOf(users);
    }

    public static List<User> allUsers() {
        return allUsers;
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

    public static Optional<User> findByDeviceIdentifier(final String deviceIdentifier) {
        return
                StreamEx.of(allUsers())
                        .findAny(user ->
                                user.getId().toString().substring(0, 4)
                                        .equals(deviceIdentifier));
    }

}
