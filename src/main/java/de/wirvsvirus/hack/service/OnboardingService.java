package de.wirvsvirus.hack.service;

import de.wirvsvirus.hack.mock.MockFactory;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.service.dto.UserSignedInDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class OnboardingService {

    @Autowired
    private OnboardingRepository onboardingRepository;

    public UserSignedInDto signin(final String deviceIdentifier) {

        final Optional<User> userLookup =
                MockFactory.findByDeviceIdentifier(deviceIdentifier);

        if (!userLookup.isPresent()) {
            // TODO add user if not exists
            throw new IllegalStateException("user not found by device");
        } else {
            final Optional<String> groupName = onboardingRepository.findGroupNameForUser(
                userLookup.get().getId());

            if (groupName.isPresent()) {
                return UserSignedInDto.builder()
                        .userId(userLookup.get().getId())
                        .groupName(groupName).build();
            } else {
                return UserSignedInDto.builder()
                        .userId(userLookup.get().getId())
                        .groupName(groupName)
                        .build();
            }
        }

    }

    public void joinGroup(String groupName, User user) {
        log.info("User {} joining group {}", user.getName(), groupName);

        final Optional<String> group = onboardingRepository.findGroupNameByUser(user.getId());
        if (group.isPresent()) {
            if (group.get().equals(groupName)) {
                log.info("User is already member of group");
            } else {
                throw new IllegalStateException("User is already member of a group - " + group.get());
            }
        } else {
            onboardingRepository.joinGroup(groupName, user.getId());
        }

    }

    public void startNewGroup(final User user, final String groupName) {
        log.info("New group {} by user {}", groupName, user.getName());

        final boolean groupExists = MockFactory.allGroups.contains(groupName);

        if (groupExists) {
            throw new IllegalStateException("Cannot start group - group name already taken");
        } else {
            onboardingRepository.startNewGroup(groupName);
            onboardingRepository.joinGroup(groupName, user.getId());
        }

    }
}
