package de.wirvsvirus.hack.rest;

import de.wirvsvirus.hack.model.OnboardingRepository;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.rest.dto.JoinGroupRequest;
import de.wirvsvirus.hack.rest.dto.SigninUserRequest;
import de.wirvsvirus.hack.rest.dto.SigninUserResponse;
import de.wirvsvirus.hack.rest.dto.StartNewGroupRequest;
import de.wirvsvirus.hack.service.OnboardingService;
import de.wirvsvirus.hack.service.dto.UserSignedInDto;
import de.wirvsvirus.hack.spring.UserInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

        if (signinResult.getGroupName().isPresent()) {
            return
                    SigninUserResponse.builder()
                            .hasGroup(true)
                            .groupName(signinResult.getGroupName().get())
                            .build();

        } else {
            return
                    SigninUserResponse.builder()
                            .hasGroup(false)
                            .build();

        }
    }

    @PostMapping("/group/join")
    public void joinGroup(@RequestBody @Valid final JoinGroupRequest request) {
        final User user = onboardingRepository.findByUserId(UserInterceptor.getCurrentUserId());

        onboardingService.joinGroup(request.getGroupName(), user);

    }

    @PostMapping("/group/start")
    public void startNewGroup(@RequestBody @Valid final StartNewGroupRequest request) {
        final User user = onboardingRepository.findByUserId(UserInterceptor.getCurrentUserId());

        onboardingService.startNewGroup(user, request.getGroupName());

    }

}
