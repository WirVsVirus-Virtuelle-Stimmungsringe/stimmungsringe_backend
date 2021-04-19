package de.wirvsvirus.hack.service;

import de.wirvsvirus.hack.model.Role;
import java.util.List;

/**
 * @deprecated feature not used ATM
 */
@Deprecated
public interface RoleBasedTextSuggestionsService {

    List<String> forMe(Role role);

    List<String> forOthers(Role role);
}
