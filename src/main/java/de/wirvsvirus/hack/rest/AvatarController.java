package de.wirvsvirus.hack.rest;

import com.amazonaws.util.IOUtils;
import com.google.common.hash.Hashing;
import de.wirvsvirus.hack.model.StockAvatar;
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
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(AvatarController.CONTROLLER_PATH)
@Slf4j
public class AvatarController {
    static final String CONTROLLER_PATH = "/avatar";
    static final String FALLBACK_AVATAR_ENDPOINT = "/fallback";
    static final String STOCK_AVATAR_ENDPOINT = "/stock";

    @Autowired
    private AvatarService avatarService;

    @GetMapping(value = "/available")
    public AvailableAvatarsResponse getAvailableAvatars() {
        return AvailableAvatarsResponse.builder()
                .stockAvatars(Arrays.asList(StockAvatar.values()))
                .build();
    }

    @GetMapping(value = FALLBACK_AVATAR_ENDPOINT)
    public ResponseEntity<Resource> getFallbackAvatar() {
        CacheControl cacheConfiguration = CacheControl
                .maxAge(30, TimeUnit.DAYS)
                .cachePublic();
        return copyResourceToResponse(avatarService.getFallbackAvatarResource(), cacheConfiguration);
    }

    @GetMapping(value = STOCK_AVATAR_ENDPOINT + "/{stockAvatar}")
    public ResponseEntity<Resource> getStockAvatar(@NotNull @PathVariable("stockAvatar") final StockAvatar stockAvatar) {
        CacheControl cacheConfiguration = CacheControl
                .maxAge(1, TimeUnit.DAYS)
                .cachePublic();
        return copyResourceToResponse(avatarService.getStockAvatarResource(stockAvatar), cacheConfiguration);
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
