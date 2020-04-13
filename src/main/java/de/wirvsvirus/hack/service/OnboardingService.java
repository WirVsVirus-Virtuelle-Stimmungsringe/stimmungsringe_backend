package de.wirvsvirus.hack.service;

import com.google.common.base.Preconditions;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import de.wirvsvirus.hack.service.dto.GroupSettingsDto;
import de.wirvsvirus.hack.service.dto.UserSettingsDto;
import de.wirvsvirus.hack.service.dto.UserSignedInDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class OnboardingService {

    @Autowired
    private OnboardingRepository onboardingRepository;

    public UserSignedInDto signin(final String deviceIdentifier) {

        if ("0000".equals(deviceIdentifier)) {
            final User newUser = new User(UUID.randomUUID(), deviceIdentifier);
            newUser.setName("onboarding-fake");
            newUser.setRoles(Collections.emptyList());
            onboardingRepository.createNewUser(newUser, Sentiment.cloudyNight);
            log.warn("Fake onboarding user {}" + newUser);
            return UserSignedInDto.builder()
                    .userId(newUser.getUserId())
                    .group(Optional.empty())
                    .build();
        }

        final Optional<User> userLookup =
                onboardingRepository.findByDeviceIdentifier(deviceIdentifier);

        if (!userLookup.isPresent()) {
            log.info("User not found - create blank user and assign deviceIdentfier");

            Preconditions.checkState(deviceIdentifier.length() >= 3);
            final User newUser = new User(UUID.randomUUID(), deviceIdentifier);
            newUser.setName("noname");
            newUser.setRoles(Collections.emptyList());
            onboardingRepository.createNewUser(newUser, Sentiment.sunnyWithClouds);
            return UserSignedInDto.builder()
                    .userId(newUser.getUserId())
                    .group(Optional.empty())
                    .build();
        } else {
            final Optional<Group> group = onboardingRepository.findGroupForUser(
                    userLookup.get().getUserId());

            log.info("User {} signed in - group is {}", userLookup.get(), group);

            if (group.isPresent()) {
                return UserSignedInDto.builder()
                        .userId(userLookup.get().getUserId())
                        .group(group)
                        .build();
            } else {
                return UserSignedInDto.builder()
                        .userId(userLookup.get().getUserId())
                        .group(group)
                        .build();
            }
        }

    }

    public void updateUser(final User user, final UserSettingsDto userSettings) {
        final String name = userSettings.getName();


        if (name.isEmpty()) {
            return;
        }

        userSettings.setName(StringUtils.trim(name));

        onboardingRepository.updateUser(user.getUserId(), userSettings);
    }

    public void updateGroup(final Optional<Group> group, final GroupSettingsDto groupSettings) {
        final String groupName = groupSettings.getGroupName();

        if (groupName.isEmpty()) {
            return;
        }

        groupSettings.setGroupName(StringUtils.trim(groupSettings.getGroupName()));

        onboardingRepository.updateGroup(group.get().getGroupId(), groupSettings);
    }

    public void joinGroup(UUID groupId, User user) {
        log.info("User {} joining group {}", user.getName(), groupId);

        final Optional<Group> currentGroup = onboardingRepository.findGroupByUser(user.getUserId());
        if (currentGroup.isPresent()) {
            if (currentGroup.get().getGroupId().equals(groupId)) {
                log.info("User is already member of requested group");
            } else {
                log.info("User is already member of another group");
            }
        } else {
            final Optional<Group> lookup = onboardingRepository.findGroupById(groupId);
            Preconditions.checkState(lookup.isPresent(), "Group <%s> does not exist", groupId);
            onboardingRepository.joinGroup(lookup.get().getGroupId(), user.getUserId());
        }

    }

    public void leaveGroup(final UUID groupId, final User user) {
        log.info("User {} leaving group {}", user.getName(), groupId);

        final Optional<Group> currentGroup = onboardingRepository.findGroupByUser(user.getUserId());
        if (currentGroup.isPresent()) {
            if (currentGroup.get().getGroupId().equals(groupId)) {
                final Optional<Group> lookup = onboardingRepository.findGroupById(groupId);
                Preconditions.checkState(lookup.isPresent(), "Group <%s> does not exist", groupId);
                onboardingRepository.leaveGroup(lookup.get().getGroupId(), user.getUserId());
                log.info("... remove user {} from group {} with groupId {}", user.getUserId(), currentGroup.get().getGroupName(), currentGroup.get().getGroupId());
            } else {
                log.info("User is member of another group");
            }
        } else {
            log.info("User is not member of any group");
        }

    }

    public Group startNewGroup(final User user, final String groupName) {
        log.info("New group {} by user {}", groupName, user.getName());

        Preconditions.checkState(groupName.length() >= 3);
        String groupCode = GroupCodeUtil.generateGroupCode();
        final Group newGroup = onboardingRepository.startNewGroup(groupName, groupCode);
        onboardingRepository.joinGroup(newGroup.getGroupId(), user.getUserId());
        log.info("...started new group {} with groupid {}", newGroup.getGroupName(), newGroup.getGroupId());
        return newGroup;

    }


}
