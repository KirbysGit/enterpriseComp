package Project2;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Represents a train that needs to move through the switch yard.
 * Implements Runnable to allow concurrent execution of train movements.
 */
public class Train implements Runnable {
    private final int trainNumber;          // Unique identifier for this train
    private final int inboundTrack;         // Track where train enters the yard
    private final int outboundTrack;        // Track where train exits the yard
    private final List<Switch> requiredSwitches;  // Switches needed for the route
    private final Random random;            // For generating random delay times
    
    // Constants for timing control
    private static final long LOCK_TIMEOUT = 1000;    // Maximum time to wait for a switch lock
    private static final long RETRY_MIN_DELAY = 1000; // Minimum delay before retrying
    private static final long RETRY_MAX_DELAY = 3000; // Maximum delay before retrying

    public Train(int trainNumber, int inboundTrack, int outboundTrack, List<Switch> requiredSwitches) {
        this.trainNumber = trainNumber;
        this.inboundTrack = inboundTrack;
        this.outboundTrack = outboundTrack;
        this.requiredSwitches = requiredSwitches;
        this.random = new Random();
    }

    @Override
    public void run() {
        int attempts = 0;
        final int MAX_ATTEMPTS = 3;  // Maximum number of attempts before permanent hold

        // Try to acquire and move through switches
        while (attempts < MAX_ATTEMPTS) {
            if (acquireSwitches()) {
                try {
                    moveTrain();  // Simulate train movement through switches
                    System.out.println("Train #" + trainNumber + ": Has been dispatched");
                    return;
                } finally {
                    releaseLocks();  // Always release locks, even if movement fails
                }
            }
            attempts++;
            if (attempts < MAX_ATTEMPTS) {
                // Wait random time before retrying
                long delay = RETRY_MIN_DELAY + random.nextInt((int)(RETRY_MAX_DELAY - RETRY_MIN_DELAY));
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
        // Train couldn't complete its route after max attempts
        System.out.println("Train #" + trainNumber + " is on permanent hold and cannot be dispatched");
    }

    /**
     * Attempts to acquire all switches needed for the route in order
     * @return true if all switches were acquired, false if any acquisition failed
     */
    private boolean acquireSwitches() {
        for (Switch switchLock : requiredSwitches) {
            if (!acquireSwitch(switchLock)) {
                System.out.println("Train #" + trainNumber + ": UNABLE TO LOCK Switch " + switchLock.getSwitchId());
                releaseLocks();  // Release any switches already acquired
                return false;
            }
            System.out.println("Train #" + trainNumber + ": HOLDS LOCK on Switch " + switchLock.getSwitchId());
        }
        System.out.println("Train #" + trainNumber + ": HOLDS ALL NEEDED SWITCH LOCKS");
        return true;
    }

    /**
     * Attempts to acquire a single switch with timeout
     */
    private boolean acquireSwitch(Switch switchLock) {
        return switchLock.acquire(LOCK_TIMEOUT);
    }

    /**
     * Releases all held switch locks in reverse order to prevent deadlocks
     */
    private void releaseLocks() {
        for (int i = requiredSwitches.size() - 1; i >= 0; i--) {
            if (requiredSwitches.get(i).getLock().isHeldByCurrentThread()) {
                requiredSwitches.get(i).release();
            }
        }
    }

    /**
     * Simulates train movement through the switches
     */
    private void moveTrain() {
        try {
            Thread.sleep(1000);  // Simulate movement time through switches
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Getter methods
    public int getTrainNumber() {
        return trainNumber;
    }

    public int getInboundTrack() {
        return inboundTrack;
    }

    public int getOutboundTrack() {
        return outboundTrack;
    }
} 