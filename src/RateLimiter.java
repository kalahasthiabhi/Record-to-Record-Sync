public class RateLimiter {
    private final int maxTokens;
    private double currentTokens;
    private long lastRefillTimestamp;
    private final long refillIntervalMillis = 1000;

    public RateLimiter(int ratePerSecond) {
        this.maxTokens = ratePerSecond;
        this.currentTokens = ratePerSecond;
        this.lastRefillTimestamp = System.currentTimeMillis();
    }


    /**
     * Currently, each ExternalApiClient has its own RateLimiter instance.This is safe as long as the client is used by a single thread.
     * However, if multiple threads share the same ExternalApiClient, then this method must remain synchronized to ensure thread safety
     */
    public boolean tryAcquire() {
        refill();
        if (currentTokens >= 1) {
            currentTokens -= 1;
            return true;
        }
        return false;
    }

    private void refill() {
        long now = System.currentTimeMillis();
        double tokensToAdd = (now - lastRefillTimestamp) / (double) refillIntervalMillis * maxTokens;
        currentTokens = Math.min(maxTokens, currentTokens + tokensToAdd);
        lastRefillTimestamp = now;
    }
}
