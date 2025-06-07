import config.RateLimiterConfig;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class QueueDispatcher {
    private final Map<String, InMemoryQueue> crmQueueMap = new HashMap<>();
    private final Map<String, ExternalApiClient> externalAPIClientMap = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public QueueDispatcher() {
        crmQueueMap.put("finacle", new InMemoryQueue());
        externalAPIClientMap.put("finacle", new ExternalApiClient("finacle", RateLimiterConfig.getRateFor("finacle")));

    }

    public void start() {
        new Thread(this::produceMockEvents).start();

        // Start scheduled dispatch task
        scheduler.scheduleAtFixedRate(() -> {
            try {
                crmQueueMap.forEach((provider, queue) -> {
                    SyncTask task = queue.poll();
                    if (task != null) {
                        ExternalApiClient client = externalAPIClientMap.get(provider);
                        boolean success = client.sync(task);
                        if (!success) {
                            queue.offer(task); // backpressure: requeue the task
                        }
                    }
                });
            } catch (Exception e) {
                System.err.println("[ERROR] Exception in scheduled dispatcher: " + e.getMessage());
            }
        }, 0, 200, TimeUnit.MILLISECONDS);
    }

    private void produceMockEvents() {
        for (int i = 1; i <= 20; i++) {
            SyncTask task = new SyncTask("finacle", "id-" + i, "update", "John Doe");
            crmQueueMap.get("finacle").offer(task);
        }
    }
}
