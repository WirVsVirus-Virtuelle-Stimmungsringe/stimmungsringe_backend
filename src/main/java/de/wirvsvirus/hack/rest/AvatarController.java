package de.wirvsvirus.hack.rest;

import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import de.wirvsvirus.hack.service.AvatarService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@RestController
@RequestMapping("/avatar")
@Slf4j
public class AvatarController {
    @Autowired
    private OnboardingRepository onboardingRepository;

    @Autowired
    private AvatarService avatarService;

    @GetMapping(value = "/{userId}")
    public void getImageAsByteArray(HttpServletResponse response,
                                    @NotNull @PathVariable("userId") UUID userId) throws IOException {
        final User user = onboardingRepository.lookupUserById(userId);
        final ClassPathResource avatarResource = avatarService.getAvatarUrl(user);

        if (avatarResource.getFilename().endsWith(".png")) {
            response.setContentType(MediaType.IMAGE_PNG_VALUE);
        } else if (avatarResource.getFilename().endsWith(".jpg")) {
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        } else {
            throw new IllegalStateException(String.format("Unknown image file extension: %s", avatarResource.getFilename()));
        }

        final InputStream avatarImage = avatarResource.getInputStream();
        IOUtils.copy(avatarImage, response.getOutputStream());
    }

}
