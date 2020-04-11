package de.wirvsvirus.hack.model;

import com.google.common.base.Preconditions;

import java.util.List;
import java.util.UUID;

public class User {

    final private UUID id;

    private String name;

    private List<Role> roles;

    public User(UUID userId) {
        Preconditions.checkNotNull(userId);
        this.id = userId;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Deprecated
    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(final List<Role> roles) {
        this.roles = roles;
    }
}
