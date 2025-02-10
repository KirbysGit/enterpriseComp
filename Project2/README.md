# Multi-threaded Train Yard Simulator
CNT 4714 - Spring 2025 - Project 2

## Project Description
This project implements a multi-threaded simulation of a train switch yard using Java's concurrency utilities. The simulation models Precision Scheduled Railroading (PSR) concepts, where trains must acquire and release track switches in a controlled manner to prevent deadlocks.

## Files in the Project
1. `TrainYardSimulator.java`
   - Main class that manages the simulation
   - Handles loading of configuration files
   - Manages thread pool and train dispatching
   - Generates final status reports

2. `Train.java`
   - Implements the Runnable interface for train threads
   - Handles switch acquisition and release logic
   - Manages train movement through the yard
   - Implements retry logic for failed switch acquisitions

3. `Switch.java`
   - Represents a switch in the train yard
   - Implements thread-safe locking using ReentrantLock
   - Handles switch acquisition and release operations

4. `theYardFile.csv`
   - Configuration file for yard layout
   - Format: inboundTrack,firstSwitch,secondSwitch,thirdSwitch,outboundTrack
   - Each line represents a valid route through the yard

5. `theFleetFile.csv`
   - Train schedule configuration
   - Format: trainNumber,inboundTrack,outboundTrack
   - Each line represents a train to be dispatched

## How to Run the Simulation

1. Compile the Java files:
   ```bash

   javac Project2/*.java
   ```

2. Run the simulation:
   ```bash
   java Project2.TrainYardSimulator
   ```

## Output Format
The simulation provides detailed output showing:
- Train movements through the yard
- Switch acquisition and release operations
- Permanent hold notifications
- Final status report for all trains

Example output:
```
$ $ $ TRAIN MOVEMENT SIMULATION BEGINS........... $ $ $
Train 3: HOLDS LOCK on Switch 4
Train 3: HOLDS LOCK on Switch 3
...
$ $ $ SIMULATION ENDS $ $ $

Final Train Status Report:
Train Number | Inbound Track | Outbound Track | Status
------------------------------------------------------
3            | 5             | 4              | Dispatched
4            | 6             | 8              | Permanent Hold

$ $ $ FINAL STATUS REPORT COMPLETED $ $ $
```

## Author
Colin Kirby
CNT 4714 - Spring 2025
University of Central Florida 