package de.wirvsvirus.hack.rest;

import de.wirvsvirus.hack.model.StockAvatar;
import de.wirvsvirus.hack.model.User;
import org.springframework.stereotype.Component;

@Component
public class AvatarUrlResolver {
    private static final String FALLBACK_AVATAR_PATH =
            AvatarController.CONTROLLER_PATH + AvatarController.FALLBACK_AVATAR_ENDPOINT;
    private static final String STOCK_AVATAR_PATH =
            AvatarController.CONTROLLER_PATH + AvatarController.STOCK_AVATAR_ENDPOINT;

    public String getUserAvatarUrl(User user) {
        if (user.getStockAvatar() == null) {
            return FALLBACK_AVATAR_PATH;
        }

        return getStockAvatarUrl(user.getStockAvatar());
    }

    public String getStockAvatarUrl(StockAvatar stockAvatar) {
        return String.format("%s/%s", STOCK_AVATAR_PATH, stockAvatar.name());
    }
}
