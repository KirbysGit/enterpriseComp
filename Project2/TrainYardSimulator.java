package Project2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Main simulator class that manages the train yard simulation.
 * Handles configuration loading and coordinates train movements.
 */
public class TrainYardSimulator {
    private static final int MAX_THREADS = 30;  // Maximum number of concurrent trains
    private final Map<Integer, Switch> switches = new HashMap<>();  // All switches in the yard
    private final List<Train> trains = new ArrayList<>();
    private final Map<String, List<Switch>> routeCache = new HashMap<>();  // Cache of valid routes
    private final Set<Integer> dispatchedTrains = new HashSet<>();
    private final Set<Integer> permanentHoldTrains = new HashSet<>();

    public TrainYardSimulator() {
        // No need to set up logging in this version
    }

    /**
     * Loads the yard configuration from CSV file.
     * Format: inboundTrack,switch1,switch2,switch3,outboundTrack
     */
    public void loadYardConfiguration(String yardFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(yardFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int inboundTrack = Integer.parseInt(parts[0]);
                int firstSwitch = Integer.parseInt(parts[1]);
                int secondSwitch = Integer.parseInt(parts[2]);
                int thirdSwitch = Integer.parseInt(parts[3]);
                int outboundTrack = Integer.parseInt(parts[4]);

                // Create switches if they don't exist (switches are reused across routes)
                switches.putIfAbsent(firstSwitch, new Switch(firstSwitch));
                switches.putIfAbsent(secondSwitch, new Switch(secondSwitch));
                switches.putIfAbsent(thirdSwitch, new Switch(thirdSwitch));

                // Cache the route for this track combination
                String routeKey = inboundTrack + "-" + outboundTrack;
                List<Switch> route = Arrays.asList(
                    switches.get(firstSwitch),
                    switches.get(secondSwitch),
                    switches.get(thirdSwitch)
                );
                routeCache.put(routeKey, route);
            }
        }
    }

    /**
     * Loads the train schedule from CSV file.
     * Format: trainNumber,inboundTrack,outboundTrack
     */
    public void loadTrainSchedule(String fleetFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fleetFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int trainNumber = Integer.parseInt(parts[0]);
                int inboundTrack = Integer.parseInt(parts[1]);
                int outboundTrack = Integer.parseInt(parts[2]);

                String routeKey = inboundTrack + "-" + outboundTrack;
                List<Switch> requiredSwitches = routeCache.get(routeKey);
                
                if (requiredSwitches != null) {
                    trains.add(new Train(trainNumber, inboundTrack, outboundTrack, requiredSwitches, this));
                } else {
                    System.out.println("Warning: No valid route found for Train #" + trainNumber +
                                     " from track " + inboundTrack + " to " + outboundTrack);
                }
            }
        }
    }

    /**
     * Starts the simulation by creating a thread pool and submitting all trains
     */
    public void startSimulation() {
        System.out.println("$ $ $ TRAIN MOVEMENT SIMULATION BEGINS........... $ $ $");
        
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
        
        for (Train train : trains) {
            executor.submit(train);
        }

        executor.shutdown();
        try {
            // Wait for all trains to complete or timeout after 5 minutes
            if (!executor.awaitTermination(5, TimeUnit.MINUTES)) {
                System.out.println("Simulation timed out after 5 minutes");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        System.out.println("$ $ $ SIMULATION ENDS $ $ $");
        printFinalStatus();
    }

    public void markTrainDispatched(int trainNumber) {
        dispatchedTrains.add(trainNumber);
    }

    public void markTrainPermanentHold(int trainNumber) {
        permanentHoldTrains.add(trainNumber);
    }

    private void printFinalStatus() {
        System.out.println("\nFinal Train Status Report:");
        System.out.println("Train Number | Inbound Track | Outbound Track | Status");
        System.out.println("------------------------------------------------");
        
        // Sort trains by number for consistent output
        List<Train> trainList = new ArrayList<>(trains);
        trainList.sort(Comparator.comparingInt(Train::getTrainNumber));
        
        for (Train train : trainList) {
            int trainNumber = train.getTrainNumber();
            String status = permanentHoldTrains.contains(trainNumber) ? "Permanent Hold" : 
                          dispatchedTrains.contains(trainNumber) ? "Dispatched" : "Incomplete";
            System.out.printf("%-12d | %-13d | %-14d | %s%n",
                trainNumber,
                train.getInboundTrack(),
                train.getOutboundTrack(),
                status);
        }
    }

    /**
     * Main entry point for the simulation
     */
    public static void main(String[] args) {
        TrainYardSimulator simulator = new TrainYardSimulator();
        try {
            simulator.loadYardConfiguration("Project2/theYardFile.csv");
            simulator.loadTrainSchedule("Project2/theFleetFile.csv");
            simulator.startSimulation();
        } catch (IOException e) {
            System.err.println("Error reading input files: " + e.getMessage());
            System.exit(1);
        }
    }
} 