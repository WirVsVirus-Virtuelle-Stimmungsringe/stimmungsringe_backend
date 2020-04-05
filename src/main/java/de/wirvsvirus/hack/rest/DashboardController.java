package de.wirvsvirus.hack.rest;

import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.model.UserRepository;
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
    private UserRepository userRepository;

    @GetMapping
    public DashboardResponse dashboardView() {
        final User currentUser = userRepository.findByUserId(UserInterceptor.getCurrentUserId());

        DashboardResponse response = new DashboardResponse();

        {
            final UserMinimalResponse me = Mappers.mapResponseFromDomain(currentUser);

            final Sentiment sentiment = userRepository.findSentimentByUserId(currentUser.getId());

            MyTileResponse myTileResponse = new MyTileResponse();
            myTileResponse.setUser(me);
            myTileResponse.setSentiment(sentiment);

            response.setMyTile(myTileResponse);
        }

        final List<User> otherUsersInGroup = userRepository.findOtherUsersInGroup(currentUser.getId());

        final List<OtherTileResponse> otherTiles = new ArrayList<>();
        for (final User otherUser : otherUsersInGroup) {
            final UserMinimalResponse other = Mappers.mapResponseFromDomain(otherUser);

            final Sentiment sentiment = userRepository.findSentimentByUserId(otherUser.getId());

            OtherTileResponse tileResponse = new OtherTileResponse();
            tileResponse.setUser(other);
            tileResponse.setSentiment(sentiment);

            otherTiles.add(tileResponse);
        }

        response.setOtherTiles(otherTiles);

        return response;
    }


}
