package de.wirvsvirus.hack.rest;

import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import de.wirvsvirus.hack.rest.dto.DashboardResponse;
import de.wirvsvirus.hack.rest.dto.GroupDataResponse;
import de.wirvsvirus.hack.rest.dto.MyTileResponse;
import de.wirvsvirus.hack.rest.dto.OtherTileResponse;
import de.wirvsvirus.hack.rest.dto.UserMinimalResponse;
import de.wirvsvirus.hack.service.OnboardingService;
import de.wirvsvirus.hack.spring.UserInterceptor;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@Slf4j
public class DashboardController {

  @Autowired
  private OnboardingRepository onboardingRepository;

  @Autowired
  private OnboardingService onboardingService;

  @GetMapping
  public DashboardResponse dashboardView() {
    final User currentUser = onboardingRepository.lookupUserById(
        UserInterceptor.getCurrentUserId());

    final Optional<Group> group = onboardingRepository.findGroupByUser(currentUser.getUserId());

    return DashboardResponse.builder()
        .myTile(buildMyTileResponse(currentUser))
        .otherTiles(buildOtherTileResponseList(currentUser, group))
        .groupData(buildGroupData(group))
        .build();
  }

  private MyTileResponse buildMyTileResponse(final User currentUser) {
    final UserMinimalResponse me = Mappers.mapResponseFromDomain(currentUser,
        AvatarUrlResolver::getUserAvatarUrls);

    final Sentiment sentiment = onboardingRepository.findSentimentByUserId(currentUser.getUserId());
    final String sentimentText = onboardingRepository.findSentimentTextByUserId(
        currentUser.getUserId());
    final Instant lastStatusUpdate = onboardingRepository.findLastStatusUpdateByUserId(
        currentUser.getUserId());

    return MyTileResponse.builder()
        .user(me)
        .sentiment(sentiment)
        .sentimentText(sentimentText)
        .lastStatusUpdate(lastStatusUpdate)
        .build();
  }

  private List<OtherTileResponse> buildOtherTileResponseList(final User currentUser,
      final Optional<Group> group) {
    final List<User> otherUsersInGroup;
    if (group.isPresent()) {
      otherUsersInGroup = onboardingService.listOtherUsersForDashboard(currentUser,
          group.get().getGroupId());
    } else {
      otherUsersInGroup = Collections.emptyList();
    }

    final List<OtherTileResponse> otherTiles = new ArrayList<>();
    for (final User otherUser : otherUsersInGroup) {
      final UserMinimalResponse other = Mappers.mapResponseFromDomain(otherUser,
          AvatarUrlResolver::getUserAvatarUrls);

      final Sentiment sentiment = onboardingRepository.findSentimentByUserId(otherUser.getUserId());
      final String sentimentText = onboardingRepository.findSentimentTextByUserId(
          otherUser.getUserId());
      final Instant lastStatusUpdate = onboardingRepository.findLastStatusUpdateByUserId(
          otherUser.getUserId());

      otherTiles.add(
          OtherTileResponse.builder()
              .user(other)
              .sentiment(sentiment)
              .sentimentText(sentimentText)
              .lastStatusUpdate(lastStatusUpdate)
              .build()
      );
    }
    return otherTiles;
  }

  private GroupDataResponse buildGroupData(final Optional<Group> groupOptional) {
    return groupOptional.map(group ->
        GroupDataResponse.builder()
            .groupId(group.getGroupId())
            .groupName(group.getGroupName())
            .groupCode(group.getGroupCode())
            .build()
    ).orElse(null);
  }


}
