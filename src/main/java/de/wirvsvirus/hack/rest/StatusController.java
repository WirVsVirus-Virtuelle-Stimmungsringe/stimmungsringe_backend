package de.wirvsvirus.hack.rest;


import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.model.UserRepository;
import de.wirvsvirus.hack.rest.dto.UpdateStatusRequest;
import de.wirvsvirus.hack.spring.UserInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mystatus")
@Slf4j
public class StatusController {

    @Autowired
    private UserRepository userRepository;

    @PutMapping
    public void updateStatus(@RequestBody UpdateStatusRequest request) {
        final User currentUser = userRepository.findByUserId(UserInterceptor.getCurrentUserId());

        log.info("Updating status for user {} to {}", currentUser.getId(), request.getSentimentCode());
        userRepository.updateStatus(currentUser.getId(), request.getSentimentCode());
    }

}
