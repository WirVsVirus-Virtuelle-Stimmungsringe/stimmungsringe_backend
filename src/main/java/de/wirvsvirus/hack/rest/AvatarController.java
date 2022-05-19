package de.wirvsvirus.hack.rest;

import com.google.common.hash.Hashing;
import de.wirvsvirus.hack.model.StockAvatar;
import de.wirvsvirus.hack.rest.AvatarUrlResolver.AvatarUrls;
import de.wirvsvirus.hack.rest.dto.AvailableAvatarsResponse;
import de.wirvsvirus.hack.service.AvatarService;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
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

@RestController
@RequestMapping(AvatarController.CONTROLLER_PATH)
@Slf4j
public class AvatarController {

  static final String CONTROLLER_PATH = "/avatar";
  static final String FALLBACK_AVATAR_ENDPOINT = "/fallback";
  static final String STOCK_AVATAR_ENDPOINT = "/stock";
  static final String STOCK_AVATAR_SVG_ENDPOINT = "/stock/svg";

  @Autowired
  private AvatarService avatarService;

  @GetMapping(value = "/available")
  public AvailableAvatarsResponse getAvailableAvatars() {
    final List<AvailableAvatarsResponse.StockAvatarResponse> stockAvatarResponses =
        Arrays.stream(StockAvatar.values())
            .map(stockAvatar -> {
                  final AvatarUrls stockAvatarUrls = AvatarUrlResolver.getStockAvatarUrls(stockAvatar);
                  return AvailableAvatarsResponse.StockAvatarResponse.builder()
                      .avatarName(stockAvatar.name())
                      .avatarUrl(stockAvatarUrls.getAvatarUrl())
                      .avatarSvgUrl(stockAvatarUrls.getAvatarSvgUrl().orElse(null))
                      .build();
                }
            )
            .collect(Collectors.toList());

    return AvailableAvatarsResponse.builder()
        .stockAvatars(stockAvatarResponses)
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
  public ResponseEntity<Resource> getStockAvatar(
      @NotNull @PathVariable("stockAvatar") final StockAvatar stockAvatar) {
    CacheControl cacheConfiguration = CacheControl
        .maxAge(1, TimeUnit.DAYS)
        .cachePublic();
    return copyResourceToResponse(avatarService.getStockAvatarResource(stockAvatar),
        cacheConfiguration);
  }

  @GetMapping(value = STOCK_AVATAR_SVG_ENDPOINT + "/{stockAvatar}")
  public ResponseEntity<Resource> getStockAvatarSvg(
      @NotNull @PathVariable("stockAvatar") final StockAvatar stockAvatar) {
    CacheControl cacheConfiguration = CacheControl
        .maxAge(1, TimeUnit.DAYS)
        .cachePublic();
    return copyResourceToResponse(avatarService.getStockAvatarSvgResource(stockAvatar),
        cacheConfiguration);
  }

  private ResponseEntity<Resource> copyResourceToResponse(final ClassPathResource avatarResource,
      final CacheControl cacheConfiguration) {
    final String filename = avatarResource.getFilename();

    if (filename == null) {
      throw new IllegalStateException(
          String.format("Resource has no file name: %s", avatarResource));
    }

    final MediaType responseContentType;
    if (filename.endsWith(".png")) {
      responseContentType = MediaType.IMAGE_PNG;
    } else if (filename.endsWith(".jpg")) {
      responseContentType = MediaType.IMAGE_JPEG;
    } else if (filename.endsWith(".svg")) {
      responseContentType = new MediaType("image", "svg+xml");
    } else {
      throw new IllegalStateException(
          String.format("Unknown image file extension: %s", filename));
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
