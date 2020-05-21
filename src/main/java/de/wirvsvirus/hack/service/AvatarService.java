package de.wirvsvirus.hack.service;

import com.google.common.base.Preconditions;
import de.wirvsvirus.hack.model.StockAvatar;
import de.wirvsvirus.hack.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AvatarService {

    public ClassPathResource getUserAvatarUrl(User user) {
        if (user.getStockAvatar() == null) {
            return new ClassPathResource("/images/stockavatars/avatar-fallback.jpg");
        }

        return getStockAvatarUrl(user.getStockAvatar());
    }

    public ClassPathResource getStockAvatarUrl(StockAvatar stockAvatar) {
        Preconditions.checkArgument(stockAvatar != null, "stockAvatar must be set!");

        return new ClassPathResource(
                String.format("/images/stockavatars/%s.png", stockAvatar.name().toLowerCase())
        );
    }
}
