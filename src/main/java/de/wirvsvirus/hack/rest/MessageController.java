package de.wirvsvirus.hack.rest;

import de.wirvsvirus.hack.mock.MockFactory;
import de.wirvsvirus.hack.model.Message;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import de.wirvsvirus.hack.rest.dto.*;
import de.wirvsvirus.hack.service.MessageService;
import de.wirvsvirus.hack.service.dto.MessageTemplateDto;
import de.wirvsvirus.hack.spring.UserInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/message")
@Slf4j
public class MessageController {

    @Autowired
    private OnboardingRepository onboardingRepository;

    @Autowired
    private MessageService messageService;

    @Autowired
    private AvatarUrlResolver avatarUrlResolver;

    @GetMapping("/inbox")
    public MessageInboxResponse getInbox() {
        final User currentUser = onboardingRepository.lookupUserById(UserInterceptor.getCurrentUserId());

        final MessageInboxResponse inboxResponse = buildMessageInbox(currentUser);

        return inboxResponse;
    }

    @PostMapping("/send/{recipientUserId}")
    public AvailableMessagesResponse sendMessage(
            @RequestBody @Valid final SendMessageRequest request,
            @NotNull @PathVariable("recipientUserId") final UUID recipientUserId) {
        final User currentUser = onboardingRepository.lookupUserById(UserInterceptor.getCurrentUserId());
        final User recipient = onboardingRepository.lookupUserById(recipientUserId);

        messageService.sendMessage(currentUser, recipient, request.getText());

        return availableMessages(recipient.getUserId());
    }

    @GetMapping("/available-messages/{recipientUserId}")
    public AvailableMessagesResponse getAvailableMessages(@NotNull @PathVariable("recipientUserId") final UUID recipientUserId) {

        return availableMessages(recipientUserId);
    }

    /**
     * enumerate message templates available for sending to a user
     */
    private AvailableMessagesResponse availableMessages(final UUID recipientUserId) {
        final User currentUser = onboardingRepository.lookupUserById(UserInterceptor.getCurrentUserId());
        final User recipient = onboardingRepository.lookupUserById(recipientUserId);

        final List<MessageTemplateDto> availableMessages =
                messageService.calcAvailableMessages(currentUser, recipient);

        return AvailableMessagesResponse.builder()
                .messageTemplates(availableMessages.stream()
                        .map(template -> MessageTemplate.builder()
                                .used(template.isUsed())
                                .text(template.getText())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    private MessageInboxResponse buildMessageInbox(User currentUser) {

        final List<Message> messages = onboardingRepository.findMessagesByRecipientId(currentUser.getUserId());

        final List<MessageResponse> responseList = messages.stream()
                .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
                .map(message -> MessageResponse.builder()
                        .createdAt(message.getCreatedAt())
                        .senderUser(resolveSenderUser(message.getSenderUserId()))
                        .text(message.getText())
                        .build())
                .collect(Collectors.toList());

        return MessageInboxResponse.builder().messages(responseList).build();
    }

    private UserMinimalResponse resolveSenderUser(UUID senderUserId) {
        final User senderUser = onboardingRepository.lookupUserById(senderUserId);
        return Mappers.mapResponseFromDomain(senderUser,
                avatarUrlResolver::getUserAvatarUrl);
    }

}
