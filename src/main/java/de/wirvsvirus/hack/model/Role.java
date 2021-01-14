package de.wirvsvirus.hack.model;

import de.wirvsvirus.hack.exception.RoleNotFoundException;
import one.util.streamex.MoreCollectors;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Deprecated
public enum Role {
    ARBEITNEHMER("Arbeitnehmer"),
    ELTERNTEIL("Elternteil"),
    PARTNER("Partner"),
    KIND("Kind"),
    ME_TIME("Me-Time");

    private final String identifier;

    private Role(final String identifier) {
        this.identifier = identifier;
    }

    public static Role ofIdentifier(String identifier) throws RoleNotFoundException {
        return
            Arrays.stream(Role.values())
                .collect(MoreCollectors.onlyOne(r -> StringUtils.equalsAnyIgnoreCase(r.identifier, identifier)))
                .orElseThrow(() -> new RoleNotFoundException(identifier));
    }
}
