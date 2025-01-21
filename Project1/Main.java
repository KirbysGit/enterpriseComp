/*
    Name : Colin Kirby
    Course : CNT 4714 - Spring 2025
    Assignment Title : Project 1 - An Event-driven Enterprise Simulation
    Date : Monday, January 20, 2025
*/

/**
 * Entry point for the Nile Dot Com e-store application.
 * This class initializes the inventory system and launches the graphical user interface.
 */
public class Main {
    /**
     * The main method that starts the application.
     * It performs two main tasks:
     * 1. Loads the inventory data from the CSV file into memory
     * 2. Initializes and displays the graphical user interface
     *
     * @param args Command line arguments (not used in this application)
     */
    public static void main(String[] args) {
        // Load the inventory data
        InventoryLoader loader = new InventoryLoader();
        loader.loadInventory("inventory.csv"); // Load data from the inventory file

        // Initialize and display the GUI
        new InventoryGUI(loader);
    }
}


