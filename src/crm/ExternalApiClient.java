package crm;

import model.CRM;
import model.SyncTask;
import service.RateLimiter;

import java.util.Random;

public class ExternalApiClient {
    private final RateLimiter limiter;
    private final CRM crm;

    public ExternalApiClient(CRM crm, int ratePerSecond) {
        this.crm = crm;
        this.limiter = new RateLimiter(ratePerSecond);
    }

    public boolean sync(SyncTask task) {
        if (!limiter.tryAcquire()) {
            System.out.println("Not able to acquire lock for: " + crm.name());
            return false;
        }

        // This can be replaced to exponential backoff retry in production.
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                if (new Random().nextInt() < 2) throw new RuntimeException("Simulated failure");
                System.out.println("Synced: " + task);
                return true;
            } catch (Exception ex) {
                try {
                    System.out.println("Error while trying to sync for task: " + task.toString() + "," +  ex.getMessage());
                    Thread.sleep((1 << attempt) * 100L + new Random().nextInt(100));
                } catch (InterruptedException ignored) {}
            }
        }

        System.err.println("Failed permanently: " + task);
        return false;
    }
}
