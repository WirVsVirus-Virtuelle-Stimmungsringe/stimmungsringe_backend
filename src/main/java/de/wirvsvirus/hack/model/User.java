package de.wirvsvirus.hack.model;

import com.google.common.base.Preconditions;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@ToString
public class User {

    private final UUID userId;

    private final String deviceIdentifier;

    private String name;

    private List<Role> roles;

    public User(UUID userId, String deviceIdentifier) {
        Preconditions.checkNotNull(userId);
        Preconditions.checkNotNull(deviceIdentifier);
        this.userId = userId;
        this.deviceIdentifier = deviceIdentifier;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getDeviceIdentifier() {
        return deviceIdentifier;
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
