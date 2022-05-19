package de.wirvsvirus.hack.rest;

import com.google.common.base.Preconditions;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import de.wirvsvirus.hack.rest.dto.OtherStatusPageResponse;
import de.wirvsvirus.hack.rest.dto.SuggestionResponse;
import de.wirvsvirus.hack.rest.dto.UserMinimalResponse;
import de.wirvsvirus.hack.service.RoleBasedTextSuggestionsService;
import de.wirvsvirus.hack.spring.UserInterceptor;
import java.util.List;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/otherstatuspage")
@Slf4j
public class OtherStatusPageController {

  @Autowired
  private OnboardingRepository userRepository;

  @Autowired
  private RoleBasedTextSuggestionsService suggestionsService;

  @GetMapping(value = "/{otherUserId}")
  public OtherStatusPageResponse viewOtherStatusPage(
      @PathVariable("otherUserId") @NotNull UUID otherUserId) {

    Preconditions.checkState(
        !otherUserId.equals(UserInterceptor.getCurrentUserId()),
        "Cannot have others' perspective on your own page");

    final User otherUser = userRepository.lookupUserById(otherUserId);

    final UserMinimalResponse me = Mappers.mapResponseFromDomain(otherUser,
        AvatarUrlResolver::getUserAvatarUrls);
    final Sentiment sentiment = userRepository.findSentimentByUserId(otherUserId);
    final String sentimentText = userRepository.findSentimentTextByUserId(otherUserId);

    final List<SuggestionResponse> suggestions =
        StreamEx.of(otherUser.getRoles())
            .flatMap(role -> suggestionsService.forOthers(role).stream())
            .map(text -> SuggestionResponse.builder().text(text).build())
            .toList();

    return OtherStatusPageResponse.builder()
        .user(me)
        .sentiment(sentiment)
        .sentimentText(sentimentText)
        .suggestions(suggestions)
        .build();
  }

}
