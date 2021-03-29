package de.wirvsvirus.hack.rest;

import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import de.wirvsvirus.hack.repository.microstream.MicrostreamBackupService;
import de.wirvsvirus.hack.rest.dto.*;
import de.wirvsvirus.hack.service.OnboardingService;
import de.wirvsvirus.hack.service.PushNotificationService;
import de.wirvsvirus.hack.service.dto.DeviceType;
import de.wirvsvirus.hack.service.dto.GroupSettingsDto;
import de.wirvsvirus.hack.service.dto.UserSettingsDto;
import de.wirvsvirus.hack.service.dto.UserSignedInDto;
import de.wirvsvirus.hack.spring.UserInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/onboarding")
@Slf4j
public class OnboardingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MicrostreamBackupService.class);

    @Autowired
    private OnboardingService onboardingService;

    @Autowired
    private OnboardingRepository onboardingRepository;

    @Autowired
    private PushNotificationService pushNotificationService;

    @PutMapping("/signin")
    public SigninUserResponse signin(@RequestBody @Valid final SigninUserRequest request) {

        final UserSignedInDto signinResult = onboardingService.signin(request.getDeviceIdentifier());
        final String userId = signinResult.getUserId().toString();
        if (request.getFcmToken() != null) {
            pushNotificationService.registerFcmTokenForUser(signinResult.getUserId(),
                request.getDeviceIdentifier(),
                DeviceType.valueOf(request.getDeviceType()),
                request.getFcmToken());
        }

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

    @PutMapping("/user/settings")
    public void updateUserSettings(@RequestBody @Valid final UpdateUserSettingsRequest request) {
        final User currentUser = onboardingRepository.lookupUserById(UserInterceptor.getCurrentUserId());

        onboardingService.updateUser(currentUser,
                UserSettingsDto.builder()
                        .name(request.getName())
                        .stockAvatar(request.getStockAvatar())
                        .build());

    }

    @PutMapping("/group/{groupId}/settings")
    public void updateGroupSettings(@RequestBody @Valid final UpdateGroupSettingsRequest request,
                                    @NotNull @PathVariable("groupId") final UUID groupId) {
        final User currentUser = onboardingRepository.lookupUserById(UserInterceptor.getCurrentUserId());

        final Group group = onboardingService.lookupGroupCheckPermissions(currentUser, groupId);

        onboardingService.updateGroup(group,
                GroupSettingsDto.builder()
                        .groupName(request.getGroupName())
                        .build());
    }


    /**
     * used for group settings
     */
    @GetMapping("/user/settings")
    public UserSettingsResponse getUserSettings() {
        final User currentUser = onboardingRepository.lookupUserById(UserInterceptor.getCurrentUserId());
        return UserSettingsResponse.builder()
                .userName(currentUser.getName())
                .hasName(currentUser.hasName())
                .stockAvatar(currentUser.getStockAvatar())
                .build();
    }

    /**
     * used for group settings
     */
    @GetMapping("/group/{groupId}/settings")
    public GroupSettingsResponse getGroupSettings(@NotNull @PathVariable("groupId") final UUID groupId) {
        final User currentUser = onboardingRepository.lookupUserById(UserInterceptor.getCurrentUserId());
        final Group group = onboardingService.lookupGroupCheckPermissions(currentUser, groupId);

        return GroupSettingsResponse.builder()
                .groupId(group.getGroupId())
                .groupName(group.getGroupName())
                .groupCode(group.getGroupCode())
                .build();
    }

    @PutMapping("/group/join")
    public ResponseEntity<GroupDataResponse> joinGroup(@RequestBody @Valid final JoinGroupRequest request) {
        final User currentUser = onboardingRepository.lookupUserById(UserInterceptor.getCurrentUserId());

        final Optional<Group> joinedGroup = onboardingService.joinGroup(request.getGroupCode(), currentUser);

        if (!joinedGroup.isPresent()) {
            return ResponseEntity.noContent().build();
        }
        final Group group = joinedGroup.get();

        return ResponseEntity.ok(GroupDataResponse.builder()
            .groupId(group.getGroupId())
            .groupCode(group.getGroupCode())
            .groupName(group.getGroupName())
            .build());
    }

    @PutMapping("/group/leave")
    public void leaveGroup(@RequestBody @Valid final LeaveGroupRequest request) {
        final User currentUser = onboardingRepository.lookupUserById(UserInterceptor.getCurrentUserId());

        onboardingService.leaveGroup(request.getGroupId(), currentUser);
    }

    @PostMapping("/group/start")
    public ResponseEntity<GroupDataResponse> startNewGroup(@RequestBody @Valid final StartNewGroupRequest request) {
        final User currentUser = onboardingRepository.lookupUserById(UserInterceptor.getCurrentUserId());

        final Group newGroup = onboardingService.startNewGroup(currentUser, request.getGroupName());
        return ResponseEntity.ok(
            GroupDataResponse.builder()
                        .groupId(newGroup.getGroupId())
                        .groupName(newGroup.getGroupName())
                        .groupCode(newGroup.getGroupCode())
                        .build());

    }

}
