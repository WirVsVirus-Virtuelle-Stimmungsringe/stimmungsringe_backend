package de.wirvsvirus.hack.rest;

import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import de.wirvsvirus.hack.rest.dto.DashboardResponse;
import de.wirvsvirus.hack.rest.dto.MyTileResponse;
import de.wirvsvirus.hack.rest.dto.OtherTileResponse;
import de.wirvsvirus.hack.rest.dto.UserMinimalResponse;
import de.wirvsvirus.hack.spring.UserInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
@Slf4j
public class DashboardController {

    @Autowired
    private OnboardingRepository onboardingRepository;

    @GetMapping
    public DashboardResponse dashboardView() {
        final User currentUser = onboardingRepository.findByUserId(UserInterceptor.getCurrentUserId());

        DashboardResponse response = new DashboardResponse();

        {
            final UserMinimalResponse me = Mappers.mapResponseFromDomain(currentUser);

            final Sentiment sentiment = onboardingRepository.findSentimentByUserId(currentUser.getUserId());

            MyTileResponse myTileResponse = new MyTileResponse();
            myTileResponse.setUser(me);
            myTileResponse.setSentiment(sentiment);

            response.setMyTile(myTileResponse);
        }

        final List<User> otherUsersInGroup = onboardingRepository.findOtherUsersInGroup(currentUser.getUserId());

        final List<OtherTileResponse> otherTiles = new ArrayList<>();
        for (final User otherUser : otherUsersInGroup) {
            final UserMinimalResponse other = Mappers.mapResponseFromDomain(otherUser);

            final Sentiment sentiment = onboardingRepository.findSentimentByUserId(otherUser.getUserId());

            OtherTileResponse tileResponse = new OtherTileResponse();
            tileResponse.setUser(other);
            tileResponse.setSentiment(sentiment);

            otherTiles.add(tileResponse);
        }

        response.setOtherTiles(otherTiles);

        return response;
    }


}
