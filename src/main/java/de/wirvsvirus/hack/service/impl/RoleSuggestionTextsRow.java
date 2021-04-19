package de.wirvsvirus.hack.service.impl;

import de.wirvsvirus.hack.model.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleSuggestionTextsRow {

    private Role role;
    private String expectation;
    private String selfRecommendation;
    private String forMe;
    private String thirdPartyRecommendation;
    private String forOthers;

}
