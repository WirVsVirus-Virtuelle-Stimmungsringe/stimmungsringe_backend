package de.wirvsvirus.hack.service;

import com.google.common.base.Preconditions;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessagingService {

    @Autowired
    private OnboardingRepository onboardingRepository;

    public void sendMessage(final User receipient, final User currentUser) {
        final Group group1 = onboardingRepository.findGroupByUser(currentUser.getUserId())
                .orElseThrow(() -> new IllegalStateException());
        final Group group2 = onboardingRepository.findGroupByUser(receipient.getUserId())
                .orElseThrow(() -> new IllegalStateException());
        Preconditions.checkState(group1.getGroupId().equals(group2.getGroupId()),
                "Users must be in same group");

        log.warn("Send Message from {} to {}", currentUser.getUserId(), receipient.getUserId());

        onboardingRepository.sendMessage(currentUser, receipient, "Ich denk' an dich!");
    }

}
