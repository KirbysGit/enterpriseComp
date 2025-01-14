public class Main {
    public static void main(String[] args) {
        // Load the inventory data
        InventoryLoader loader = new InventoryLoader();
        loader.loadInventory("inventory.csv"); // Ensure the file path is correct

        // Start the GUI
        new InventoryGUI(loader);
    }
}


