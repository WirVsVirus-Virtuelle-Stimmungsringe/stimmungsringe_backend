package de.wirvsvirus.hack.rest;

import com.amazonaws.util.IOUtils;
import com.google.common.hash.Hashing;
import de.wirvsvirus.hack.model.StockAvatar;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import de.wirvsvirus.hack.rest.dto.AvailableAvatarsResponse;
import de.wirvsvirus.hack.service.AvatarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/avatar")
@Slf4j
public class AvatarController {
    @Autowired
    private OnboardingRepository onboardingRepository;

    @Autowired
    private AvatarService avatarService;

    @GetMapping(value = "/{userId}")
    public ResponseEntity<Resource> getAvatarForUser(@NotNull @PathVariable("userId") UUID userId) {
        final User user = onboardingRepository.lookupUserById(userId);
        final ClassPathResource avatarResource = avatarService.getUserAvatarUrl(user);

        return copyResourceToResponse(avatarResource, CacheControl.empty());
    }

    @GetMapping(value = "/available")
    public AvailableAvatarsResponse getAvailableAvatars() {
        return AvailableAvatarsResponse.builder()
                .stockAvatars(Arrays.asList(StockAvatar.values()))
                .build();
    }

    @GetMapping(value = "/stock/{stockAvatar}")
    public ResponseEntity<Resource> getStockAvatar(@NotNull @PathVariable("stockAvatar") final StockAvatar stockAvatar) {
        CacheControl cacheConfiguration = CacheControl
                .maxAge(1, TimeUnit.DAYS)
                .cachePublic();
        return copyResourceToResponse(avatarService.getStockAvatarUrl(stockAvatar), cacheConfiguration);
    }

    private ResponseEntity<Resource> copyResourceToResponse(final ClassPathResource avatarResource, final CacheControl cacheConfiguration) {
        final MediaType responseContentType;
        if (avatarResource.getFilename().endsWith(".png")) {
            responseContentType = MediaType.IMAGE_PNG;
        } else if (avatarResource.getFilename().endsWith(".jpg")) {
            responseContentType = MediaType.IMAGE_JPEG;
        } else {
            throw new IllegalStateException(String.format("Unknown image file extension: %s", avatarResource.getFilename()));
        }

        return ResponseEntity.ok()
                .contentType(responseContentType)
                .eTag(getResourceHash(avatarResource))
                .cacheControl(cacheConfiguration)
                .body(avatarResource);
    }

    private String getResourceHash(ClassPathResource resource) {
        try {
            final byte[] resourceBytes = IOUtils.toByteArray(resource.getInputStream());
            return Hashing.murmur3_128().hashBytes(resourceBytes).toString();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
