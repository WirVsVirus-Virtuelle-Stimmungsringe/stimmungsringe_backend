package de.wirvsvirus.hack.service;

import com.google.common.base.Preconditions;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.service.dto.UserSignedInDto;
import lombok.extern.slf4j.Slf4j;
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

        final Optional<User> userLookup =
                onboardingRepository.findByDeviceIdentifier(deviceIdentifier);

        if (!userLookup.isPresent()) {
            log.info("User not found - create blank user and assign deviceIdentfier");

            Preconditions.checkState(deviceIdentifier.length() >= 3);
            final User newUser = new User(UUID.randomUUID(), deviceIdentifier);
            newUser.setName("noname");
            newUser.setRoles(Collections.emptyList());
            onboardingRepository.createNewUser(newUser);
            return UserSignedInDto.builder()
                    .userId(newUser.getUserId())
                    .groupName(Optional.empty())
                    .build();
        } else {
            final Optional<Group> group = onboardingRepository.findGroupNameForUser(
                userLookup.get().getUserId());

            if (group.isPresent()) {
                return UserSignedInDto.builder()
                        .userId(userLookup.get().getUserId())
                        .groupName(group.map(Group::getGroupName))
                        .build();
            } else {
                return UserSignedInDto.builder()
                        .userId(userLookup.get().getUserId())
                        .groupName(group.map(Group::getGroupName))
                        .build();
            }
        }

    }

    public void joinGroup(UUID groupId, User user) {
        log.info("User {} joining group {}", user.getName(), groupId);

        final Optional<Group> currentGroup = onboardingRepository.findGroupByUser(user.getUserId());
        if (currentGroup.isPresent()) {
            if (currentGroup.get().getGroupId().equals(groupId)) {
                log.info("User is already member of group");
            } else {
                throw new IllegalStateException("User is already member of a currentGroup - " + currentGroup.get());
            }
        } else {
            final Optional<Group> lookup = onboardingRepository.findGroupById(groupId);
            Preconditions.checkState(lookup.isPresent(), "Group <%s> does not exist", groupId);
            onboardingRepository.joinGroup(lookup.get().getGroupId(), user.getUserId());
        }

    }

    public Group startNewGroup(final User user, final String groupName) {
        log.info("New group {} by user {}", groupName, user.getName());

        final boolean groupExists = onboardingRepository.findGroupByName(groupName).isPresent();

        if (groupExists) {
            throw new IllegalStateException("Cannot start group - group name already taken");
        } else {
            Preconditions.checkState(groupName.length() >= 3);
            final Group newGroup = onboardingRepository.startNewGroup(groupName);
            onboardingRepository.joinGroup(newGroup.getGroupId(), user.getUserId());
            log.info("...started new group {} with groupid {}", newGroup.getGroupName(), newGroup.getGroupId());
            return newGroup;
        }

    }
}
