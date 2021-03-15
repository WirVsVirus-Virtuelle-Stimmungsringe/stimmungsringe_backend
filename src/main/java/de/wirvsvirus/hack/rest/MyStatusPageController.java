package de.wirvsvirus.hack.rest;

import de.wirvsvirus.hack.model.*;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import de.wirvsvirus.hack.rest.dto.*;
import de.wirvsvirus.hack.service.RoleBasedTextSuggestionsService;
import de.wirvsvirus.hack.spring.UserInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/mystatuspage")
@Slf4j
public class MyStatusPageController {

    @Autowired
    private RoleBasedTextSuggestionsService suggestionsService;

    @Autowired
    private OnboardingRepository onboardingRepository;

    @Autowired
    private AvatarUrlResolver avatarUrlResolver;

    @GetMapping
    public MyStatusPageResponse viewMyStatusPage() {

        final User currentUser = onboardingRepository.lookupUserById(UserInterceptor.getCurrentUserId());

        MyStatusPageResponse response = new MyStatusPageResponse();

        final UserMinimalResponse me = Mappers.mapResponseFromDomain(currentUser, avatarUrlResolver::getUserAvatarUrl);

        final Sentiment sentiment = onboardingRepository.findSentimentByUserId(currentUser.getUserId());
        final String sentimentText = onboardingRepository.findSentimentTextByUserId(currentUser.getUserId());
        final List<SuggestionResponse> suggestions = new ArrayList<>();

        currentUser.getRoles().stream()
            .flatMap(role -> suggestionsService.forMe(role).stream())
            .map(text -> {
                final SuggestionResponse sugg = new SuggestionResponse();
                sugg.setText(text);
                return sugg;
            }).forEach(suggestions::add);

        response.setUser(me);
        response.setSentiment(sentiment);
        response.setSuggestions(suggestions);
        response.setSentimentText(sentimentText);

        return response;
    }

}
