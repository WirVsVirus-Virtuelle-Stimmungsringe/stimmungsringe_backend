package de.wirvsvirus.hack.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import de.wirvsvirus.hack.model.Group;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/dashboard")
@Slf4j
public class DashboardController {

    @Autowired
    private OnboardingRepository onboardingRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<DashboardResponse> dashboardView() {
        final User currentUser = onboardingRepository.lookupUserById(UserInterceptor.getCurrentUserId());

        final Optional<Group> group = onboardingRepository.findGroupByUser(currentUser.getUserId());

        DashboardResponse response = new DashboardResponse();

        {
            final UserMinimalResponse me = Mappers.mapResponseFromDomain(currentUser);

            final Sentiment sentiment = onboardingRepository.findSentimentByUserId(currentUser.getUserId());

            MyTileResponse myTileResponse = new MyTileResponse();
            myTileResponse.setUser(me);
            myTileResponse.setSentiment(sentiment);

            response.setMyTile(myTileResponse);
        }


        final List<User> otherUsersInGroup;
        if (group.isPresent()) {
            otherUsersInGroup = onboardingRepository.findOtherUsersInGroup(group.get().getGroupId(), currentUser.getUserId());
        } else {
            otherUsersInGroup = Collections.emptyList();
        }

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

        final String hash = buildHash(response);

        return ResponseEntity.status(HttpStatus.OK).header("X-Dashboard-Hash", hash).body(response);
    }

    private String buildHash(final DashboardResponse response) {
        try {
            return Hashing.sha256().hashString(
                objectMapper.writeValueAsString(response), StandardCharsets.UTF_8).toString();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException();
        }
    }


}
