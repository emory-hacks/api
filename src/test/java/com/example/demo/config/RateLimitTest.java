package com.example.demo.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RateLimitTest {

    @AfterEach
    void clearSecurity() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void blocksTheTwentyFirstRequestUntilTheWindowSlides() {
        AtomicLong now = new AtomicLong(1_000_000L);
        SlidingWindowRateLimiter limiter = new SlidingWindowRateLimiter(20, 10_000L, now::get);

        for (int i = 0; i < 20; i++) {
            assertEquals(0, limiter.tryAcquire("user:a@emory.edu"));
        }
        assertTrue(limiter.tryAcquire("user:a@emory.edu") > 0);
        assertEquals(0, limiter.tryAcquire("user:b@emory.edu"));

        now.addAndGet(10_000L);
        assertEquals(0, limiter.tryAcquire("user:a@emory.edu"));
    }

    @Test
    void returns429ForTheCallerWhoExceededTheLimit() throws Exception {
        RateLimitInterceptor interceptor = new RateLimitInterceptor(new SlidingWindowRateLimiter(2, 10_000L));
        MockHttpServletRequest request = requestFrom("10.0.0.8");

        assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
        assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));

        MockHttpServletResponse blocked = new MockHttpServletResponse();
        assertFalse(interceptor.preHandle(request, blocked, new Object()));
        assertEquals(429, blocked.getStatus());
        int retryAfter = Integer.parseInt(blocked.getHeader("Retry-After"));
        assertTrue(retryAfter >= 1 && retryAfter <= 10);
        assertTrue(blocked.getContentAsString().contains("Too many requests"));

        MockHttpServletRequest otherCaller = requestFrom("10.0.0.9");
        assertTrue(interceptor.preHandle(otherCaller, new MockHttpServletResponse(), new Object()));
    }

    @Test
    void signedInUsersDoNotShareAnIpBucket() throws Exception {
        RateLimitInterceptor interceptor = new RateLimitInterceptor(new SlidingWindowRateLimiter(1, 10_000L));
        MockHttpServletRequest request = requestFrom("10.1.1.1");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("a@emory.edu", null, AuthorityUtils.NO_AUTHORITIES));
        assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("b@emory.edu", null, AuthorityUtils.NO_AUTHORITIES));
        assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));

        MockHttpServletResponse blocked = new MockHttpServletResponse();
        assertFalse(interceptor.preHandle(request, blocked, new Object()));
        assertEquals(429, blocked.getStatus());
    }

    @Test
    void anonymousCallersAreLimitedPerIp() throws Exception {
        RateLimitInterceptor interceptor = new RateLimitInterceptor(new SlidingWindowRateLimiter(1, 10_000L));
        SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken(
                "key", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")));

        assertTrue(interceptor.preHandle(requestFrom("10.2.0.1"), new MockHttpServletResponse(), new Object()));
        assertTrue(interceptor.preHandle(requestFrom("10.2.0.2"), new MockHttpServletResponse(), new Object()));

        MockHttpServletResponse blocked = new MockHttpServletResponse();
        assertFalse(interceptor.preHandle(requestFrom("10.2.0.1"), blocked, new Object()));
        assertEquals(429, blocked.getStatus());
    }

    private static MockHttpServletRequest requestFrom(String ip) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users/me");
        request.setRemoteAddr(ip);
        return request;
    }
}
