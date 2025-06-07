# Record-to-Record Synchronization Service

## Problem Statement

Design a bi-directional record synchronization service that can handle CRUD (Create, Read, Update, Delete) operations between two systems:

- **System A (Internal):** We have full access, including back-end services and storage.
- **System B (External):** Accessible only via API. External APIs may have rate limits, and the data models may differ from our internal system.

The system must handle over **300 million synchronization requests daily**, with near real-time latency and **99.9% availability**.

### Additional Requirements
- External APIs cannot support unlimited requests.
- Some data transformations are required to map between internal and external schemas.
- The system must support **multiple CRM providers**.
- Synchronization must occur **record-by-record**.
- Input/output should be validated against **predefined schemas**.
- Data must be transformed into/from the specific object models before being processed.
- Sync actions (CRUD) are determined by **pre-configured rules or triggers**.

## Solution

This service simulates a real-time, rate-limited synchronization system between an internal queue and an external system (e.g., CRM or financial APIs like Finacle). It uses:

- In-memory queues to simulate per-provider task queues
- A token-bucket-based `RateLimiter` to enforce API rate limits
- A scheduled dispatcher to consume tasks and send them to external APIs
- A retry mechanism via re-queuing failed tasks (backpressure handling)

## Key Components

### 1. `RateLimiter`
- Implements a token bucket pattern to control request rates.
- One instance per `ExternalApiClient`.
- Tokens are refilled based on elapsed time since last refill.

### 2. `InMemoryQueue`
- A thread-safe queue (`LinkedBlockingQueue`) used to enqueue `SyncTask`s per provider.

### 3. `QueueDispatcher`
- Pulls records from the in-memory queue on a fixed schedule.
- Sends tasks to the appropriate `ExternalApiClient`.
- Re-queues tasks on failure (e.g., due to rate limit or transient error).
- Uses `ScheduledExecutorService` to dispatch every 200ms.

### 4. `ExternalApiClient`
- Performs the actual sync operation.
- Applies retry and rate limiting internally.
- Can simulate success/failure and dead-letter queue behavior.

## Key Considerations

### 1. Why token bucket rate limiter?
- Because it is easier to implement and maintain.
- We can use a redis cache when we work in the actual production environment when we work with multiple instances.

### 2. Why Queue dispatcher?
- Periodically polls in-memory queues for tasks similar to how kafka consumer works in production.
- Handles failure through re-queuing.

### 3. Why InMemoryQueue?
- InMemoryQueue is a simple BlockingQueue that holds the sync tasks that needs to be acted upon per external system like finacle.
- We can use a messaging system like kafka in the actual production environment.

