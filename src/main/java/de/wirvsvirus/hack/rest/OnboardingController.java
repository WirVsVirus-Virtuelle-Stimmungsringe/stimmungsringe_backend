package de.wirvsvirus.hack.rest;

import de.wirvsvirus.hack.model.OnboardingRepository;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.rest.dto.*;
import de.wirvsvirus.hack.service.OnboardingService;
import de.wirvsvirus.hack.service.dto.UserSignedInDto;
import de.wirvsvirus.hack.spring.UserInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/onboarding")
@Slf4j
public class OnboardingController {

    @Autowired
    private OnboardingService onboardingService;

    @Autowired
    private OnboardingRepository onboardingRepository;

    @PutMapping("/signin")
    public SigninUserResponse signin(@RequestBody @Valid final SigninUserRequest request) {

        final UserSignedInDto signinResult = onboardingService.signin(request.getDeviceIdentifier());
        final String userId = signinResult.getUserId().toString();

        if (signinResult.getGroupName().isPresent()) {
            return
                    SigninUserResponse.builder()
                            .userId(userId)
                            .hasGroup(true)
                            .groupName(signinResult.getGroupName().get())
                            .build();

        } else {
            return
                    SigninUserResponse.builder()
                            .userId(userId)
                            .hasGroup(false)
                            .build();

        }
    }

    @PutMapping("/group/join")
    public void joinGroup(@RequestBody @Valid final JoinGroupRequest request) {
        final User user = onboardingRepository.findByUserId(UserInterceptor.getCurrentUserId());

        onboardingService.joinGroup(request.getGroupName(), user);

    }

    @PostMapping("/group/start")
    public void startNewGroup(@RequestBody @Valid final StartNewGroupRequest request) {
        final User user = onboardingRepository.findByUserId(UserInterceptor.getCurrentUserId());

        onboardingService.startNewGroup(user, request.getGroupName());

    }

    @PostMapping("/group-by-name")
    public ResponseEntity getGroupByName(@RequestBody @Valid final FindGroupRequest request) {

        final Optional<String> match =
            onboardingRepository.findGroupByName(request.getGroupName());

        if (match.isPresent()) {
            return ResponseEntity.ok(
                    FindGroupResponse.builder().groupName(match.get()).build());
        } else {
            return ResponseEntity.noContent().build();
        }


    }

}
