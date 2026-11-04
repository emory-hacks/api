package com.example.demo.config;

import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

/**
 * Allows up to {@code maxRequests} hits per key inside a rolling time window.
 */
public final class SlidingWindowRateLimiter {
    private final int maxRequests;
    private final long windowMillis;
    private final LongSupplier clock;
    private final ConcurrentHashMap<String, ArrayDeque<Long>> windows = new ConcurrentHashMap<>();

    public SlidingWindowRateLimiter(int maxRequests, long windowMillis) {
        this(maxRequests, windowMillis, System::currentTimeMillis);
    }

    public SlidingWindowRateLimiter(int maxRequests, long windowMillis, LongSupplier clock) {
        if (maxRequests < 1) {
            throw new IllegalArgumentException("maxRequests must be at least 1");
        }
        if (windowMillis < 1) {
            throw new IllegalArgumentException("windowMillis must be at least 1");
        }
        this.maxRequests = maxRequests;
        this.windowMillis = windowMillis;
        this.clock = clock;
    }

    /**
     * @return {@code 0} when the call is allowed; otherwise seconds to wait, at least 1
     */
    public int tryAcquire(String key) {
        long now = clock.getAsLong();
        ArrayDeque<Long> window = windows.computeIfAbsent(key, ignored -> new ArrayDeque<>());
        synchronized (window) {
            evict(window, now);
            if (window.size() >= maxRequests) {
                long retryAt = window.peekFirst() + windowMillis;
                long waitMillis = Math.max(1L, retryAt - now);
                return (int) Math.min(Integer.MAX_VALUE, (waitMillis + 999) / 1000);
            }
            window.addLast(now);
            return 0;
        }
    }

    private void evict(ArrayDeque<Long> window, long now) {
        while (!window.isEmpty() && now - window.peekFirst() >= windowMillis) {
            window.removeFirst();
        }
    }
}
