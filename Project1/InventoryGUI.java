import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.BorderFactory;
import java.awt.Color;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

public class InventoryGUI implements ActionListener {
    private JFrame frame;
    private JTextField itemIdField, quantityField;
    private JButton searchButton, addToCartButton, checkoutButton, emptyCartButton, exitButton, deleteLastItemButton;
    private JTextArea resultArea, cartArea;
    private InventoryLoader inventoryLoader;
    private ArrayList<CartItem> cart; // Shopping cart
    private static final double TAX_RATE = 0.06; // 6% tax rate
    private int currentItemNumber = 1;
    private JLabel subtotalLabel;
    private JPanel mainPanel;
    private boolean searchSuccessful = false;
    private JLabel itemIdLabel, quantityLabel, detailsLabel;
    private static final int MAX_CART_SIZE = 5;

    // Constructor
    public InventoryGUI(InventoryLoader loader) {
        this.inventoryLoader = loader;
        this.cart = new ArrayList<>();

        // Set up the main frame
        frame = new JFrame("Nile.Com - Spring 2025");
        frame.setSize(800, 900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create main panel with padding
        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Setup GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Create top section panel
        JPanel topSection = new JPanel(new GridBagLayout());
        topSection.setBorder(BorderFactory.createTitledBorder("Item Entry"));

        // Create input fields with labels
        itemIdLabel = new JLabel(String.format("Enter Item ID for Item #%d:", currentItemNumber));
        itemIdField = new JTextField(15);
        quantityLabel = new JLabel(String.format("Enter Quantity for Item #%d:", currentItemNumber));
        quantityField = new JTextField(5);
        detailsLabel = new JLabel(String.format("Details for Item #%d:", currentItemNumber));
        resultArea = new JTextArea(3, 40);
        resultArea.setEditable(false);
        
        // Style the text fields
        itemIdField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        quantityField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Create subtotal label
        subtotalLabel = new JLabel("Current Subtotal for 0 Item(s): $0.00");
        subtotalLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Add components to top section
        GridBagConstraints topGbc = new GridBagConstraints();
        topGbc.fill = GridBagConstraints.HORIZONTAL;
        topGbc.insets = new Insets(5, 5, 5, 5);
        topGbc.gridwidth = 1;

        // Item ID row
        topGbc.gridx = 0; topGbc.gridy = 0;
        topSection.add(itemIdLabel, topGbc);
        topGbc.gridx = 1;
        topSection.add(itemIdField, topGbc);

        // Quantity row
        topGbc.gridx = 0; topGbc.gridy = 1;
        topSection.add(quantityLabel, topGbc);
        topGbc.gridx = 1;
        topSection.add(quantityField, topGbc);

        // Details row
        topGbc.gridx = 0; topGbc.gridy = 2;
        topGbc.gridwidth = 2;
        topSection.add(detailsLabel, topGbc);
        topGbc.gridy = 3;
        topSection.add(new JScrollPane(resultArea), topGbc);

        // Subtotal row
        topGbc.gridy = 4;
        topSection.add(subtotalLabel, topGbc);

        // Create cart panel
        JPanel cartPanel = new JPanel(new GridBagLayout());
        cartPanel.setBorder(BorderFactory.createTitledBorder("Shopping Cart"));
        cartArea = new JTextArea(12, 50);
        cartArea.setEditable(false);
        cartArea.setText("Your Shopping Cart is Currently Empty");
        cartArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        // Create buttons
        searchButton = createStyledButton(String.format("Search for Item #%d", currentItemNumber));
        searchButton.setEnabled(true);
        deleteLastItemButton = createStyledButton("Delete Last Item Added to Cart");
        emptyCartButton = createStyledButton("Empty Cart - Start A New Order");
        addToCartButton = createStyledButton(String.format("Add Item #%d to Cart", currentItemNumber));
        checkoutButton = createStyledButton("Checkout");
        exitButton = createStyledButton("Exit (Close App)");
        
        // Style exit button
        exitButton.setBackground(new Color(180, 70, 70));
        exitButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(170, 60, 60)),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)));

        // Create button panel
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBorder(BorderFactory.createTitledBorder("User Controls"));
        
        // Add buttons to button panel
        GridBagConstraints buttonGbc = new GridBagConstraints();
        buttonGbc.fill = GridBagConstraints.HORIZONTAL;
        buttonGbc.insets = new Insets(5, 5, 5, 5);
        buttonGbc.gridx = 0; buttonGbc.gridy = 0;
        buttonPanel.add(searchButton, buttonGbc);
        buttonGbc.gridx = 1;
        buttonPanel.add(deleteLastItemButton, buttonGbc);
        buttonGbc.gridx = 2;
        buttonPanel.add(emptyCartButton, buttonGbc);
        buttonGbc.gridy = 1;
        buttonGbc.gridx = 0;
        buttonPanel.add(addToCartButton, buttonGbc);
        buttonGbc.gridx = 1;
        buttonPanel.add(checkoutButton, buttonGbc);
        buttonGbc.gridx = 2;
        buttonPanel.add(exitButton, buttonGbc);

        // Add all sections to main panel
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(topSection, gbc);
        
        gbc.gridy = 1;
        mainPanel.add(cartPanel, gbc);
        
        gbc.gridy = 2;
        mainPanel.add(new JScrollPane(cartArea), gbc);
        
        gbc.gridy = 3;
        mainPanel.add(buttonPanel, gbc);

        // Add action listeners
        searchButton.addActionListener(this);
        deleteLastItemButton.addActionListener(this);
        emptyCartButton.addActionListener(this);
        addToCartButton.addActionListener(this);
        checkoutButton.addActionListener(this);
        exitButton.addActionListener(this);

        // Add main panel to frame
        frame.add(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Both fields should be enabled from the start
        itemIdField.setEnabled(true);
        quantityField.setEnabled(true);
        
        // Add document listeners
        itemIdField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { 
                searchSuccessful = false; 
                updateButtonStates(); 
            }
            public void removeUpdate(DocumentEvent e) { 
                searchSuccessful = false; 
                updateButtonStates(); 
            }
            public void insertUpdate(DocumentEvent e) { 
                searchSuccessful = false; 
                updateButtonStates(); 
            }
        });

        quantityField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { updateButtonStates(); }
            public void removeUpdate(DocumentEvent e) { updateButtonStates(); }
            public void insertUpdate(DocumentEvent e) { updateButtonStates(); }
        });
        
        // Initial button states
        updateButtonStates();
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 120, 170)),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)));
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchButton) {
            String itemId = itemIdField.getText().trim();
            if (itemId.isEmpty()) {
                resultArea.setText("Error: Please enter an Item ID");
                return;
            }

            InventoryItem item = inventoryLoader.getInventory().get(itemId);

            if (item != null) {
                if (!item.isInStock() || item.getQuantity() <= 0) {
                    // Show error dialog for out of stock items
                    JOptionPane.showMessageDialog(
                        frame,
                        "Sorry... that item is out of stock, please try another item",
                        "Nile Dot Com - ERROR",
                        JOptionPane.ERROR_MESSAGE
                    );
                    
                    // Clear both item ID and quantity fields
                    itemIdField.setText("");
                    quantityField.setText("");
                    resultArea.setText("");
                    searchSuccessful = false;
                    updateButtonStates();
                    return;
                }
                
                searchSuccessful = true;
                
                // Get quantity (if entered)
                int quantity = 0;
                try {
                    quantity = Integer.parseInt(quantityField.getText().trim());
                } catch (NumberFormatException ex) {
                    // If no quantity entered or invalid, just show item details without quantity-specific info
                }

                double unitPrice = item.getPrice();
                int discountPercent = getDiscountPercentage(quantity);
                double finalPrice = quantity * unitPrice * (1 - discountPercent/100.0);
                
                // Format the result string with all required information
                StringBuilder result = new StringBuilder();
                result.append(String.format("%s \"%s\" %s", 
                    itemId,
                    item.getDescription(),
                    formatCurrency(unitPrice)));
                
                if (quantity > 0) {
                    result.append(String.format(" %d %d%% %s",
                        quantity,
                        discountPercent,
                        formatCurrency(finalPrice)));
                }
                
                resultArea.setText(result.toString());
                updateButtonStates();

                // On successful search, increment the item number
                currentItemNumber++;
                updateLabels();  // This updates all labels including Details
            } else {
                // Show error dialog for items not found in inventory
                JOptionPane.showMessageDialog(
                    frame,
                    "Item ID " + itemId + " not in file",
                    "Nile Dot Com - ERROR",
                    JOptionPane.ERROR_MESSAGE
                );
                
                // Clear the item ID field and result area
                itemIdField.setText("");
                resultArea.setText("");
                searchSuccessful = false;
                updateButtonStates();
                
                // Note: We don't increment currentItemNumber here
                // as the search was unsuccessful
            }
        } else if (e.getSource() == addToCartButton) {
            if (cart.size() >= MAX_CART_SIZE) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Cart is full (5 items maximum). Please checkout, delete items, or start a new order.",
                    "Nile Dot Com - Cart Full",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            String itemId = itemIdField.getText().trim();
            String quantityText = quantityField.getText().trim();

            try {
                int quantity = Integer.parseInt(quantityText);
                InventoryItem item = inventoryLoader.getInventory().get(itemId);

                if (item != null && item.isInStock() && item.getQuantity() >= quantity) {
                    // Add item to cart with current cart size + 1 as the item number
                    cart.add(new CartItem(item, quantity, cart.size() + 1));
                    item.setQuantity(item.getQuantity() - quantity);

                    // Update displays
                    updateCartDisplay();
                    updateSubtotalLabel();
                    
                    // Clear only input fields, keep the result area (details) displayed
                    itemIdField.setText("");
                    quantityField.setText("");
                    
                    // Reset search status
                    searchSuccessful = false;
                    updateButtonStates();
                }
            } catch (NumberFormatException ex) {
                resultArea.setText("Error: Invalid quantity. Please enter a valid number.");
            }
        } else if (e.getSource() == checkoutButton) {
            handleCheckout();
        } else if (e.getSource() == emptyCartButton) {
            handleEmptyCart();
        } else if (e.getSource() == exitButton) {
            System.exit(0);
        } else if (e.getSource() == deleteLastItemButton) {
            handleDeleteLastItem();
        }
    }

    private void updateCartDisplay() {
        if (cart.isEmpty()) {
            cartArea.setText("Your Shopping Cart is Currently Empty");
        } else {
            StringBuilder cartContent = new StringBuilder();
            cartContent.append(String.format("Your Shopping Cart Currently Contains %d Item(s)\n\n", cart.size()));
            
            // Create a separator line using regular dashes
            String separator = "-".repeat(80) + "\n";
            
            for (CartItem cartItem : cart) {
                InventoryItem item = cartItem.getItem();
                double unitPrice = item.getPrice();
                int discountPercent = getDiscountPercentage(cartItem.getQuantity());
                double totalPrice = cartItem.getQuantity() * unitPrice * (1 - discountPercent/100.0);
                
                cartContent.append(String.format("Item %d - SKU: %s, Desc: %s, Price Ea. $%.2f, Qty: %d, Total: $%.2f\n",
                    cartItem.getItemNumber(),
                    item.getItemID(),
                    item.getDescription(),
                    unitPrice,
                    cartItem.getQuantity(),
                    totalPrice));
                
                cartContent.append(separator);
            }
            
            cartArea.setText(cartContent.toString());
            cartArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        }
        updateButtonStates();
    }

    private void handleCheckout() {
        if (cart.isEmpty()) {
            return;
        }

        // Get current date and time
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        
        // Format for invoice display (January 8, 2025, 3:28:45 PM EST)
        String invoiceDateTime = now.format(java.time.format.DateTimeFormatter
            .ofPattern("MMMM d, yyyy, h:mm:ss a")) + " EST";
        
        // Format for transaction ID (DDMMYYYYHHMMSS)
        String transactionId = now.format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyyHHmmss"));
        
        // Create invoice message
        StringBuilder invoice = new StringBuilder();
        invoice.append("Nile Dot Com - FINAL INVOICE\n\n");
        invoice.append("Date: ").append(invoiceDateTime).append("\n\n");
        invoice.append("Number of line items: ").append(cart.size()).append("\n\n");
        invoice.append("Item# / ID / Title / Price / Qty / Disc % / Subtotal:\n\n");

        // Add each item
        double orderSubtotal = 0.0;
        int itemNumber = 1;
        
        for (CartItem cartItem : cart) {
            InventoryItem item = cartItem.getItem();
            int quantity = cartItem.getQuantity();
            double unitPrice = item.getPrice();
            int discountPercent = getDiscountPercentage(quantity);
            double itemTotal = quantity * unitPrice * (1 - discountPercent/100.0);
            orderSubtotal += itemTotal;

            invoice.append(String.format("%d. %s \"%s\" %s %d %d%% %s\n",
                itemNumber++,
                item.getItemID(),
                item.getDescription(),
                formatCurrency(unitPrice),
                quantity,
                discountPercent,
                formatCurrency(itemTotal)));
        }

        // Add totals
        double taxAmount = orderSubtotal * TAX_RATE;
        double orderTotal = orderSubtotal + taxAmount;

        invoice.append("\nOrder subtotal: ").append(formatCurrency(orderSubtotal)).append("\n");
        invoice.append("Tax rate: ").append(String.format("%.0f%%", TAX_RATE * 100)).append("\n");
        invoice.append("Tax amount: ").append(formatCurrency(taxAmount)).append("\n");
        invoice.append("ORDER TOTAL: ").append(formatCurrency(orderTotal)).append("\n\n");
        invoice.append("Thanks for shopping at Nile Dot Com!");

        // Show invoice
        JOptionPane.showMessageDialog(
            frame,
            invoice.toString(),
            "Nile Dot Com - FINAL INVOICE",
            JOptionPane.INFORMATION_MESSAGE
        );

        // Write to transaction.csv
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("transactions.csv", true))) {
            for (CartItem cartItem : cart) {
                InventoryItem item = cartItem.getItem();
                int quantity = cartItem.getQuantity();
                double unitPrice = item.getPrice();
                int discountPercent = getDiscountPercentage(quantity);
                double itemTotal = quantity * unitPrice * (1 - discountPercent/100.0);

                writer.write(String.format("%s, %s, %s, %d, %s, %d%%, %s\n",
                    transactionId,
                    item.getItemID(),
                    item.getDescription(),
                    quantity,
                    formatCurrency(unitPrice),
                    discountPercent,
                    formatCurrency(itemTotal)));
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                frame,
                "Error writing to transaction file: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }

        // Clear cart and reset GUI
        cart.clear();
        currentItemNumber = 1;
        
        // Clear and disable input fields
        itemIdField.setText("");
        quantityField.setText("");
        resultArea.setText("");
        itemIdField.setEnabled(false);
        quantityField.setEnabled(false);
        
        // Update displays
        updateCartDisplay();
        updateLabels();
        updateSubtotalLabel();
        
        // Disable most buttons, only leave Empty Cart and Exit enabled
        searchButton.setEnabled(false);
        addToCartButton.setEnabled(false);
        deleteLastItemButton.setEnabled(false);
        checkoutButton.setEnabled(false);
        
        // Keep these buttons enabled
        emptyCartButton.setEnabled(true);
        exitButton.setEnabled(true);
        
        // Update button appearances
        searchButton.setBackground(new Color(200, 200, 200));
        addToCartButton.setBackground(new Color(200, 200, 200));
        deleteLastItemButton.setBackground(new Color(200, 200, 200));
        checkoutButton.setBackground(new Color(200, 200, 200));
        
        // Keep enabled buttons colored
        emptyCartButton.setBackground(new Color(70, 130, 180));
        exitButton.setBackground(new Color(180, 70, 70)); // Keep exit button red
        
        searchSuccessful = false;
    }

    private double calculateSubtotal() {
        double subtotal = 0;
        for (CartItem item : cart) {
            subtotal += item.getItem().getPrice() * item.getQuantity();
        }
        return subtotal;
    }

    private double calculateDiscount(double subtotal) {
        // Simple discount rule: 10% off for purchases over $100
        return subtotal >= 100 ? subtotal * 0.10 : 0;
    }

    private String generateInvoice(double subtotal, double discount, double tax, double total) {
        StringBuilder invoice = new StringBuilder("=== INVOICE ===\n\n");
        invoice.append("Items:\n");
        for (CartItem item : cart) {
            invoice.append(String.format("%s x%d: $%.2f\n", 
                item.getItem().getDescription(),
                item.getQuantity(),
                item.getItem().getPrice() * item.getQuantity()));
        }
        invoice.append(String.format("\nSubtotal: $%.2f\n", subtotal));
        invoice.append(String.format("Discount: $%.2f\n", discount));
        invoice.append(String.format("Tax (6%%): $%.2f\n", tax));
        invoice.append(String.format("Total: $%.2f", total));
        return invoice.toString();
    }

    private void logTransaction(double subtotal, double discount, double tax, double total) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        try (FileWriter fw = new FileWriter("transactions.csv", true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            
            StringBuilder transaction = new StringBuilder();
            transaction.append(timestamp).append(",");
            // Add items
            for (CartItem item : cart) {
                transaction.append(item.getItem().getItemID())
                          .append(":").append(item.getQuantity()).append(";");
            }
            transaction.append(",")
                      .append(String.format("%.2f", subtotal)).append(",")
                      .append(String.format("%.2f", discount)).append(",")
                      .append(String.format("%.2f", tax)).append(",")
                      .append(String.format("%.2f", total)).append("\n");
            
            bw.write(transaction.toString());
            
        } catch (IOException e) {
            resultArea.setText("Error logging transaction: " + e.getMessage());
        }
    }

    private void updateLabels() {
        // Update input field labels
        itemIdLabel.setText(String.format("Enter Item ID for Item #%d:", currentItemNumber));
        quantityLabel.setText(String.format("Enter Quantity for Item #%d:", currentItemNumber));
        
        // Update button texts
        searchButton.setText(String.format("Search for Item #%d", currentItemNumber));
        addToCartButton.setText(String.format("Add Item #%d to Cart", currentItemNumber));
        
        // Details label stays at current number
        detailsLabel.setText(String.format("Details for Item #%d:", currentItemNumber - 1));
    }

    private void updateSubtotalLabel() {
        double subtotal = calculateSubtotal();
        subtotalLabel.setText(String.format("Current Subtotal for %d Item(s): $%.2f", 
            cart.size(), subtotal));
    }

    private void handleEmptyCart() {
        if (!cart.isEmpty() || !itemIdField.isEnabled()) {  // Check if cart is not empty OR fields are disabled
            int result = JOptionPane.showConfirmDialog(
                frame,
                "Are you sure you want to empty the cart and start a new order?",
                "Confirm Empty Cart",
                JOptionPane.YES_NO_OPTION
            );
            
            if (result == JOptionPane.YES_OPTION) {
                // Clear cart and reset everything
                cart.clear();
                currentItemNumber = 1;
                
                // Clear and re-enable input fields
                itemIdField.setText("");
                quantityField.setText("");
                resultArea.setText("");
                itemIdField.setEnabled(true);
                quantityField.setEnabled(true);
                
                // Re-enable and reset buttons
                searchButton.setEnabled(true);
                addToCartButton.setEnabled(false);
                deleteLastItemButton.setEnabled(false);
                checkoutButton.setEnabled(false);
                emptyCartButton.setEnabled(true);
                exitButton.setEnabled(true);
                
                // Reset button colors
                searchButton.setBackground(new Color(70, 130, 180));
                addToCartButton.setBackground(new Color(200, 200, 200));
                deleteLastItemButton.setBackground(new Color(200, 200, 200));
                checkoutButton.setBackground(new Color(200, 200, 200));
                emptyCartButton.setBackground(new Color(70, 130, 180));
                exitButton.setBackground(new Color(180, 70, 70));
                
                // Update displays
                updateCartDisplay();
                updateLabels();
                updateSubtotalLabel();
                searchSuccessful = false;
                updateButtonStates();
            }
        }
    }

    private void handleDeleteLastItem() {
        if (!cart.isEmpty()) {
            CartItem lastItem = cart.remove(cart.size() - 1);
            // Restore the quantity to inventory
            InventoryItem item = lastItem.getItem();
            item.setQuantity(item.getQuantity() + lastItem.getQuantity());
            
            // Update displays
            updateCartDisplay();
            updateSubtotalLabel();
            
            // Clear the details area
            resultArea.setText("");
            
            // Decrement item number
            if (currentItemNumber > 1) {
                currentItemNumber--;
                updateLabels();
            }
            
            searchSuccessful = false;
            updateButtonStates();
        }
    }

    // Add new method to check if input fields are valid
    private boolean hasValidInput() {
        String itemId = itemIdField.getText().trim();
        String quantity = quantityField.getText().trim();
        
        // Check if both fields have content and quantity is a positive number
        if (!itemId.isEmpty() && !quantity.isEmpty()) {
            try {
                int qty = Integer.parseInt(quantity);
                return qty > 0;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    // Update the button states method
    private void updateButtonStates() {
        boolean cartHasItems = !cart.isEmpty();
        boolean cartIsFull = cart.size() >= MAX_CART_SIZE;
        
        // Search and Add buttons are only enabled if cart isn't full
        searchButton.setEnabled(!searchSuccessful && !cartIsFull);
        addToCartButton.setEnabled(searchSuccessful && !cartIsFull && hasValidInput());
        
        // Cart-dependent buttons
        deleteLastItemButton.setEnabled(cartHasItems);
        checkoutButton.setEnabled(cartHasItems);
        emptyCartButton.setEnabled(true);
        exitButton.setEnabled(true);
        
        // Update button appearances
        searchButton.setBackground(searchButton.isEnabled() ? 
            new Color(70, 130, 180) : new Color(200, 200, 200));
        addToCartButton.setBackground(addToCartButton.isEnabled() ? 
            new Color(70, 130, 180) : new Color(200, 200, 200));
        deleteLastItemButton.setBackground(deleteLastItemButton.isEnabled() ? 
            new Color(70, 130, 180) : new Color(200, 200, 200));
        checkoutButton.setBackground(checkoutButton.isEnabled() ? 
            new Color(70, 130, 180) : new Color(200, 200, 200));
        
        // If cart is full, disable input fields
        itemIdField.setEnabled(!cartIsFull);
        quantityField.setEnabled(!cartIsFull);
        
        // If cart is full, show message in result area
        if (cartIsFull) {
            resultArea.setText("Cart is full (5 items maximum). Please checkout, delete items, or start a new order.");
        }
    }

    // Calculate discount percentage based on quantity
    private int getDiscountPercentage(int quantity) {
        if (quantity >= 15) return 20;
        if (quantity >= 10) return 15;
        if (quantity >= 5) return 10;
        return 0;
    }

    // Format currency
    private String formatCurrency(double amount) {
        return String.format("$%.2f", amount);
    }
}
