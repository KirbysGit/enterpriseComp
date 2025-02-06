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
    private final TrainYardSimulator simulator;  // Reference to simulator for status updates
    
    // Constants for timing control
    private static final long LOCK_TIMEOUT = 1000;    // Maximum time to wait for a switch lock
    private static final long RETRY_MIN_DELAY = 1000; // Minimum delay before retrying
    private static final long RETRY_MAX_DELAY = 3000; // Maximum delay before retrying

    public Train(int trainNumber, int inboundTrack, int outboundTrack, List<Switch> requiredSwitches, TrainYardSimulator simulator) {
        this.trainNumber = trainNumber;
        this.inboundTrack = inboundTrack;
        this.outboundTrack = outboundTrack;
        this.requiredSwitches = requiredSwitches;
        this.random = new Random();
        this.simulator = simulator;
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
                    System.out.println("Train " + trainNumber + ": Clear of yard control");
                    System.out.println("Train " + trainNumber + ": Releasing all switch locks");
                    releaseLocks();  // Always release locks, even if movement fails
                    System.out.println("Train " + trainNumber + ": Has been dispatched and moves on down the line out of yard control into CTC");
                    System.out.println("@ @ @ TRAIN " + trainNumber + ": DISPATCHED @ @ @");
                    simulator.markTrainDispatched(trainNumber);
                    return;
                } finally {
                    releaseLocks();  // Always release locks, even if movement fails
                }
            }
            attempts++;
            if (attempts < MAX_ATTEMPTS) {
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
        System.out.println("*************");
        System.out.println("Train " + trainNumber + " is on permanent hold and cannot be dispatched");
        System.out.println("*************");
        simulator.markTrainPermanentHold(trainNumber);
    }

    /**
     * Attempts to acquire all switches needed for the route in order
     * @return true if all switches were acquired, false if any acquisition failed
     */
    private boolean acquireSwitches() {
        // Try to acquire first switch
        Switch firstSwitch = requiredSwitches.get(0);
        if (!acquireSwitch(firstSwitch)) {
            System.out.println("Train " + trainNumber + ": UNABLE TO LOCK first required switch: Switch " + 
                             firstSwitch.getSwitchId() + ". Train will wait...");
            return false;
        }
        System.out.println("Train " + trainNumber + ": HOLDS LOCK on Switch " + firstSwitch.getSwitchId());

        // Try to acquire second switch
        Switch secondSwitch = requiredSwitches.get(1);
        if (!acquireSwitch(secondSwitch)) {
            System.out.println("Train " + trainNumber + ": UNABLE TO LOCK second required switch: Switch " + 
                             secondSwitch.getSwitchId());
            System.out.println("Train " + trainNumber + ": Releasing lock on first required switch: Switch " + 
                             firstSwitch.getSwitchId() + ". Train will wait...");
            firstSwitch.release();
            return false;
        }
        System.out.println("Train " + trainNumber + ": HOLDS LOCK on Switch " + secondSwitch.getSwitchId());

        // Try to acquire third switch
        Switch thirdSwitch = requiredSwitches.get(2);
        if (!acquireSwitch(thirdSwitch)) {
            System.out.println("Train " + trainNumber + ": UNABLE TO LOCK third required switch: Switch " + 
                             thirdSwitch.getSwitchId());
            System.out.println("Train " + trainNumber + ": Releasing locks on first and second required switches: Switch " + 
                             firstSwitch.getSwitchId() + " and Switch " + secondSwitch.getSwitchId() + ". Train will wait...");
            secondSwitch.release();
            firstSwitch.release();
            return false;
        }
        System.out.println("Train " + trainNumber + ": HOLDS LOCK on Switch " + thirdSwitch.getSwitchId());
        System.out.println("Train " + trainNumber + ": HOLDS ALL NEEDED SWITCH LOCKS - Train movement begins");
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
            Switch switchLock = requiredSwitches.get(i);
            if (switchLock.getLock().isHeldByCurrentThread()) {
                System.out.println("Train " + trainNumber + ": Unlocks/releases lock on Switch " + switchLock.getSwitchId());
                switchLock.release();
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

    public int getRequiredSwitchCount() {
        return requiredSwitches.size();
    }
} 