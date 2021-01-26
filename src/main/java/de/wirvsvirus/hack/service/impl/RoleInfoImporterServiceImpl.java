package de.wirvsvirus.hack.service.impl;

import de.wirvsvirus.hack.exception.RoleNotFoundException;
import de.wirvsvirus.hack.model.Role;
import de.wirvsvirus.hack.service.RoleBasedTextSuggestionsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Slf4j
public class RoleInfoImporterServiceImpl implements RoleBasedTextSuggestionsService {

    private static final String DATA_FILE = "/data/role_text.csv";

    private static final char DELIMITER = '\t';

    private static final int COLUMN_ROLE = 0;
    private static final int COLUMN_EXPECTATION = 1;
    private static final int COLUMN_SELF_RECOMMENDATION = 2;
    private static final int COLUMN_FOR_ME = 3;
    private static final int COLUMN_THIRD_PARTY_RECOMMENDATION = 4;
    private static final int COLUMN_FOR_OTHERS = 5;

    private Map<Role, List<RoleSuggestionTextsRow>> roleRoleDataMap;

    @PostConstruct
    private void initialize() throws IOException {
        log.info("Resolve Role data from CSV...");
        final List<RoleSuggestionTextsRow> roleData = importData(RoleBasedTextSuggestionsService.class.getResourceAsStream(DATA_FILE));
        this.roleRoleDataMap = roleData.parallelStream().collect(groupingBy(RoleSuggestionTextsRow::getRole));
    }

    @Override
    public List<String> forMe(Role role) {
        return this.roleRoleDataMap.get(role).stream().map(r -> r.getForMe()).collect(Collectors.toList());
    }

    @Override
    public List<String> forOthers(Role role) {
        return this.roleRoleDataMap.get(role).stream().map(r -> r.getForOthers()).collect(Collectors.toList());
    }

    private List<RoleSuggestionTextsRow> importData(InputStream inputStream) throws IOException {
        final List<RoleSuggestionTextsRow> roleTexts = new ArrayList<>();

        final Iterable<CSVRecord> records = CSVFormat.DEFAULT
                .withDelimiter(DELIMITER)
                .withFirstRecordAsHeader()
                .parse(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        for (CSVRecord record : records) {
            try {
                roleTexts.add(RoleSuggestionTextsRow.builder()
                        .role(Role.ofIdentifier(record.get(COLUMN_ROLE)))
                        .expectation(record.get(COLUMN_EXPECTATION))
                        .selfRecommendation(record.get(COLUMN_SELF_RECOMMENDATION))
                        .forMe(record.get(COLUMN_FOR_ME))
                        .thirdPartyRecommendation(record.get(COLUMN_THIRD_PARTY_RECOMMENDATION))
                        .forOthers(record.get(COLUMN_FOR_OTHERS))
                        .build());
            } catch (RoleNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }
        return roleTexts;
    }
}
