/*
 * Name: Colin Kirby
 * Course: CNT 4714 Spring 2025
 * Assignment Title: Project 2 - Multi-Threaded Programming in Java
 * Date: February 7, 2025
 * 
 * Class: TrainYardSimulator.java
 * 
 * Description:
 * This class implements a train yard simulation using multi-threading concepts.
 * It manages the movement of trains through a switch yard, handling switch locks,
 * train dispatching, and preventing deadlocks. The simulation reads yard configuration
 * and train schedules from CSV files and ensures proper synchronization of train movements.
 */

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
 * This class is responsible for:
 * 1. Loading yard configuration (switch layouts)
 * 2. Loading train schedules
 * 3. Managing train threads via ExecutorService
 * 4. Tracking train statuses (dispatched/permanent hold)
 * 5. Generating final simulation reports
 */
public class TrainYardSimulator {
    // Maximum number of concurrent trains allowed in the simulation
    private static final int MAX_THREADS = 30;
    
    // Maps switch IDs to Switch objects for the entire yard
    private final Map<Integer, Switch> switches = new HashMap<>();
    
    // List of all trains in the simulation
    private final List<Train> trains = new ArrayList<>();
    
    // Cache of valid routes (inboundTrack-outboundTrack -> required switches)
    private final Map<String, List<Switch>> routeCache = new HashMap<>();
    
    // Sets to track the final status of each train
    private final Set<Integer> dispatchedTrains = new HashSet<>();
    private final Set<Integer> permanentHoldTrains = new HashSet<>();

    /**
     * Loads the yard configuration from a CSV file.
     * Each line represents a valid route through the yard with format:
     * inboundTrack,firstSwitch,secondSwitch,thirdSwitch,outboundTrack
     * 
     * @param yardFile Path to the yard configuration CSV file
     * @throws IOException If there's an error reading the file
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

                // Create switches if they don't exist (switches are shared across routes)
                switches.putIfAbsent(firstSwitch, new Switch(firstSwitch));
                switches.putIfAbsent(secondSwitch, new Switch(secondSwitch));
                switches.putIfAbsent(thirdSwitch, new Switch(thirdSwitch));

                // Cache the route for quick lookup during train creation
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
     * Loads the train schedule from a CSV file.
     * Each line represents a train with format:
     * trainNumber,inboundTrack,outboundTrack
     * 
     * If a train's route is not valid (not in yard configuration),
     * the train is created but marked for permanent hold.
     * 
     * @param fleetFile Path to the fleet schedule CSV file
     * @throws IOException If there's an error reading the file
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
                
                // Create train object regardless of route validity
                Train train = new Train(trainNumber, inboundTrack, outboundTrack, 
                                      requiredSwitches != null ? requiredSwitches : new ArrayList<>(), 
                                      this);
                trains.add(train);
                
                // If no valid route exists, mark train for permanent hold
                if (requiredSwitches == null) {
                    System.out.println("*************");
                    System.out.println("Train " + trainNumber + " is on permanent hold and cannot be dispatched");
                    System.out.println("*************");
                    permanentHoldTrains.add(trainNumber);
                }
            }
        }
    }

    /**
     * Starts the simulation by creating a thread pool and submitting valid trains.
     * Only trains that are not on permanent hold are submitted to the executor.
     * The simulation runs until all trains complete or timeout occurs.
     */
    public void startSimulation() {
        System.out.println("$ $ $ TRAIN MOVEMENT SIMULATION BEGINS........... $ $ $");
        
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
        
        // Only submit trains that aren't on permanent hold
        for (Train train : trains) {
            if (!permanentHoldTrains.contains(train.getTrainNumber())) {
                executor.submit(train);
            }
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

    /**
     * Marks a train as successfully dispatched.
     * Called by Train threads upon successful completion.
     * 
     * @param trainNumber The number of the train to mark as dispatched
     */
    public void markTrainDispatched(int trainNumber) {
        dispatchedTrains.add(trainNumber);
    }

    /**
     * Marks a train as permanently held.
     * Called either during loading (invalid route) or by Train threads
     * when they exceed maximum retry attempts.
     * 
     * @param trainNumber The number of the train to mark as permanently held
     */
    public void markTrainPermanentHold(int trainNumber) {
        permanentHoldTrains.add(trainNumber);
    }

    /**
     * Prints the final status report of all trains in the simulation.
     * Trains are sorted by train number for consistent output.
     * Possible statuses are:
     * - Dispatched: Train successfully completed its route
     * - Permanent Hold: Train either had invalid route or exceeded retry attempts
     * - Incomplete: Train could not complete its route within the simulation time
     */
    private void printFinalStatus() {
        System.out.println("\nFinal Train Status Report:");
        System.out.println("Train Number | Inbound Track | Outbound Track | Status");
        System.out.println("------------------------------------------------------");
        
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
        
        System.out.println("\n$ $ $ FINAL STATUS REPORT COMPLETED $ $ $");
    }

    /**
     * Main entry point for the simulation.
     * Loads configuration files and starts the simulation.
     * 
     * @param args Command line arguments (not used)
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