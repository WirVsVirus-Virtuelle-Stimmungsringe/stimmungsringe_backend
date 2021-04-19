package de.wirvsvirus.hack.spring;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Component;

@Component
@WebFilter("/stimmungsring/*")
@Slf4j
public class RestEndpointTimingsStatsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // empty
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        final StopWatch stopWatch = StopWatch.createStarted();
        try {
            chain.doFilter(req, resp);
        } finally {
            log.debug("Time spent in endpoint {}: {} us", ((HttpServletRequest) req).getRequestURI(),
                    stopWatch.getTime(TimeUnit.MICROSECONDS));
        }
    }

    @Override
    public void destroy() {
        // empty
    }
}
