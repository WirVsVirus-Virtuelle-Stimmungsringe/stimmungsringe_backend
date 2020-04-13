package de.wirvsvirus.hack.rest;

import de.wirvsvirus.hack.mock.MockFactory;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/debug")
@Slf4j
public class DebugController {

    @GetMapping("/users")
    public Collection<User> getAllUsers() {
        return MockFactory.allUsers.values();
    }

    @GetMapping("/groups")
    public Collection<Group> getAllGroups() {
        return MockFactory.allGroups.values();
    }

}
