package com.example.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class IpRateLimitFilter extends OncePerRequestFilter {
	private final int maxConcurrentPerIp;
	private final int maxRequestsPerWindow;
	private final long windowMs;

	private final ConcurrentHashMap<String, AtomicInteger> inFlight = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

	public IpRateLimitFilter(
			@Value("${app.rate-limit.max-concurrent-per-ip:10}") int maxConcurrentPerIp,
			@Value("${app.rate-limit.max-requests-per-window:20}") int maxRequestsPerWindow,
			@Value("${app.rate-limit.window-seconds:10}") int windowSeconds
	) {
		this.maxConcurrentPerIp = maxConcurrentPerIp;
		this.maxRequestsPerWindow = maxRequestsPerWindow;
		this.windowMs = windowSeconds * 1000L;
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return HttpMethod.OPTIONS.matches(request.getMethod());
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
	) throws ServletException, IOException {
		String ip = clientIp(request);
		AtomicInteger concurrent = inFlight.computeIfAbsent(ip, key -> new AtomicInteger());

		if (concurrent.incrementAndGet() > maxConcurrentPerIp) {
			concurrent.decrementAndGet();
			reject(response, "Too many concurrent requests from this IP");
			return;
		}

		boolean allowed;
		try {
			allowed = allowInWindow(ip);
		} catch (RuntimeException e) {
			concurrent.decrementAndGet();
			throw e;
		}

		if (!allowed) {
			concurrent.decrementAndGet();
			reject(response, "Too many requests from this IP");
			return;
		}

		try {
			filterChain.doFilter(request, response);
		} finally {
			int remaining = concurrent.decrementAndGet();
			if (remaining <= 0) {
				inFlight.remove(ip, concurrent);
			}
		}
	}

	private boolean allowInWindow(String ip) {
		long now = System.currentTimeMillis();
		Window window = windows.computeIfAbsent(ip, key -> new Window(now));
		synchronized (window) {
			if (now - window.startMs >= windowMs) {
				window.startMs = now;
				window.count = 0;
			}
			window.count++;
			return window.count <= maxRequestsPerWindow;
		}
	}

	private static void reject(HttpServletResponse response, String message) throws IOException {
		response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
		response.setContentType("application/json");
		response.getWriter().write("{\"message\":\"" + message + "\"}");
	}

	static String clientIp(HttpServletRequest request) {
		String forwarded = request.getHeader("X-Forwarded-For");
		if (forwarded != null && !forwarded.isBlank()) {
			int comma = forwarded.indexOf(',');
			String first = comma >= 0 ? forwarded.substring(0, comma) : forwarded;
			return first.trim();
		}
		String realIp = request.getHeader("X-Real-IP");
		if (realIp != null && !realIp.isBlank()) {
			return realIp.trim();
		}
		return request.getRemoteAddr();
	}

	private static final class Window {
		private long startMs;
		private int count;

		private Window(long startMs) {
			this.startMs = startMs;
			this.count = 0;
		}
	}
}
