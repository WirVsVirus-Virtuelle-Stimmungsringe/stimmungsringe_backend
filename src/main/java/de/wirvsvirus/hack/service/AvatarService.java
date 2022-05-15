package de.wirvsvirus.hack.service;

import com.google.common.base.Preconditions;
import de.wirvsvirus.hack.model.StockAvatar;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AvatarService {

  public ClassPathResource getFallbackAvatarResource() {
    return new ClassPathResource("/images/stockavatars/avatar-fallback.png");
  }

  public ClassPathResource getStockAvatarResource(StockAvatar stockAvatar) {
    // for the time being, we will serve all SVG avatars as PNGs as fallback for older clients
    Preconditions.checkArgument(stockAvatar != null, "stockAvatar must be set!");

    return new ClassPathResource(
        String.format("/images/stockavatars/%s.png", stockAvatar.name().toLowerCase())
    );
  }

  public ClassPathResource getStockAvatarSvgResource(StockAvatar stockAvatar) {
    Preconditions.checkArgument(stockAvatar != null, "stockAvatar must be set!");
    Preconditions.checkArgument(stockAvatar.isSvgImage, "stockAvatar must be an SVG image");

    return new ClassPathResource(
        String.format("/images/stockavatars/%s.svg", stockAvatar.name().toLowerCase())
    );
  }
}
