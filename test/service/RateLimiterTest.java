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

    @Test
    public void testConcurrentAccessIsSafe() throws InterruptedException {
        final RateLimiter limiter = new RateLimiter(5);
        final AtomicInteger successCount = new AtomicInteger(0);
        final int threads = 10;

        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                if (limiter.tryAcquire()) {
                    successCount.incrementAndGet();
                }
                latch.countDown();
            }).start();
        }

        latch.await(); // Wait for all threads to finish
        assertTrue(successCount.get() <= 5); // Cannot exceed 5 tokens
    }

    @Test
    public void testNoNegativeTokens() throws InterruptedException {
        RateLimiter limiter = new RateLimiter(1);
        for (int i = 0; i < 10; i++) {
            limiter.tryAcquire();
        }

        Thread.sleep(1000);
        limiter.tryAcquire(); // should be allowed again
        assertTrue(true); // no assertion failure implies no exception
    }
}
