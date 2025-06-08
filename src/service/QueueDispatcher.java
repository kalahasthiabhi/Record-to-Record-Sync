package service;

import config.RateLimiterConfig;
import model.Action;
import crm.ExternalApiClient;
import model.CRM;
import model.ExternalRecord;
import model.SyncTask;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class QueueDispatcher {
    private final Map<CRM, InMemoryQueue> crmQueueMap = new HashMap<>();
    private final Map<CRM, InMemoryQueue> crmDlqMap = new HashMap<>();
    private final Map<CRM, ExternalApiClient> externalAPIClientMap = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public QueueDispatcher() {
        crmQueueMap.put(CRM.FINACLE, new InMemoryQueue());
        crmDlqMap.put(CRM.FINACLE, new InMemoryQueue());
        externalAPIClientMap.put(CRM.FINACLE, createClient(CRM.FINACLE));

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
                            crmDlqMap.get(provider).offer(task); // Move to DLQ
                            System.err.println("SyncTask moved to DLQ after retries: " + task);
                        }
                    }
                });
            } catch (Exception e) {
                System.err.println("Exception in scheduled dispatcher: " + e.getMessage());
            }
        }, 0, 200, TimeUnit.MILLISECONDS);
    }

    private void produceMockEvents() {
        for (int i = 1; i <= 20; i++) {
            ExternalRecord payload = new ExternalRecord(i + "", "Abhishekh", "kalahasthiabhi30@gmail.com");
            Action action = i % 2 == 0 ? Action.CREATE : Action.UPDATE;
            SyncTask task = new SyncTask(CRM.FINACLE, action, payload);
            crmQueueMap.get(CRM.FINACLE).offer(task);
        }
    }

    public InMemoryQueue getDlqFor(CRM crm) {
        return crmDlqMap.get(crm);
    }

    protected ExternalApiClient createClient(CRM crm) {
        return new ExternalApiClient(crm, RateLimiterConfig.getRateFor(crm));
    }

}
