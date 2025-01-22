/*
    Name : Colin Kirby
    Course : CNT 4714 - Spring 2025
    Assignment Title : Project 1 - An Event-driven Enterprise Simulation
    Date : Monday, January 20, 2025
*/

/**
 * Represents an item in the shopping cart with its associated quantity and position.
 * This class manages the relationship between inventory items and their cart-specific properties.
 */
public class CartItem {
    /** The inventory item associated with this cart item */
    private InventoryItem item;
    
    /** The quantity of this item in the cart */
    private int quantity;
    
    /** The position/number of this item in the cart (1-based indexing) */
    private int itemNumber;

    /**
     * Creates a new cart item with the specified inventory item, quantity, and position.
     * 
     * @param item The inventory item to add to the cart
     * @param quantity The quantity of the item being added
     * @param itemNumber The position of this item in the cart (1-based)
     */
    public CartItem(InventoryItem item, int quantity, int itemNumber) {
        this.item = item;
        this.quantity = quantity;
        this.itemNumber = itemNumber;
    }

    /**
     * @return The inventory item associated with this cart item
     */
    public InventoryItem getItem() {
        return item;
    }

    /**
     * @return The quantity of this item in the cart
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * @return The position/number of this item in the cart
     */
    public int getItemNumber() {
        return itemNumber;
    }

    /**
     * Calculates the discount percentage based on the quantity ordered.
     * Discount tiers:
     * - 20% off for 15 or more items
     * - 15% off for 10-14 items
     * - 10% off for 5-9 items
     * - No discount for less than 5 items
     *
     * @param quantity The number of items ordered
     * @return The discount percentage (0, 10, 15, or 20)
     */
    private int getDiscountPercentage(int quantity) {
        if (quantity >= 15) return 20;
        if (quantity >= 10) return 15;
        if (quantity >= 5) return 10;
        return 0;
    }

    /**
     * Generates a string representation of the cart item including:
     * - Item number in cart
     * - SKU (item ID)
     * - Description
     * - Unit price
     * - Quantity
     * - Total price (after applying any quantity-based discounts)
     *
     * @return A formatted string containing all item details
     */
    @Override
    public String toString() {
        double unitPrice = item.getPrice();
        int discountPercent = getDiscountPercentage(quantity);
        double totalPrice = quantity * unitPrice * (1 - discountPercent/100.0);
        
        return String.format("Item %d - SKU: %s, Desc: %s, Price Ea. $%.2f, Qty: %d, Total: $%.2f",
            itemNumber,
            item.getItemID(),
            item.getDescription(),
            unitPrice,
            quantity,
            totalPrice);
    }
}
