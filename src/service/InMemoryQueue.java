package service;

import model.SyncTask;

import java.util.concurrent.*;

public class InMemoryQueue {
    private final BlockingQueue<SyncTask> queue = new LinkedBlockingQueue<>();

    public void offer(SyncTask task) {
        queue.offer(task);
    }

    public SyncTask poll() {
        return queue.poll();
    }

    public int size() {
        return queue.size();
    }

}
