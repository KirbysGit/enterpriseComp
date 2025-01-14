public class CartItem {
    private InventoryItem item;
    private int quantity;

    public CartItem(InventoryItem item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public InventoryItem getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "ID: " + item.getItemID() + ", Description: " + item.getDescription() +
               ", Quantity: " + quantity + ", Price: $" + (item.getPrice() * quantity);
    }
}
