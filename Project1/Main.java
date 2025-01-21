/*
    Name : Colin Kirby
    Course : CNT 4714 - Spring 2025
    Assignment Title : Project 1 - An Event-driven Enterprise Simulation
    Date : Monday, January 20, 2025
*/

public class Main {
    public static void main(String[] args) {
        // Load the inventory data
        InventoryLoader loader = new InventoryLoader();
        loader.loadInventory("inventory.csv"); // Ensure the file path is correct

        // Start the GUI
        new InventoryGUI(loader);
    }
}


