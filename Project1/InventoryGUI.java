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

public class InventoryGUI implements ActionListener {
    private JFrame frame;
    private JTextField itemIdField, quantityField;
    private JButton searchButton, addToCartButton, checkoutButton;
    private JTextArea resultArea, cartArea;
    private InventoryLoader inventoryLoader;
    private ArrayList<CartItem> cart; // Shopping cart
    private static final double TAX_RATE = 0.06; // 6% tax rate
    private int currentItemNumber = 1;
    private JLabel subtotalLabel;
    private JPanel mainPanel;

    // Constructor
    public InventoryGUI(InventoryLoader loader) {
        this.inventoryLoader = loader;
        this.cart = new ArrayList<>();

        // Set up the main frame
        frame = new JFrame("Nile Dot Com - Item Processing");
        frame.setSize(600, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create main panel with padding
        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Setup GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Create components with styling
        JLabel itemIdLabel = new JLabel(String.format("Enter Item ID for Item #%d:", currentItemNumber));
        itemIdField = new JTextField(15);
        JLabel quantityLabel = new JLabel(String.format("Enter Quantity for Item #%d:", currentItemNumber));
        quantityField = new JTextField(5);
        
        // Style the text fields
        itemIdField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        quantityField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Create details panel
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Item Details"));
        resultArea = new JTextArea(3, 40);
        resultArea.setEditable(false);
        resultArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Create cart panel
        JPanel cartPanel = new JPanel(new GridBagLayout());
        cartPanel.setBorder(BorderFactory.createTitledBorder("Shopping Cart"));
        cartArea = new JTextArea(8, 40);
        cartArea.setEditable(false);
        
        // Style the buttons
        searchButton = createStyledButton("Search Item");
        addToCartButton = createStyledButton("Add to Cart");
        checkoutButton = createStyledButton("Checkout");

        // Create subtotal label with styling
        subtotalLabel = new JLabel("Current Subtotal for 0 Item(s): $0.00");
        subtotalLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        
        // Add components to the main panel
        // Item ID row
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(itemIdLabel, gbc);
        
        gbc.gridx = 1;
        mainPanel.add(itemIdField, gbc);
        
        gbc.gridx = 2;
        mainPanel.add(searchButton, gbc);

        // Quantity row
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(quantityLabel, gbc);
        
        gbc.gridx = 1;
        mainPanel.add(quantityField, gbc);
        
        gbc.gridx = 2;
        mainPanel.add(addToCartButton, gbc);

        // Details area
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 3;
        mainPanel.add(new JScrollPane(resultArea), gbc);

        // Subtotal
        gbc.gridy = 3;
        mainPanel.add(subtotalLabel, gbc);

        // Cart area
        gbc.gridy = 4;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(new JScrollPane(cartArea), gbc);

        // Checkout button
        gbc.gridy = 5;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_END;
        mainPanel.add(checkoutButton, gbc);

        // Add action listeners
        searchButton.addActionListener(this);
        addToCartButton.addActionListener(this);
        checkoutButton.addActionListener(this);

        // Add main panel to frame
        frame.add(mainPanel);
        frame.setLocationRelativeTo(null); // Center on screen
        frame.setVisible(true);
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
            // Search for the item
            String itemId = itemIdField.getText().trim();
            InventoryItem item = inventoryLoader.getInventory().get(itemId);

            if (item != null) {
                resultArea.setText("Item Found:\n" + item);
            } else {
                resultArea.setText("Error: Item ID " + itemId + " not found.");
            }
        } else if (e.getSource() == addToCartButton) {
            // Add item to cart
            String itemId = itemIdField.getText().trim();
            String quantityText = quantityField.getText().trim();

            try {
                int quantity = Integer.parseInt(quantityText);
                InventoryItem item = inventoryLoader.getInventory().get(itemId);

                if (item == null) {
                    resultArea.setText("Error: Item ID " + itemId + " not found.");
                } else if (!item.isInStock() || item.getQuantity() < quantity) {
                    resultArea.setText("Error: Insufficient stock for Item ID " + itemId + ".");
                } else {
                    // Add item to cart and update inventory
                    cart.add(new CartItem(item, quantity));
                    item.setQuantity(item.getQuantity() - quantity);

                    // Update displays
                    updateCartDisplay();
                    updateSubtotalLabel();
                    
                    // Increment item number and update labels
                    currentItemNumber++;
                    updateLabels();

                    // Clear input fields
                    itemIdField.setText("");
                    quantityField.setText("");
                    
                    // Show success message
                    resultArea.setText("Item added to cart:\n" + item);
                }
            } catch (NumberFormatException ex) {
                resultArea.setText("Error: Invalid quantity. Please enter a valid number.");
            }
        } else if (e.getSource() == checkoutButton) {
            handleCheckout();
        }
    }

    private void updateCartDisplay() {
        StringBuilder cartContent = new StringBuilder("Current Cart:\n");
        for (CartItem cartItem : cart) {
            cartContent.append(cartItem).append("\n");
        }
        cartArea.setText(cartContent.toString());
    }

    private void handleCheckout() {
        if (cart.isEmpty()) {
            resultArea.setText("Error: Cart is empty!");
            return;
        }

        // Calculate totals
        double subtotal = calculateSubtotal();
        double discount = calculateDiscount(subtotal);
        double tax = (subtotal - discount) * TAX_RATE;
        double total = subtotal - discount + tax;

        // Generate invoice
        String invoice = generateInvoice(subtotal, discount, tax, total);
        resultArea.setText(invoice);

        // Log transaction
        logTransaction(subtotal, discount, tax, total);

        // Clear cart
        cart.clear();
        updateCartDisplay();

        // After successful checkout, reset item number
        currentItemNumber = 1;
        updateLabels();
        updateSubtotalLabel();
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
        // Update all numbered labels
        for (Component comp : frame.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if (label.getText().contains("Item ID for Item #")) {
                    label.setText(String.format("Enter Item ID for Item #%d:", currentItemNumber));
                } else if (label.getText().contains("Quantity for Item #")) {
                    label.setText(String.format("Enter Quantity for Item #%d:", currentItemNumber));
                } else if (label.getText().contains("Details for Item #")) {
                    label.setText(String.format("Details for Item #%d:", currentItemNumber));
                }
            }
        }
    }

    private void updateSubtotalLabel() {
        double subtotal = calculateSubtotal();
        subtotalLabel.setText(String.format("Current Subtotal for %d Item(s): $%.2f", 
            cart.size(), subtotal));
    }
}
