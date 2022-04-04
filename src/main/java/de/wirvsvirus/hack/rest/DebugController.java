package de.wirvsvirus.hack.rest;

import com.google.common.hash.Hashing;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.rest.dto.SunshineHoursResponse;
import de.wirvsvirus.hack.service.AchievementService;
import de.wirvsvirus.hack.service.LoggingService;
import de.wirvsvirus.hack.service.StatsService;
import de.wirvsvirus.hack.spring.Database;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("/debug")
@Slf4j
public class DebugController {

    @Autowired
    private LoggingService loggingService;

    @Autowired
    private StatsService statsService;

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private Database database;


    @DeleteMapping("/achievement/level/{userId}")
    public void resetSplashSeen(
        @RequestHeader("X-FAM-Debug") String debugCode,
        @NotNull @PathVariable("userId") final UUID userId
    ) {
        checkDebugCode(debugCode);
        database.dataRoot().getAchievementShownStatusByUserAndType().remove(userId);
        database.persist(database.dataRoot().getAchievementShownStatusByUserAndType());
    }

    @GetMapping("/users")
    public Collection<User> getAllUsers(
            @RequestHeader("X-FAM-Debug") String debugCode
    ) {
        checkDebugCode(debugCode);
        return database.dataRoot().getAllUsers().values();
    }

    @GetMapping("/groups")
    public Collection<Group> getAllGroups(@RequestHeader("X-FAM-Debug") String debugCode) {
        checkDebugCode(debugCode);
        return database.dataRoot().getAllGroups().values();
    }

    @GetMapping("/groups/sunshine-hours")
    public List<SunshineHoursResponse> getSunshineHoursForAllGroups(@RequestHeader("X-FAM-Debug") String debugCode) {
        checkDebugCode(debugCode);
        final Instant now = Instant.now();
        return
            database.dataRoot().getAllGroups().values().stream()
                .map(group -> {
                    final Duration sunshine =
                        statsService.calcSunshineTimeForGroup(group.getGroupId(), now);
                    return SunshineHoursResponse.builder()
                        .groupId(group.getGroupId())
                        .groupName(group.getGroupName())
                        .sunshineHours(sunshine.toString())
                        .build();
                })
                .collect(Collectors.toList());
    }

    @GetMapping (value = "/logfiles", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<StreamingResponseBody> download(
            final HttpServletResponse response,
            @RequestParam(value = "startDate", required = false) final String startDateRaw,
            @RequestHeader("X-FAM-Debug") String debugCode) {
        checkDebugCode(debugCode);

        final Predicate<String> removeLogsByDate =
                StringUtils.isNotBlank(startDateRaw)
                ? line -> removeLogsByDate(line, LocalDate.parse(startDateRaw))
                : line -> false;

        response.setContentType("text/plain");
        response.setHeader(
                "Content-Disposition",
                "attachment;filename=stimmungsringe.log");

        StreamingResponseBody stream = out -> {
            final PrintWriter writer = new PrintWriter(out);

            StreamEx.of(loggingService.readLogLines())
                    .dropWhile(removeLogsByDate)
                    .forEach(line -> writer.println(line));

            writer.flush();
        };
        return new ResponseEntity<>(stream, HttpStatus.OK);
    }

    /**
     * 2020-05-04 10:44:49.512 user:- trace:- path:-  INFO
     */
    private boolean removeLogsByDate(final String line, final LocalDate startDate) {
        if (!line.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}.*")) {
            return false;
        }
        final LocalDate logDate = LocalDate.parse(line.substring(0, 10));
        return logDate.isBefore(startDate);
    }

    /**
     * @param debugCode Secret debug hash - ask Stefan .. starts with "aeLeiv7...."
     */
    private void checkDebugCode(final String debugCode) {
        final String hashed = Hashing.sha256().hashString(debugCode, StandardCharsets.ISO_8859_1).toString();
        final String expected = "ff43ee88b4ef1c750519c6d681dc9992d990f6e852021b48d8a5faf182af1f27";
        if (!expected.equals(hashed)) {
            throw new IllegalArgumentException("Secret debug hash is wrong - ask Stefan");
        }
    }

}
