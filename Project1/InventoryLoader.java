import java.io.*;
import java.util.HashMap;

public class InventoryLoader {
    // HashMap to store inventory data with Item ID as the key
    private HashMap<String, InventoryItem> inventory;

    // Constructor
    public InventoryLoader() {
        inventory = new HashMap<>();
    }

    // Method to load inventory data from a CSV file
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

    // Getter for the inventory
    public HashMap<String, InventoryItem> getInventory() {
        return inventory;
    }
}
