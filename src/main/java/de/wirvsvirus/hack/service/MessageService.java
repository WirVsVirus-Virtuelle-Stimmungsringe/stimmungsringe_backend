package de.wirvsvirus.hack.service;

import com.google.common.base.Preconditions;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Message;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import de.wirvsvirus.hack.service.dto.MessageTemplateDto;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MessageService {

    @Autowired
    private OnboardingRepository onboardingRepository;

    public void sendMessage(final User sender, final User recipient, final String text) {
        Preconditions.checkArgument(StringUtils.isNotBlank(text));
        final Group group1 = onboardingRepository.findGroupByUser(sender.getUserId())
                .orElseThrow(() -> new IllegalStateException());
        final Group group2 = onboardingRepository.findGroupByUser(recipient.getUserId())
                .orElseThrow(() -> new IllegalStateException());
        Preconditions.checkState(group1.getGroupId().equals(group2.getGroupId()),
                "Users must be in same group");
        Preconditions.checkState(
                !recipient.getUserId().equals(sender.getUserId()),
                "Cannot send message to himself");


        log.warn("Send Message from {} to {}: {}", sender.getUserId(), recipient.getUserId(), text);

        onboardingRepository.sendMessage(sender, recipient, text);
    }

    public List<MessageTemplateDto> calcAvailableMessages(final User currentUser, final User recipient) {
        final Set<String> usedMessageTexts = onboardingRepository.findMessagesByRecipientId(recipient.getUserId())
                .stream()
                .filter(message -> message.getSenderUserId().equals(currentUser.getUserId()))
                .map(Message::getText)
                .collect(Collectors.toSet());
        return StreamEx.of("Ich denk' an dich!", "Kopf hoch!")
                .map(text -> {
                    boolean used = usedMessageTexts.contains(text);
                    return MessageTemplateDto.builder().used(used).text(text).build();
                })
                .toList();
    }
}
