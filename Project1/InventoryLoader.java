/*
    Name : Colin Kirby
    Course : CNT 4714 - Spring 2025
    Assignment Title : Project 1 - An Event-driven Enterprise Simulation
    Date : Monday, January 20, 2025
*/

import java.io.*;
import java.util.HashMap;

/**
 * Manages the loading and storage of inventory data from external files.
 * This class handles reading inventory items from a CSV file and maintains them in memory
 * for quick access using a HashMap data structure.
 */
public class InventoryLoader {
    /** 
     * Stores inventory items with their Item ID as the key for efficient lookup.
     * The HashMap provides O(1) access time when searching for items by their ID.
     */
    private HashMap<String, InventoryItem> inventory;

    /**
     * Creates a new InventoryLoader instance with an empty inventory HashMap.
     * The inventory will be populated when loadInventory() is called.
     */
    public InventoryLoader() {
        inventory = new HashMap<>();
    }

    /**
     * Loads inventory data from a CSV file into the inventory HashMap.
     * The CSV file should have the following format per line:
     * ItemID, "Description", InStock, Quantity, Price
     * 
     * Example: 22345532, "3 ft mini USB cable M-F", true, 444, 4.50
     * 
     * The method handles:
     * - Removing quotes from description fields
     * - Converting string values to appropriate data types
     * - Creating InventoryItem objects for each valid line
     * - Basic error handling for file operations
     *
     * @param filePath The path to the CSV file containing inventory data
     */
    public void loadInventory(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split the line into fields by commas
                String[] fields = line.split(",");
                
                // Parse fields
                String itemID = fields[0].trim();
                String description = fields[1].trim().replaceAll("\"", ""); // Remove quotes
                boolean inStock = Boolean.parseBoolean(fields[2].trim());
                int quantity = Integer.parseInt(fields[3].trim());
                double price = Double.parseDouble(fields[4].trim());
                
                // Create an InventoryItem object and store it
                InventoryItem item = new InventoryItem(itemID, description, inStock, quantity, price);
                inventory.put(itemID, item);
            }
            System.out.println("Inventory loaded successfully.");
        } catch (FileNotFoundException e) {
            System.err.println("Error: Inventory file not found.");
        } catch (IOException e) {
            System.err.println("Error reading inventory file.");
        } catch (Exception e) {
            System.err.println("Error processing inventory file: " + e.getMessage());
        }
    }

    /**
     * Returns the complete inventory HashMap containing all loaded items.
     * The returned HashMap maps item IDs (String) to InventoryItem objects.
     *
     * @return HashMap containing all inventory items with their IDs as keys
     */
    public HashMap<String, InventoryItem> getInventory() {
        return inventory;
    }
}
