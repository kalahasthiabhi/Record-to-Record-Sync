package service;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RateLimiterTest {

    @Test
    public void testAllowsInitialTokens() {
        RateLimiter limiter = new RateLimiter(2); // 2 tokens/sec

        assertTrue(limiter.tryAcquire());
        assertTrue(limiter.tryAcquire());
        assertFalse(limiter.tryAcquire()); // exceeded initial tokens
    }

    @Test
    public void testRefillsTokensOverTime() throws InterruptedException {
        RateLimiter limiter = new RateLimiter(2); // 2 tokens/sec

        // Deplete initial tokens
        limiter.tryAcquire();
        limiter.tryAcquire();
        assertFalse(limiter.tryAcquire());

        // Wait long enough for 2 tokens to be added back
        Thread.sleep(1100);

        assertTrue(limiter.tryAcquire());
        assertTrue(limiter.tryAcquire());
        assertFalse(limiter.tryAcquire());
    }
}
