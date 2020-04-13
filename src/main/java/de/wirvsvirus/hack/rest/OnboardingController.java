package de.wirvsvirus.hack.rest;

import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import de.wirvsvirus.hack.rest.dto.*;
import de.wirvsvirus.hack.service.OnboardingService;
import de.wirvsvirus.hack.service.dto.UserPropertiesDto;
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

        if (signinResult.getGroup().isPresent()) {
            final Group group = signinResult.getGroup().get();
            return
                    SigninUserResponse.builder()
                            .userId(userId)
                            .hasGroup(true)
                            .groupId(group.getGroupId().toString())
                            .groupName(group.getGroupName())
                            .build();

        } else {
            return
                    SigninUserResponse.builder()
                            .userId(userId)
                            .hasGroup(false)
                            .build();

        }
    }

    @PutMapping("/user/properties")
    public void updateUserProperties(@RequestBody @Valid final UpdateUserPropertiesRequest request) {
        final User user = onboardingRepository.lookupUserById(UserInterceptor.getCurrentUserId());

        onboardingService.updateUser(user,
                UserPropertiesDto.builder()
                        .name(request.getName())
                        .build());

    }

    /**
     * used for group settings
     */
    @GetMapping("/group/settings")
    public GroupSettingsResponse getGroupSettings() {
        final Group group = onboardingRepository.findGroupByUser(UserInterceptor.getCurrentUserId())
                .orElseThrow(() -> new IllegalStateException("User not in a group"));

        return GroupSettingsResponse.builder()
                .groupId(group.getGroupId())
                .groupName(group.getGroupName())
                .groupCode(group.getGroupCode())
                .build();
    }

    @PutMapping("/group/join")
    public void joinGroup(@RequestBody @Valid final JoinGroupRequest request) {
        final User user = onboardingRepository.lookupUserById(UserInterceptor.getCurrentUserId());

        onboardingService.joinGroup(request.getGroupId(), user);
    }

    @PutMapping("/group/leave")
    public void leaveGroup(@RequestBody @Valid final LeaveGroupRequest request) {
        final User user = onboardingRepository.lookupUserById(UserInterceptor.getCurrentUserId());

        onboardingService.leaveGroup(request.getGroupId(), user);
    }

    @PostMapping("/group/start")
    public ResponseEntity<StartNewGroupResponse> startNewGroup(@RequestBody @Valid final StartNewGroupRequest request) {
        final User user = onboardingRepository.lookupUserById(UserInterceptor.getCurrentUserId());

        final Group newGroup = onboardingService.startNewGroup(user, request.getGroupName());
        return ResponseEntity.ok(
                StartNewGroupResponse.builder()
                        .groupId(newGroup.getGroupId())
                        .groupName(newGroup.getGroupName())
                        .build());

    }

    @PostMapping("/group-by-code")
    public ResponseEntity<FindGroupResponse> getGroupByCode(@RequestBody @Valid final FindGroupRequest request) {

        final Optional<Group> match =
                onboardingRepository.findGroupByCode(request.getGroupCode());

        return match.map(group -> ResponseEntity.ok(
                FindGroupResponse.builder()
                        .groupId(group.getGroupId())
                        .groupName(group.getGroupName())
                        .build())
        ).orElseGet(() -> ResponseEntity.noContent().build());

    }

}
