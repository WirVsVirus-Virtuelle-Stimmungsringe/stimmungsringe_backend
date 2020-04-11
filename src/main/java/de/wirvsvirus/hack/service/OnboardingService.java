package de.wirvsvirus.hack.service;

import com.google.common.base.Preconditions;
import de.wirvsvirus.hack.mock.MockFactory;
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
            MockFactory.allUsers.put(newUser.getUserId(), newUser);
            throw new IllegalStateException("user not found by device");
        } else {
            final Optional<String> groupName = onboardingRepository.findGroupNameForUser(
                userLookup.get().getUserId());

            if (groupName.isPresent()) {
                return UserSignedInDto.builder()
                        .userId(userLookup.get().getUserId())
                        .groupName(groupName).build();
            } else {
                return UserSignedInDto.builder()
                        .userId(userLookup.get().getUserId())
                        .groupName(groupName)
                        .build();
            }
        }

    }

    public void joinGroup(String groupName, User user) {
        log.info("User {} joining group {}", user.getName(), groupName);

        final Optional<String> group = onboardingRepository.findGroupNameByUser(user.getUserId());
        if (group.isPresent()) {
            if (group.get().equals(groupName)) {
                log.info("User is already member of group");
            } else {
                throw new IllegalStateException("User is already member of a group - " + group.get());
            }
        } else {
            final Optional<String> lookup = onboardingRepository.findGroupByName(groupName);
            Preconditions.checkState(lookup.isPresent(), "Group <%s> does not exist", groupName);
            onboardingRepository.joinGroup(groupName, user.getUserId());
        }

    }

    public void startNewGroup(final User user, final String groupName) {
        log.info("New group {} by user {}", groupName, user.getName());

        final boolean groupExists = MockFactory.allGroups.contains(groupName);

        if (groupExists) {
            throw new IllegalStateException("Cannot start group - group name already taken");
        } else {
            Preconditions.checkState(groupName.length() >= 3);
            onboardingRepository.startNewGroup(groupName);
            onboardingRepository.joinGroup(groupName, user.getUserId());
        }

    }
}
