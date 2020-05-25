package de.wirvsvirus.hack.mock;

import com.google.common.collect.Lists;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Message;
import de.wirvsvirus.hack.model.Role;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;

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
     * (from,to) -> list message
     */
    public static List<Message> userToUserMessages = new ArrayList<>();

    static {
        final List<User> users = new ArrayList<>();

        final User daniela;
        final User frida;
        final User otto;
        final User stefan;

        {
            User user = createUser("cafecafe-b855-46ba-b907-321d2d38beef");
            user.setName("Daniela");
            user.setRoles(Lists.newArrayList(Role.ARBEITNEHMER, Role.ELTERNTEIL, Role.ME_TIME));
            users.add(user);
            daniela = user;
        }

        {
            User user = createUser("12340000-b855-46ba-b907-321d2d38feeb");
            user.setName("Frida");
            user.setRoles(Lists.newArrayList(Role.KIND));
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
            users.add(user);
            stefan = user;
        }

        users.forEach(user -> {
            allUsers.put(user.getUserId(), user);
            sentimentByUser.put(user.getUserId(), dummySentimentByUser(user.getUserId()));
        });

        userToUserMessages.add(createMessage(stefan, otto, "Hallo, Otto!"));
        userToUserMessages.add(createMessage(frida, stefan, "Ich denk' an dich!"));
        userToUserMessages.add(createMessage(daniela, stefan, "Ich denk' an dich!"));
        userToUserMessages.add(createMessage(frida, otto, "Ich denk' an dich!"));

    }

    private static User createUser(final String userId) {
        return new User(UUID.fromString(userId), userId.substring(0, 4));
    }

    private static Message createMessage(final User sender, final User recipient, final String text) {
        final Message message = new Message();
        message.setSenderUserId(sender.getUserId());
        message.setReceipientUserId(recipient.getUserId());
        message.setText(text);
        return message;
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
