package de.wirvsvirus.hack.service;

import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AvatarService {
    @Autowired
    private OnboardingRepository onboardingRepository;

    public ClassPathResource getAvatarUrl(User user) {
        if (user.getStockAvatar() == null) {
            return new ClassPathResource("/images/stockavatars/avatar-fallback.jpg");
        }

        return new ClassPathResource(
                String.format("/images/stockavatars/%s.png", user.getStockAvatar().name().toLowerCase())
        );
    }
}
