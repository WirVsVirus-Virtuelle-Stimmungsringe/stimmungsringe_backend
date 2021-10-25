package de.wirvsvirus.hack.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import de.wirvsvirus.hack.model.StockAvatar;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import({AvatarService.class})
class AvatarServiceTest {

  @Autowired
  private AvatarService avatarService;

  @Test
  void checkIfAllStockAvatarsExistsOnClasspath() {
    Assertions.assertAll(
        Arrays.stream(StockAvatar.values())
            .map(avatar -> avatarService.getStockAvatarResource(avatar))
            .map(resource -> () ->
                assertTrue(resource.exists(),
                    "Avatar image must be available in " + resource.getPath()))
    );
  }

}