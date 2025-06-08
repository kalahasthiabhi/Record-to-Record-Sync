package service;

import config.RateLimiterConfig;
import crm.ExternalApiClient;
import model.CRM;
import model.SyncTask;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class QueueDispatcherTest {

    @Before
    public void setup() {
        RateLimiterConfig.overrideRate(CRM.FINACLE, 10);  // Custom test rate
    }

    @Test
    public void testEventsAreDispatchedSuccessfully() throws InterruptedException {
        QueueDispatcher dispatcher = new QueueDispatcher() {
            @Override
            protected ExternalApiClient createClient(CRM provider) {
                return new ExternalApiClient(provider, 100) {
                    private int count = 0;

                    @Override
                    public boolean sync(SyncTask task) {
                        return true; // return success always
                    }
                };
            }
        };

        TimeUnit.SECONDS.sleep(2);

        InMemoryQueue dlqQueue = dispatcher.getDlqFor(CRM.FINACLE);
        assertEquals(dlqQueue.size(), 0);
    }

    @Test
    public void testFailedEventsAreRequeued() throws InterruptedException {
        QueueDispatcher dispatcher = new QueueDispatcher() {
            @Override
            protected ExternalApiClient createClient(CRM provider) {
                return new ExternalApiClient(provider, 100) {
                    private int count = 0;

                    @Override
                    public boolean sync(SyncTask task) {
                        return (++count % 2 == 0);  // Fail every other call
                    }
                };
            }
        };

        dispatcher.start();
        TimeUnit.SECONDS.sleep(2);

        InMemoryQueue dlq = dispatcher.getDlqFor(CRM.FINACLE);
        assertNotEquals(dlq.size(), 0);
    }
}
