package de.wirvsvirus.hack.rest;


import com.google.common.base.Preconditions;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import de.wirvsvirus.hack.rest.dto.UpdateStatusRequest;
import de.wirvsvirus.hack.spring.UserInterceptor;
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
    private OnboardingRepository userRepository;

    @PutMapping
    public void updateStatus(@Valid @RequestBody UpdateStatusRequest request) {
        final User currentUser = userRepository.findByUserId(UserInterceptor.getCurrentUserId());
        Preconditions.checkNotNull(request.getSentiment(), "sentiment must not be null");

        log.info("Updating status for user {} to {}", currentUser.getId(), request.getSentiment());
        userRepository.updateStatus(currentUser.getId(), request.getSentiment());
    }

}
