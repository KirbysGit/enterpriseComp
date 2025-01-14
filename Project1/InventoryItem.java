public class InventoryItem {
    private String itemID;
    private String description;
    private boolean inStock;
    private int quantity;
    private double price;

    // Constructor
    public InventoryItem(String itemID, String description, boolean inStock, int quantity, double price) {
        this.itemID = itemID;
        this.description = description;
        this.inStock = inStock;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters
    public String getItemID() { return itemID; }
    public String getDescription() { return description; }
    public boolean isInStock() { return inStock; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }

    // Setter for quantity
    public void setQuantity(int quantity) {
        this.quantity = quantity;

        // Update inStock based on quantity
        this.inStock = quantity > 0;
    }

    @Override
    public String toString() {
        return "ID: " + itemID + ", Description: " + description + ", In Stock: " + inStock +
               ", Quantity: " + quantity + ", Price: $" + price;
    }
}
