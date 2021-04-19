package de.wirvsvirus.hack.spring;

import com.google.common.base.Preconditions;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Slf4j
public class UserInterceptor implements HandlerInterceptor {

  private static final String HEADER_USER_ID = "X-User-ID";

  private static final ThreadLocal<UUID> USER_ID_PER_THREAD = new ThreadLocal<>();

  @Override
  public boolean preHandle(HttpServletRequest request,
      HttpServletResponse response, Object object) {
    final String rawUserIdFromHeader = request.getHeader(HEADER_USER_ID);

    if (request.getServletPath().startsWith("/error")) {
      return true;
    }


    if (request.getServletPath().endsWith("/signin") || request.getServletPath().startsWith("/debug/")
    ) {
      log.debug("Request for unauthenticated endpoint {}", request.getServletPath());
      Preconditions.checkState(rawUserIdFromHeader == null,
          "Unexpected user id header for request %s", request.getRequestURL());
      return true;
    }

    Preconditions.checkNotNull(rawUserIdFromHeader, "Header for user id missing in request %s", request.getRequestURL());

    USER_ID_PER_THREAD.set(UUID.fromString(rawUserIdFromHeader));

    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
      ModelAndView modelAndView) {
    USER_ID_PER_THREAD.remove();
  }

  public static UUID getCurrentUserId() {
    final UUID userId = USER_ID_PER_THREAD.get();
    Preconditions.checkNotNull(userId, "UserId for this thread/request not set!");
    return userId;
  }

}
