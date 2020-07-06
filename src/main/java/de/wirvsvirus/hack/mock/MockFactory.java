package de.wirvsvirus.hack.mock;

import com.google.common.collect.Lists;
import de.wirvsvirus.hack.model.*;

import java.time.Instant;
import java.util.*;

public class MockFactory {

    public static final Map<UUID, User> allUsers = new HashMap<>();
    public static final Map<UUID, Group> allGroups = new HashMap<>();
    // TODO maintain order
    public static final Map<UUID, UUID> groupByUserId = new HashMap<>();
    public static Map<UUID, Sentiment> sentimentByUser = new HashMap<>();
    public static Map<UUID, Instant> lastStatusUpdateByUser = new HashMap<>();

    /**
     * groupId -> list message
     */
    public static Map<UUID, List<Message>> allGroupMessages = new HashMap<>();

    /**
     * userId -> list devices
     */
    public static Map<UUID, List<Device>> allDevicesByUser = new HashMap<>();

    public static final User daniela;
    public static final User frida;
    public static final User otto;
    public static final User stefan;

    static {
        final List<User> users = new ArrayList<>();



        {
            User user = createUser("cafecafe-b855-46ba-b907-321d2d38beef");
            user.setName("Daniela");
            user.setRoles(Lists.newArrayList(Role.ARBEITNEHMER, Role.ELTERNTEIL, Role.ME_TIME));
            user.setStockAvatar(StockAvatar.LISA);
            users.add(user);
            daniela = user;
        }

        {
            User user = createUser("12340000-b855-46ba-b907-321d2d38feeb");
            user.setName("Frida");
            user.setRoles(Lists.newArrayList(Role.KIND));
            user.setStockAvatar(StockAvatar.DANI);
            users.add(user);
            frida = user;
        }

        {
            User user = createUser("deadbeef-b855-46ba-b907-321d01010101");
            user.setName("Otto");
            user.setRoles(Lists.newArrayList(Role.ARBEITNEHMER, Role.PARTNER));
            users.add(user);
            otto = user;
        }

        {
            User user = createUser("abbaabba-3333-46ba-b907-321d01055555");
            user.setName("Stefan");
            user.setRoles(Lists.newArrayList(Role.ARBEITNEHMER, Role.PARTNER));
            user.setStockAvatar(StockAvatar.STEFAN);
            users.add(user);
            stefan = user;
        }

        users.forEach(user -> {
            allUsers.put(user.getUserId(), user);
            sentimentByUser.put(user.getUserId(), dummySentimentByUser(user.getUserId()));
        });

    }

    private static User createUser(final String userId) {
        return new User(UUID.fromString(userId), userId.substring(0, 4));
    }

    private static Sentiment dummySentimentByUser(final UUID userId) {
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
