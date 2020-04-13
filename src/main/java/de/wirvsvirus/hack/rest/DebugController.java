package de.wirvsvirus.hack.rest;

import com.google.common.base.Preconditions;
import com.google.common.hash.Hashing;
import de.wirvsvirus.hack.mock.MockFactory;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Collection;

@RestController
@RequestMapping("/debug")
@Slf4j
public class DebugController {

    @GetMapping("/users")
    public Collection<User> getAllUsers(
            @RequestHeader("X-FAM-Debug") String debugCode
    ) {
        checkDebugCode(debugCode);
        return MockFactory.allUsers.values();
    }

    @GetMapping("/groups")
    public Collection<Group> getAllGroups(@RequestHeader("X-FAM-Debug") String debugCode) {
        checkDebugCode(debugCode);
        return MockFactory.allGroups.values();
    }

    private void checkDebugCode(final String debugCode) {
        final String hashed = Hashing.sha256().hashString(debugCode, StandardCharsets.ISO_8859_1).toString();
        final String expected = "ff43ee88b4ef1c750519c6d681dc9992d990f6e852021b48d8a5faf182af1f27";
        if (!expected.equals(hashed)) {
            throw new IllegalArgumentException("Secret debug hash is wrong - ask Stefan");
        }
    }

}
