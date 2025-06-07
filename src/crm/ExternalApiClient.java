package crm;

import model.SyncTask;
import service.RateLimiter;

import java.util.Random;

public class ExternalApiClient {
    private final RateLimiter limiter;
    private final Random random = new Random();
    private final String crm;

    public ExternalApiClient(String crm, int ratePerSecond) {
        this.crm = crm;
        this.limiter = new RateLimiter(ratePerSecond);
    }

    public boolean sync(SyncTask task) {
        if (!limiter.tryAcquire()) {
            System.out.println("[RateLimited] " + crm);
            return false;
        }

        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                if (random.nextInt(10) < 2) throw new RuntimeException("Simulated failure");

                System.out.println("[Success] Synced: " + task);
                return true;
            } catch (Exception ex) {
                try {
                    Thread.sleep((1 << attempt) * 100L + random.nextInt(100));
                } catch (InterruptedException ignored) {}
            }
        }

        System.err.println("[DLQ] Failed permanently: " + task);
        return false;
    }
}
