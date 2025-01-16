public class CartItem {
    private InventoryItem item;
    private int quantity;
    private int itemNumber;

    public CartItem(InventoryItem item, int quantity, int itemNumber) {
        this.item = item;
        this.quantity = quantity;
        this.itemNumber = itemNumber;
    }

    public InventoryItem getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getItemNumber() {
        return itemNumber;
    }

    private int getDiscountPercentage(int quantity) {
        if (quantity >= 15) return 20;
        if (quantity >= 10) return 15;
        if (quantity >= 5) return 10;
        return 0;
    }

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
