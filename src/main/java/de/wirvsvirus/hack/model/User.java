package de.wirvsvirus.hack.model;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.util.List;
import java.util.UUID;
import lombok.ToString;

@ToString
public class User implements MicrostreamObject {

    private final UUID userId;
    private final String deviceIdentifier;
    private String name;
    private List<Role> roles;
    private StockAvatar stockAvatar;

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

    /**
     * nullable
     */
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean hasName() {
        return !Strings.isNullOrEmpty(name);
    }

    @Deprecated
    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(final List<Role> roles) {
        this.roles = roles;
    }

    /**
     * nullable
     */
    public StockAvatar getStockAvatar() {
        return stockAvatar;
    }

    public void setStockAvatar(StockAvatar stockAvatar) {
        this.stockAvatar = stockAvatar;
    }

}
