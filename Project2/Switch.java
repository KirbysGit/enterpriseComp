package Project2;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

/**
 * Represents a switch in the train yard that can be locked by trains.
 * Uses ReentrantLock for thread-safe access control.
 */
public class Switch {
    // Unique identifier for this switch
    private final int switchId;
    // ReentrantLock ensures only one train can control the switch at a time
    private final ReentrantLock lock;
    // Condition variable for signaling waiting trains
    private final Condition condition;

    /**
     * Creates a new switch with fair locking enabled
     */
    public Switch(int switchId) {
        this.switchId = switchId;
        // Fair locking ensures trains acquire the switch in FIFO order
        this.lock = new ReentrantLock(true);
        this.condition = lock.newCondition();
    }

    /**
     * Attempts to acquire the switch lock with a timeout
     * @param timeout Time in milliseconds to wait for lock acquisition
     * @return true if lock was acquired, false otherwise
     */
    public boolean acquire(long timeout) {
        try {
            return lock.tryLock(timeout, java.util.concurrent.TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Releases the switch lock and signals waiting trains
     * Double locking pattern ensures all waiting trains are notified
     */
    public void release() {
        try {
            condition.signalAll();  // Signal while still holding the lock
        } finally {
            lock.unlock();  // Always release the lock
        }
    }

    // Getter methods
    public int getSwitchId() {
        return switchId;
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public Condition getCondition() {
        return condition;
    }
} 