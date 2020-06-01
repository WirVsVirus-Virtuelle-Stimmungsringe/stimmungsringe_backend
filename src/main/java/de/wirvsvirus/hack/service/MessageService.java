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
public class MessageService {

    @Autowired
    private OnboardingRepository onboardingRepository;

    public void sendMessage(final User sender, final User recipient) {
        final Group group1 = onboardingRepository.findGroupByUser(sender.getUserId())
                .orElseThrow(() -> new IllegalStateException());
        final Group group2 = onboardingRepository.findGroupByUser(recipient.getUserId())
                .orElseThrow(() -> new IllegalStateException());
        Preconditions.checkState(group1.getGroupId().equals(group2.getGroupId()),
                "Users must be in same group");
        Preconditions.checkState(
                !recipient.getUserId().equals(sender.getUserId()),
                "Cannot send message to himself");


        log.warn("Send Message from {} to {}", sender.getUserId(), recipient.getUserId());

        onboardingRepository.sendMessage(sender, recipient, "Ich denk' an dich!");
    }

}
