package de.wirvsvirus.hack.spring;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.concurrent.TimeUnit;

@Component
@WebFilter("/stimmungsring/*")
public class RestEndpointTimingsStatsFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestEndpointTimingsStatsFilter.class);

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
            LOGGER.debug("Time spent in endpoint {}: {} us", ((HttpServletRequest) req).getRequestURI(),
                    stopWatch.getTime(TimeUnit.MICROSECONDS));
        }
    }

    @Override
    public void destroy() {
        // empty
    }
}
