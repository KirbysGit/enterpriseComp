/**
 * Represents an item in the inventory system with its associated properties.
 * This class manages the core item data including stock status, quantity, and pricing.
 */
public class InventoryItem {
    /** The unique identifier (SKU) for this item */
    private String itemID;
    
    /** The descriptive name/title of the item */
    private String description;
    
    /** Flag indicating whether the item is currently in stock */
    private boolean inStock;
    
    /** The current quantity available in inventory */
    private int quantity;
    
    /** The unit price of the item */
    private double price;

    /**
     * Creates a new inventory item with the specified properties.
     * 
     * @param itemID The unique identifier (SKU) for the item
     * @param description The descriptive name/title of the item
     * @param inStock Whether the item is initially in stock
     * @param quantity The initial quantity available
     * @param price The unit price of the item
     */
    public InventoryItem(String itemID, String description, boolean inStock, int quantity, double price) {
        this.itemID = itemID;
        this.description = description;
        this.inStock = inStock;
        this.quantity = quantity;
        this.price = price;
    }

    /**
     * @return The unique identifier (SKU) of the item
     */
    public String getItemID() { return itemID; }

    /**
     * @return The descriptive name/title of the item
     */
    public String getDescription() { return description; }

    /**
     * @return True if the item is currently in stock, false otherwise
     */
    public boolean isInStock() { return inStock; }

    /**
     * @return The current quantity available in inventory
     */
    public int getQuantity() { return quantity; }

    /**
     * @return The unit price of the item
     */
    public double getPrice() { return price; }

    /**
     * Updates the quantity of the item in inventory and automatically
     * updates the inStock status based on the new quantity.
     * 
     * @param quantity The new quantity to set
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;

        // Update inStock based on quantity
        this.inStock = quantity > 0;
    }

    /**
     * Generates a string representation of the inventory item including
     * all its properties: ID, description, stock status, quantity, and price.
     *
     * @return A formatted string containing all item details
     */
    @Override
    public String toString() {
        return "ID: " + itemID + ", Description: " + description + ", In Stock: " + inStock +
               ", Quantity: " + quantity + ", Price: $" + price;
    }
}
