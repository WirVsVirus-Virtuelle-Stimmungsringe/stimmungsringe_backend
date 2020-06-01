package de.wirvsvirus.hack.rest;

import de.wirvsvirus.hack.mock.MockFactory;
import de.wirvsvirus.hack.model.Message;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import de.wirvsvirus.hack.rest.dto.MessageInboxResponse;
import de.wirvsvirus.hack.rest.dto.MessageResponse;
import de.wirvsvirus.hack.service.MessageService;
import de.wirvsvirus.hack.spring.UserInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/inbox")
@Slf4j
public class MessageInboxController {

    @Autowired
    private OnboardingRepository onboardingRepository;

    @Autowired
    private MessageService messageService;

    @GetMapping
    public MessageInboxResponse getInbox() {
        final User currentUser = onboardingRepository.lookupUserById(UserInterceptor.getCurrentUserId());

        final MessageInboxResponse inboxResponse = buildMessageInbox(currentUser);

        return inboxResponse;
    }

    @PostMapping("/send/{recipientUserId}")
    public void sendMessage(@NotNull @PathVariable("recipientUserId") final UUID recipientUserId) {
        final User currentUser = onboardingRepository.lookupUserById(UserInterceptor.getCurrentUserId());
        final User recipient = onboardingRepository.lookupUserById(recipientUserId);

        messageService.sendMessage(currentUser, recipient);

    }

    private MessageInboxResponse buildMessageInbox(User currentUser) {

        // FIXME test
        if (onboardingRepository.findMessagesByRecipientId(currentUser.getUserId()).size() < 4) {
            messageService.sendMessage(onboardingRepository.lookupUserById(MockFactory.frida.getUserId()),
                    currentUser);

        }

        final List<Message> messages = onboardingRepository.findMessagesByRecipientId(currentUser.getUserId());

        final List<MessageResponse> responseList = messages.stream()
                .map(message -> MessageResponse.builder().text(message.getText()).build())
                .collect(Collectors.toList());

        return MessageInboxResponse.builder().messages(responseList).build();
    }

}
