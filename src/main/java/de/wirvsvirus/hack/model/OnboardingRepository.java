package de.wirvsvirus.hack.model;

import com.google.common.base.Preconditions;
import de.wirvsvirus.hack.mock.MockFactory;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.MoreCollectors;
import one.util.streamex.StreamEx;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OnboardingRepository {

    private List<User> mockDb = new ArrayList<>();
    private Map<UUID, Sentiment> sentimentsByUser = new HashMap<>();

    @PostConstruct
    public void initMock() {
        mockDb = MockFactory.allUsers();

        startNewGroup("Rasselbande");
        joinGroup("Rasselbande", UUID.fromString("cafecafe-b855-46ba-b907-321d2d38beef"));
        joinGroup("Rasselbande", UUID.fromString("12340000-b855-46ba-b907-321d2d38feeb"));
        joinGroup("Rasselbande", UUID.fromString("deadbeef-b855-46ba-b907-321d01010101"));

        mockDb.forEach(user -> {
            final Sentiment sentiment = MockFactory.sentimentByUser(user.getId());
            updateStatus(user.getId(), sentiment);
        });

    }

    public User findByUserId(final UUID userId) {
        Preconditions.checkNotNull(userId);

        return
            StreamEx.of(mockDb)
                .collect(MoreCollectors.onlyOne(user -> user.getId().equals(userId)))
            .orElseThrow(() -> new IllegalStateException("User not found by id " + userId));
    }

    public static Optional<String> findGroupNameForUser(final UUID userId) {
        return Optional.ofNullable(MockFactory.groupByUserId.get(userId));
    }

    public void startNewGroup(String groupName) {
        Preconditions.checkState(groupName.length() >= 3);
        Preconditions.checkState(!MockFactory.allGroups.contains(groupName), "Group <%s> already exists", groupName);

        MockFactory.allGroups.add(groupName);
    }

    public void joinGroup(String groupName, UUID userId) {
        Preconditions.checkState(MockFactory.allGroups.contains(groupName), "Group <%s> does not exist", groupName);

        MockFactory.groupByUserId.put(userId, groupName);
    }

    /**
     * return all users in same group except the requesting user
     */
    public List<User> findOtherUsersInGroup(UUID userId) {
        return
        mockDb.stream()
            .filter(user -> !user.getId().equals(userId))
            .collect(Collectors.toList());
    }

    public Sentiment findSentimentByUserId(UUID userId) {

        final Sentiment sentiment = sentimentsByUser.get(userId);
        Preconditions.checkNotNull(
            sentiment, "Lookup error on sentiment lookup for user %s", userId);
        return sentiment;
    }

    public void updateStatus(final UUID userId, final Sentiment sentiment) {
        sentimentsByUser.put(userId, sentiment);
    }


    public Optional<String> findGroupNameByUser(final UUID userId) {
        return Optional.ofNullable(
            MockFactory.groupByUserId.get(userId));
    }
}
