package de.wirvsvirus.hack.service;

import com.google.common.base.Preconditions;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Message;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class MessagingService {

    @Autowired
    private OnboardingRepository onboardingRepository;

    public void sendMessage(final User otherUser, final User currentUser) {
        final Group group1 = onboardingRepository.findGroupByUser(currentUser.getUserId())
                .orElseThrow(() -> new IllegalStateException());
        final Group group2 = onboardingRepository.findGroupByUser(otherUser.getUserId())
                .orElseThrow(() -> new IllegalStateException());
        Preconditions.checkState(group1.getGroupId().equals(group2.getGroupId()),
                "Users must be in same group");

        log.warn("Send Message from {} to {}", currentUser.getUserId(), otherUser.getUserId());

        final Message message = new Message();
        message.setSenderUserId(currentUser.getUserId());
        message.setReceipientUserId(otherUser.getUserId());
        message.setText("Ich denk' an dich!");
        onboardingRepository.sendMessage(message);


    }

}
