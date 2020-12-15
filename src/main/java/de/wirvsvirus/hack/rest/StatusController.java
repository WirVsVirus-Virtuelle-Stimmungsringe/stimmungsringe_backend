package de.wirvsvirus.hack.rest;


import com.google.common.base.Preconditions;
import de.wirvsvirus.hack.exception.PushMessageNotSendException;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import de.wirvsvirus.hack.rest.dto.UpdateStatusRequest;
import de.wirvsvirus.hack.service.OnboardingService;
import de.wirvsvirus.hack.service.PushNotificationService;
import de.wirvsvirus.hack.spring.UserInterceptor;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/mystatus")
@Slf4j
public class StatusController {

    @Autowired
    private OnboardingRepository onboardingRepository;

    @Autowired
    private OnboardingService onboardingService;

    @PutMapping
    public void updateStatus(@Valid @RequestBody UpdateStatusRequest request) {
        final User currentUser = onboardingRepository.lookupUserById(UserInterceptor.getCurrentUserId());
        Preconditions.checkNotNull(request.getSentiment(), "sentiment must not be null");

        log.info("Updating status for user {} to {}", currentUser.getUserId(), request.getSentiment());

        onboardingService.updateSentimentStatus(currentUser, request.getSentiment());
    }

}
