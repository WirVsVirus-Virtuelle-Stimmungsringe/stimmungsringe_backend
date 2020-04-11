package de.wirvsvirus.hack.repository;

import de.wirvsvirus.hack.mock.MockFactory;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OnboardingRepository {
//    static Optional<String> findGroupNameForUser(UUID userId) {
//        return Optional.ofNullable(MockFactory.groupByUserId.get(userId));
//    }

    User findByUserId(UUID userId);

    void startNewGroup(String groupName);

    void joinGroup(String groupName, UUID userId);

    List<User> findOtherUsersInGroup(UUID userId);

    Sentiment findSentimentByUserId(UUID userId);

    void updateStatus(UUID userId, Sentiment sentiment);

    Optional<String> findGroupNameByUser(UUID userId);

    Optional<String> findGroupByName(String groupName);

    Optional<String> findGroupNameForUser(UUID id);
}
