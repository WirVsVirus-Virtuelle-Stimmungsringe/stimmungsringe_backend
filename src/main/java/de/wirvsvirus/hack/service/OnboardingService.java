package de.wirvsvirus.hack.service;

import com.google.common.base.Preconditions;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.HistoryRepository;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import de.wirvsvirus.hack.service.dto.GroupSettingsDto;
import de.wirvsvirus.hack.service.dto.UserSettingsDto;
import de.wirvsvirus.hack.service.dto.UserSignedInDto;
import de.wirvsvirus.hack.spring.UserInterceptor;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OnboardingService {

    @Autowired
    private OnboardingRepository onboardingRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private PushNotificationService pushNotificationService;

    public UserSignedInDto signin(final String deviceIdentifier) {

        if ("0000".equals(deviceIdentifier)) {
            final User newUser = new User(UUID.randomUUID(), deviceIdentifier);
            newUser.setName("onboarding-fake");
            newUser.setRoles(Collections.emptyList());
            onboardingRepository.createNewUser(newUser, Sentiment.cloudyNight,
                "Wolken! Welche Wolken?", Instant.now());
            log.warn("Fake onboarding user {}", newUser.getUserId());
            return UserSignedInDto.builder()
                    .userId(newUser.getUserId())
                    .group(Optional.empty())
                    .build();
        }

        final Optional<User> userLookup =
                onboardingRepository.findByDeviceIdentifier(deviceIdentifier);

        if (!userLookup.isPresent()) {
            log.info("User not found - create blank user and assign deviceIdentifier");

            Preconditions.checkState(deviceIdentifier.length() >= 3);
            final User newUser = new User(UUID.randomUUID(), deviceIdentifier);
            newUser.setRoles(Collections.emptyList());
            onboardingRepository.createNewUser(newUser,
                Sentiment.sunnyWithClouds,
                "Bin neu hier!",
                Instant.now());
            return UserSignedInDto.builder()
                    .userId(newUser.getUserId())
                    .group(Optional.empty())
                    .build();
        } else {
            final UUID userId = userLookup.get().getUserId();
            final Optional<Group> group = onboardingRepository.findGroupByUser(userId);

            log.info("User {} signed in - group is {}", userLookup.get(), group);

            onboardingRepository.touchLastSignin(userId);

            return UserSignedInDto.builder()
                    .userId(userId)
                    .group(group)
                    .build();
        }

    }

    /**
     * note: supports partial update
     */
    public void updateUser(final User user, final UserSettingsDto userSettings) {

        if (StringUtils.isNotBlank(userSettings.getName())) {
            userSettings.setName(StringUtils.trim(userSettings.getName()));
        } else {
            // restore stored name (if any) when name is null/empty
            userSettings.setName(user.getName());
        }

        if (userSettings.getStockAvatar() == null) {
            // restore stored avatar (if any) when avatar is empty
            userSettings.setStockAvatar(user.getStockAvatar());
        }

        onboardingRepository.updateUser(user.getUserId(), userSettings);

    }

    public void updateGroup(final Group group, final GroupSettingsDto groupSettings) {
        final String groupName = groupSettings.getGroupName();

        if (groupName.isEmpty()) {
            return;
        }

        groupSettings.setGroupName(StringUtils.trim(groupSettings.getGroupName()));

        onboardingRepository.updateGroup(group.getGroupId(), groupSettings);
    }

    public Optional<Group> joinGroup(final String groupCode, final User user) {
        final Instant timestamp = Instant.now();

        final Optional<Group> match =
            onboardingRepository.findGroupByCode(groupCode);
        if (!match.isPresent()) {
            return Optional.empty();
        }
        final Group group = match.get();

        log.info("User {} joining group {}", user.getName(), group.getGroupName());

        final Optional<Group> currentGroupOfUser = onboardingRepository.findGroupByUser(user.getUserId());
        if (currentGroupOfUser.isPresent()) {
            if (currentGroupOfUser.get().getGroupId().equals(group.getGroupId())) {
                log.warn("User is already member of requested group");
            } else {
                log.warn("User is already member of another group");
            }
        } else {
            onboardingRepository.joinGroup(group.getGroupId(), user.getUserId());

            // write history
            historyRepository.logUserJoinedGroup(timestamp, group, user);

            onboardingRepository.findOtherUsersInGroup(group.getGroupId(), user.getUserId())
                .forEach(otherUser -> sendPushMessageUserJoined(otherUser, user, group));
        }

        return Optional.of(group);
    }

    public void leaveGroup(final UUID groupId, final User user) {
        log.info("User {} leaving group {}...", user.getName(), groupId);

        final Instant timestamp = Instant.now();

        final Optional<Group> currentGroup = onboardingRepository.findGroupByUser(user.getUserId());
        if (!currentGroup.isPresent()) {
            log.warn("User is not member of any group");
            log.warn("... remove user {} with no group", user.getUserId());
            onboardingRepository.deleteUser(user.getUserId());
            return;
        }

        Preconditions.checkState(currentGroup.get().getGroupId().equals(groupId),
            "User is member of another group");

        final Group group = onboardingRepository.findGroupById(groupId)
                .orElseThrow(() -> new IllegalStateException("Group <" + groupId + "> does not exist"));
        onboardingRepository.leaveGroup(group.getGroupId(), user.getUserId());

        // write history
        historyRepository.logUserLeftGroup(timestamp, group, user);

        onboardingRepository.findOtherUsersInGroup(groupId, user.getUserId())
            .forEach(otherUser -> sendPushMessageUserLeft(otherUser, user, group));

        log.info("... remove user {} from group {} with groupId {}",
            user.getUserId(), currentGroup.get().getGroupName(), currentGroup.get().getGroupId());
        onboardingRepository.deleteUser(user.getUserId());

    }

    public Group startNewGroup(final User user, final String groupName) {
        log.info("New group {} by user {}", groupName, user.getName());

        onboardingRepository.findGroupByUser(user.getUserId())
                .ifPresent(group -> {
                    throw new IllegalStateException(String.format(
                        "User %s must not be in group (groupId=%s) when starting a new one!",
                        user.getUserId(), group.getGroupId()
                    ));
                });

        final Instant timestamp = Instant.now();

        Preconditions.checkState(groupName.length() >= 3);
        final String groupCode = AlgoUtil.generateGroupCode();
        final Optional<Group> conflict = onboardingRepository.findGroupByCode(groupCode);
        Preconditions.checkState(!conflict.isPresent(), "Group code cannot be used - conflicting");
        final Group newGroup = onboardingRepository.startNewGroup(groupName, groupCode, timestamp);
        onboardingRepository.joinGroup(newGroup.getGroupId(), user.getUserId());
        // write history
        historyRepository.logUserStartedGroup(timestamp, newGroup, user);
        log.info("...started new group {} with groupid {}", newGroup.getGroupName(), newGroup.getGroupId());
        return newGroup;

    }

    /**
     * make sure group exists and user is member of group
     */
    public Group lookupGroupCheckPermissions(final UUID groupId) {
        final Group currentGroup = onboardingRepository.findGroupByUser(UserInterceptor.getCurrentUserId())
                .orElseThrow(() -> new IllegalStateException("User not in a group"));

        final Group group = onboardingRepository.findGroupById(groupId)
                .orElseThrow(() -> new IllegalStateException("Group not found"));

        Preconditions.checkState(group.getGroupId().equals(currentGroup.getGroupId()),
                "Requested group must have current user as a member");
        return group;
    }

    /**
     * Set the (new) status (aka sentiment status).
     * Note: status might not have changed
     */
    public void updateStatus(final User user, final Sentiment sentiment,
        final String sentimentText) {
        final Instant timestamp = Instant.now();

        final Group group = onboardingRepository
            .findGroupByUser(user.getUserId())
            .orElseThrow(
                () -> new IllegalStateException("User must be in group when updating status"));

        final Sentiment prevSentiment = onboardingRepository
            .findSentimentByUserId(user.getUserId());
        final boolean sentimentChanged = prevSentiment != sentiment;
        final String prevSentimentText = onboardingRepository
            .findSentimentTextByUserId(user.getUserId());
        final boolean sentimentTextChanged = !StringUtils.equals(prevSentimentText, sentimentText);

        if (!sentimentChanged && !sentimentTextChanged) {
            log.warn("User {} tried to issue a noop status update!", user.getUserId());
            return;
        }

        onboardingRepository.updateStatus(user.getUserId(), sentiment,
            sentimentText);
        onboardingRepository.touchLastStatusUpdate(user.getUserId(), timestamp);

        // write history
        historyRepository.logUserUpdatedStatus(timestamp, group, user, sentiment, sentimentText, prevSentiment);

        if (sentimentChanged) {
            final Instant oldLastUpdated = onboardingRepository
                .findLastStatusUpdateByUserId(user.getUserId());
            onboardingRepository.clearMessagesByRecipientId(user.getUserId());

            // throttle pushes
            if (oldLastUpdated.isBefore(timestamp.minusSeconds(10))) {
                onboardingRepository
                    .findOtherUsersInGroup(group.getGroupId(), user.getUserId())
                    .forEach(recipient -> sendPushMessageStatusChanged(recipient, user));
            }

        }
    }

    private void sendPushMessageStatusChanged(User recipient, User currentUser) {
        onboardingRepository.findDevicesByUserId(recipient.getUserId())
            .forEach(device -> pushNotificationService.sendMessage(
                device.getFcmToken(), "Familiarise",
                currentUser.getName() != null
                    ? "Wetteränderung bei " + currentUser.getName() + "!"
                    : "Das Wetter eines Mitglieds hat sich geändert!",
                Optional.empty(),
                Optional.empty()
            ));
    }

    private void sendPushMessageUserJoined(User recipient, User newUser,
        Group group) {
        onboardingRepository.findDevicesByUserId(recipient.getUserId())
            .forEach(device -> pushNotificationService.sendMessage(
                    device.getFcmToken(), "Familiarise",
                    newUser.getName() != null
                        ? "Begrüße unser neues Mitglied: " + newUser.getName() + "!"
                        : "Neues Mitglied!",
                    Optional.empty(),
                    Optional.empty()
            ));
    }

    private void sendPushMessageUserLeft(
        final User recipient, final User newUser, final Group group) {
        onboardingRepository.findDevicesByUserId(recipient.getUserId())
            .forEach(device -> pushNotificationService.sendMessage(
                device.getFcmToken(), "Familiarise",
                newUser.getName() != null
                    ? "Unser Mitglied " + newUser.getName() + " hat die Gruppe verlassen!"
                    : "Ein Mitglied hat die Gruppe verlassen!",
                Optional.empty(),
                Optional.empty()
            ));
    }

    public List<User> listOtherUsersForDashboard(final User user, final UUID groupId) {
        final List<User> otherUsers = onboardingRepository.findOtherUsersInGroup(groupId, user.getUserId());

        return otherUsers.stream()
            .sorted(Comparator.comparing((User otherUser) ->
                onboardingRepository.findLastStatusUpdateByUserId(otherUser.getUserId()))
                    .reversed()
                    .thenComparing(User::getUserId)) // fallback in case the timestamp match
                .collect(Collectors.toList());
    }

}
