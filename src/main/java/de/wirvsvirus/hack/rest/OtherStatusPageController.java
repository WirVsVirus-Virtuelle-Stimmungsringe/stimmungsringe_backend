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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/otherstatuspage")
@Slf4j
public class OtherStatusPageController {

    @Autowired
    private OnboardingRepository userRepository;

    @Autowired
    private RoleBasedTextSuggestionsService suggestionsService;

    @Autowired
    private AvatarUrlResolver avatarUrlResolver;

    @GetMapping(value = "/{otherUserId}")
    public OtherStatusPageResponse viewOtherStatusPage(
            @PathVariable("otherUserId") @NotNull UUID otherUserId) {

        Preconditions.checkState(
                !otherUserId.equals(UserInterceptor.getCurrentUserId()),
                "Cannot have others' perspective on your own page");

        final User otherUser = userRepository.lookupUserById(otherUserId);

        OtherStatusPageResponse response = new OtherStatusPageResponse();

        final UserMinimalResponse me = Mappers.mapResponseFromDomain(otherUser, avatarUrlResolver::getUserAvatarUrl);
        final Sentiment sentiment = userRepository.findSentimentByUserId(otherUserId);
        final String sentimentText = userRepository.findSentimentTextByUserId(otherUserId);

        final List<SuggestionResponse> suggestions = new ArrayList<>();

        otherUser.getRoles().stream()
                .flatMap(role -> suggestionsService.forOthers(role).stream())
                .map(text -> {
                    final SuggestionResponse sugg = new SuggestionResponse();
                    sugg.setText(text);
                    return sugg;
                }).forEach(suggestions::add);

        response.setUser(me);
        response.setSentiment(sentiment);
        response.setSentimentText(sentimentText);
        response.setSuggestions(suggestions);

        return response;
    }

}
