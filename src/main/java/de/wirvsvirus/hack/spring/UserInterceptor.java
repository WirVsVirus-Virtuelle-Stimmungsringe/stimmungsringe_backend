package de.wirvsvirus.hack.spring;

import com.google.common.base.Preconditions;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Slf4j
public class UserInterceptor extends HandlerInterceptorAdapter {

    private static final String HEADER_USER_ID = "X-User-ID";

    private static final ThreadLocal<UUID> USER_ID_PER_THREAD = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object object) {

        if (request.getServletPath().startsWith("/error")) {
            return true;
        }

        if (request.getServletPath().endsWith("/signin") || request.getServletPath().startsWith("/debug/")
        ) {
            log.debug("Request for unauthenticated endpoint {}", request.getServletPath());
            Preconditions.checkState(request.getHeader(HEADER_USER_ID) == null,
                    "Unexpected user id header for request %s", request.getRequestURL());
            return true;
        }

        final String userIdRaw = request.getHeader(HEADER_USER_ID);
        Preconditions.checkNotNull(userIdRaw, "Header for user id missing in request %s", request.getRequestURL());

        USER_ID_PER_THREAD.set(UUID.fromString(userIdRaw));

        log.debug("Request for authenticated endpoint {} with userID {}", request.getServletPath(), userIdRaw);

        return true;
    }

    public static UUID getCurrentUserId() {
        final UUID userId = USER_ID_PER_THREAD.get();
        Preconditions.checkNotNull(userId, "UserId for this thread/request not set!");
        return userId;
    }

}
