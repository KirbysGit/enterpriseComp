/*
    Name : Colin Kirby
    Course : CNT 4714 - Spring 2025
    Assignment Title : Project 1 - An Event-driven Enterprise Simulation
    Date : Monday, January 20, 2025
*/

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

/**
 * The main GUI class for the Nile Dot Com e-store application.
 * This class implements the graphical user interface for the shopping system, including:
 * - Item entry and search functionality
 * - Shopping cart management
 * - Order processing and checkout
 * - Transaction logging
 */
public class InventoryGUI implements ActionListener {
    /** The main application window */
    private JFrame frame;
    
    /** Text fields for item ID and quantity input */
    private JTextField itemIdField, quantityField;
    
    /** Control buttons for various shopping actions */
    private JButton searchButton, addToCartButton, checkoutButton, emptyCartButton, exitButton, deleteLastItemButton;
    
    /** Text areas for displaying item details and cart contents */
    private JTextArea resultArea, cartArea;
    
    /** Manages the inventory data and operations */
    private InventoryLoader inventoryLoader;
    
    /** List to store items currently in the shopping cart */
    private ArrayList<CartItem> cart;
    
    /** Tax rate constant for price calculations */
    private static final double TAX_RATE = 0.06; // 6% tax rate
    
    /** Tracks the current item number being processed */
    private int currentItemNumber = 1;
    
    /** Label for displaying the current subtotal */
    private JLabel subtotalLabel;
    
    /** The main container panel for all GUI components */
    private JPanel mainPanel;
    
    /** Flag indicating if the last search was successful */
    private boolean searchSuccessful = false;
    
    /** Labels for input fields and details section */
    private JLabel itemIdLabel, quantityLabel, detailsLabel;
    
    /** Maximum number of items allowed in the cart */
    private static final int MAX_CART_SIZE = 5;

    /**
     * Constructs the main GUI window and initializes all components.
     * Sets up:
     * - Main window and layout
     * - Input fields and labels
     * - Shopping cart display
     * - Control buttons
     * - Event listeners
     * 
     * @param loader The InventoryLoader instance containing the store's inventory data
     */
    public InventoryGUI(InventoryLoader loader) {
        this.inventoryLoader = loader;
        this.cart = new ArrayList<>();

        // Set up the main frame
        frame = new JFrame("Nile.Com - Spring 2025");
        frame.setSize(1000, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create main panel with padding
        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Setup GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;

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

        // Add components to top section with more spacing
        GridBagConstraints topGbc = new GridBagConstraints();
        topGbc.fill = GridBagConstraints.HORIZONTAL;
        topGbc.insets = new Insets(10, 10, 10, 10);
        topGbc.gridwidth = 1;
        topGbc.weightx = 1.0;

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
        JPanel cartPanel = new JPanel(new BorderLayout(10, 10));
        cartPanel.setBorder(BorderFactory.createTitledBorder("Shopping Cart"));
        cartPanel.setBackground(new Color(238, 238, 238));  // Lighter gray to match Item Entry
        
        // Create cart items panel
        JPanel cartItemsPanel = new JPanel(new GridLayout(6, 1, 0, 5));  // 6 rows (header + 5 items), 5px gap
        cartItemsPanel.setBackground(new Color(238, 238, 238));  // Match parent background
        cartItemsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));  // Add padding
        
        // Add header
        JLabel headerLabel = new JLabel("Your Shopping Cart is Currently Empty");
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        headerLabel.setForeground(Color.RED);
        cartItemsPanel.add(headerLabel);
        
        // Add 5 empty slots
        for (int i = 0; i < 5; i++) {
            JPanel slotPanel = new JPanel(new BorderLayout());
            slotPanel.setPreferredSize(new Dimension(850, 35));  // Adjusted size
            slotPanel.setBackground(Color.WHITE);  // Set item bars to white
            slotPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));  // Add gray border and padding
            cartItemsPanel.add(slotPanel);
        }
        
        // Wrap cartItemsPanel in another panel to control its size
        JPanel cartWrapperPanel = new JPanel(new BorderLayout());
        cartWrapperPanel.setBackground(new Color(238, 238, 238));  // Match parent background
        cartWrapperPanel.add(cartItemsPanel, BorderLayout.CENTER);
        cartPanel.add(cartWrapperPanel, BorderLayout.CENTER);

        // Create buttons with increased spacing
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10));  // 3 rows, 2 columns, 10px gaps
        buttonPanel.setBorder(BorderFactory.createTitledBorder("User Controls"));
        
        // Create and style buttons
        searchButton = createStyledButton(String.format("Search for Item #%d", currentItemNumber));
        addToCartButton = createStyledButton(String.format("Add Item #%d to Cart", currentItemNumber));
        deleteLastItemButton = createStyledButton("Delete Last Item Added to Cart");
        checkoutButton = createStyledButton("Check Out");
        emptyCartButton = createStyledButton("Empty Cart - Start A New Order");
        exitButton = createStyledButton("Exit (Close App)");
        
        // Style exit button
        exitButton.setBackground(new Color(180, 70, 70));
        
        // Add buttons in the desired order (left column, then right column)
        buttonPanel.add(searchButton);          // Column 1, Row 1
        buttonPanel.add(addToCartButton);       // Column 2, Row 1
        buttonPanel.add(deleteLastItemButton);  // Column 1, Row 2
        buttonPanel.add(checkoutButton);        // Column 2, Row 2
        buttonPanel.add(emptyCartButton);       // Column 1, Row 3
        buttonPanel.add(exitButton);            // Column 2, Row 3

        // Add all sections to main panel with proper constraints
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(topSection, gbc);
        
        gbc.gridy = 1;
        mainPanel.add(cartPanel, gbc);
        
        gbc.gridy = 2;
        mainPanel.add(buttonPanel, gbc);

        // Add action listeners
        searchButton.addActionListener(this);
        addToCartButton.addActionListener(this);
        deleteLastItemButton.addActionListener(this);
        checkoutButton.addActionListener(this);
        emptyCartButton.addActionListener(this);
        exitButton.addActionListener(this);

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

        // Add main panel to frame
        frame.add(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Initial button states
        updateButtonStates();
    }

    /**
     * Creates a styled button with consistent appearance.
     * Applies standard colors, borders, and padding to maintain UI consistency.
     *
     * @param text The text to display on the button
     * @return A JButton with the standard styling applied
     */
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

    /**
     * Handles all button click events in the GUI.
     * Implements the ActionListener interface to process user interactions:
     * - Search button: Looks up item details
     * - Add to Cart: Adds current item to cart
     * - Checkout: Processes the order
     * - Empty Cart: Clears the current order
     * - Delete Last Item: Removes most recent item
     * - Exit: Closes the application
     *
     * @param e The ActionEvent containing information about which button was clicked
     */
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
                    
                    // Clear only the item ID and quantity fields
                    itemIdField.setText("");
                    quantityField.setText("");
                    searchSuccessful = false;
                    updateButtonStates();
                    return;
                }
                
                // Check if requested quantity exceeds available stock
                try {
                    int requestedQuantity = Integer.parseInt(quantityField.getText().trim());
                    if (requestedQuantity > item.getQuantity()) {
                        // Show insufficient stock dialog
                        JOptionPane.showMessageDialog(
                            frame,
                            String.format("Insufficient stock. Only %d on hand. Please reduce the quantity.", 
                                item.getQuantity()),
                            "Nile Dot Com - ERROR",
                            JOptionPane.ERROR_MESSAGE
                        );
                        
                        // Clear only the quantity field
                        quantityField.setText("");
                        searchSuccessful = false;
                        updateButtonStates();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    // If quantity field is empty or invalid, proceed with showing item details
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

                // On successful search, increment the item number for details label
                currentItemNumber = cart.size() + 1;
                updateLabels();
            } else {
                // Show error dialog for items not found in inventory
                JOptionPane.showMessageDialog(
                    frame,
                    "Item ID " + itemId + " not in file",
                    "Nile Dot Com - ERROR",
                    JOptionPane.ERROR_MESSAGE
                );
                
                // Clear only the item ID field
                itemIdField.setText("");
                searchSuccessful = false;
                updateButtonStates();
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

                if (item != null && item.isInStock()) {
                    // Check if requested quantity exceeds available stock
                    if (quantity > item.getQuantity()) {
                        JOptionPane.showMessageDialog(
                            frame,
                            String.format("Insufficient stock. Only %d left. Please reduce the quantity.", 
                                item.getQuantity()),
                            "Nile Dot Com - ERROR",
                            JOptionPane.ERROR_MESSAGE
                        );
                        
                        // Clear only the quantity field
                        quantityField.setText("");
                        return;
                    }

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
                    
                    // Update labels after adding item
                    updateLabels();
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

    /**
     * Updates the shopping cart display panel.
     * - Updates the header to show current item count
     * - Displays each item in the cart with full details
     * - Maintains empty slots up to MAX_CART_SIZE
     * - Updates button states based on cart contents
     */
    private void updateCartDisplay() {
        // Get the cart items panel
        JPanel cartPanel = (JPanel) mainPanel.getComponent(1);
        JPanel cartWrapperPanel = (JPanel) cartPanel.getComponent(0);
        JPanel cartItemsPanel = (JPanel) cartWrapperPanel.getComponent(0);
        cartItemsPanel.removeAll();
        
        // Update header
        JLabel headerLabel;
        if (cart.isEmpty()) {
            headerLabel = new JLabel("Your Shopping Cart is Currently Empty");
        } else {
            headerLabel = new JLabel(String.format("Your Shopping Cart Currently Contains %d Item(s)", cart.size()));
        }
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        headerLabel.setForeground(Color.RED);
        cartItemsPanel.add(headerLabel);
        
        // Always create 5 slots
        for (int i = 0; i < 5; i++) {
            JPanel slotPanel = new JPanel(new BorderLayout());
            slotPanel.setPreferredSize(new Dimension(850, 35));
            slotPanel.setBackground(Color.WHITE);  // White background for item bars
            slotPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));  // Add gray border and padding
            
            if (i < cart.size()) {
                CartItem cartItem = cart.get(i);
                InventoryItem item = cartItem.getItem();
                double unitPrice = item.getPrice();
                int quantity = cartItem.getQuantity();
                double totalPrice = quantity * unitPrice * (1 - getDiscountPercentage(quantity)/100.0);
                
                String itemText = String.format("Item %d - SKU: %s, Desc: \"%s\", Price Ea. $%.2f, Qty: %d, Total: $%.2f",
                    i + 1,
                    item.getItemID(),
                    item.getDescription(),
                    unitPrice,
                    quantity,
                    totalPrice);
                
                JLabel itemLabel = new JLabel(itemText);
                itemLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                slotPanel.add(itemLabel, BorderLayout.CENTER);
            }
            
            cartItemsPanel.add(slotPanel);
        }
        
        cartItemsPanel.revalidate();
        cartItemsPanel.repaint();
        updateButtonStates();
    }

    /**
     * Processes the checkout operation.
     * - Generates a unique transaction ID
     * - Creates an invoice with all items and totals
     * - Logs the transaction to transactions.csv
     * - Displays the final invoice to the user
     * - Disables appropriate UI elements after checkout
     */
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
        
        // Format date and time components for transaction log
        String transactionDate = now.format(java.time.format.DateTimeFormatter.ofPattern("MMMM d"));
        String transactionYear = now.format(java.time.format.DateTimeFormatter.ofPattern("yyyy"));
        String transactionTime = now.format(java.time.format.DateTimeFormatter.ofPattern("h:mm:ss a")) + " EST";
        
        // Create invoice message
        StringBuilder invoice = new StringBuilder();
        invoice.append("Date: ").append(invoiceDateTime).append("\n\n");
        invoice.append("Number of line items: ").append(cart.size()).append("\n\n");
        invoice.append("Item# / ID / Title / Price / Qty / Disc % / Subtotal:\n\n");

        // Add each item
        double orderSubtotal = 0.0;
        int itemNumber = 1;
        
        // Write to transaction.csv
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("transactions.csv", true))) {
            for (CartItem cartItem : cart) {
                InventoryItem item = cartItem.getItem();
                int quantity = cartItem.getQuantity();
                double unitPrice = item.getPrice();
                int discountPercent = getDiscountPercentage(quantity);
                double itemTotal = quantity * unitPrice * (1 - discountPercent/100.0);
                orderSubtotal += itemTotal;

                // Write transaction entry with date and time
                writer.write(String.format("%s, %s, \"%s\", %.2f, %d, %.1f, $%.2f, %s, %s, %s\n",
                    transactionId,
                    item.getItemID(),
                    item.getDescription(),
                    unitPrice,
                    quantity,
                    discountPercent/100.0,
                    itemTotal,
                    transactionDate,
                    transactionYear,
                    transactionTime));

                // Add to invoice display
                invoice.append(String.format("%d. %s \"%s\" %s %d %d%% %s\n",
                    itemNumber++,
                    item.getItemID(),
                    item.getDescription(),
                    formatCurrency(unitPrice),
                    quantity,
                    discountPercent,
                    formatCurrency(itemTotal)));
            }
            // Add an extra newline to separate orders
            writer.write("\n");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                frame,
                "Error writing to transaction file: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }

        // Add totals to invoice
        double taxAmount = orderSubtotal * TAX_RATE;
        double orderTotal = orderSubtotal + taxAmount;

        invoice.append("\n\nOrder subtotal: ").append(formatCurrency(orderSubtotal)).append("\n\n");
        invoice.append("Tax rate: ").append(String.format("%.0f%%", TAX_RATE * 100)).append("\n\n");
        invoice.append("Tax amount: ").append(formatCurrency(taxAmount)).append("\n\n");
        invoice.append("ORDER TOTAL: ").append(formatCurrency(orderTotal)).append("\n\n");
        invoice.append("Thanks for shopping at Nile Dot Com!");

        // Show invoice
        JOptionPane.showMessageDialog(
            frame,
            invoice.toString(),
            "Nile Dot Com - FINAL INVOICE",
            JOptionPane.INFORMATION_MESSAGE
        );

        // Clear input fields and disable them with visual feedback
        itemIdField.setText("");
        quantityField.setText("");
        itemIdField.setEnabled(false);
        quantityField.setEnabled(false);
        
        // Set background color to darker gray for disabled fields
        itemIdField.setBackground(new Color(200, 200, 200));
        quantityField.setBackground(new Color(200, 200, 200));
        
        // Disable most buttons after checkout
        searchButton.setEnabled(false);
        addToCartButton.setEnabled(false);
        deleteLastItemButton.setEnabled(false);
        checkoutButton.setEnabled(false);
        
        // Keep these buttons enabled
        emptyCartButton.setEnabled(true);  // To start a new order
        exitButton.setEnabled(true);       // To exit the application
        
        // Update button colors to show disabled state
        searchButton.setBackground(new Color(200, 200, 200));
        addToCartButton.setBackground(new Color(200, 200, 200));
        deleteLastItemButton.setBackground(new Color(200, 200, 200));
        checkoutButton.setBackground(new Color(200, 200, 200));
        
        // Keep enabled buttons colored
        emptyCartButton.setBackground(new Color(70, 130, 180));
        exitButton.setBackground(new Color(180, 70, 70));
        
        searchSuccessful = false;
    }

    /**
     * Calculates the current subtotal of all items in the cart.
     * 
     * @return The sum of all item prices multiplied by their quantities
     */
    private double calculateSubtotal() {
        double subtotal = 0;
        for (CartItem item : cart) {
            subtotal += item.getItem().getPrice() * item.getQuantity();
        }
        return subtotal;
    }

    /**
     * Calculates any applicable discount based on the subtotal.
     * 
     * @param subtotal The current cart subtotal
     * @return The discount amount (10% for purchases over $100)
     */
    private double calculateDiscount(double subtotal) {
        // Simple discount rule: 10% off for purchases over $100
        return subtotal >= 100 ? subtotal * 0.10 : 0;
    }

    /**
     * Generates a formatted invoice string containing all order details.
     * 
     * @param subtotal The order subtotal
     * @param discount Any applicable discount
     * @param tax The calculated tax amount
     * @param total The final total including tax and discounts
     * @return A formatted string containing the complete invoice
     */
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

    /**
     * Logs the transaction details to the transactions.csv file.
     * 
     * @param subtotal The order subtotal
     * @param discount Any applied discount
     * @param tax The tax amount
     * @param total The final total
     */
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

    /**
     * Updates all input field labels to reflect the current item number.
     * Ensures labels stay synchronized with the current operation.
     */
    private void updateLabels() {
        // Update input field labels based on cart size + 1, but never exceed 5
        int nextItemNumber = Math.min(cart.size() + 1, 5);
        itemIdLabel.setText(String.format("Enter Item ID for Item #%d:", nextItemNumber));
        quantityLabel.setText(String.format("Enter Quantity for Item #%d:", nextItemNumber));
        searchButton.setText(String.format("Search for Item #%d", nextItemNumber));
        addToCartButton.setText(String.format("Add Item #%d to Cart", nextItemNumber));
        
        // Details label shows current item being searched
        detailsLabel.setText(String.format("Details for Item #%d:", currentItemNumber));
    }

    /**
     * Updates the subtotal label with the current cart total.
     * Includes the item count and formatted currency amount.
     */
    private void updateSubtotalLabel() {
        double subtotal = 0.0;
        for (CartItem item : cart) {
            double unitPrice = item.getItem().getPrice();
            int quantity = item.getQuantity();
            int discountPercent = getDiscountPercentage(quantity);
            double itemTotal = quantity * unitPrice * (1 - discountPercent/100.0);
            subtotal += itemTotal;
        }
        subtotalLabel.setText(String.format("Current Subtotal for %d Item(s): %s", 
            cart.size(), formatCurrency(subtotal)));
    }

    /**
     * Handles the empty cart operation.
     * - Confirms with user before proceeding
     * - Clears all cart items
     * - Resets UI elements to initial state
     * - Re-enables input fields and buttons
     */
    private void handleEmptyCart() {
        if (!cart.isEmpty() || !itemIdField.isEnabled()) {
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
                
                // Reset input field backgrounds to white
                itemIdField.setBackground(Color.WHITE);
                quantityField.setBackground(Color.WHITE);
                
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

    /**
     * Handles the deletion of the last item added to the cart.
     * - Removes the most recently added item
     * - Returns the quantity to inventory
     * - Updates all displays and labels
     * - Updates button states
     */
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
            
            // Update currentItemNumber to match cart size + 1
            currentItemNumber = cart.size() + 1;
            updateLabels();
            
            searchSuccessful = false;
            updateButtonStates();
        }
    }

    /**
     * Checks if the current input fields contain valid data.
     * 
     * @return true if both item ID and quantity are valid, false otherwise
     */
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

    /**
     * Updates the enabled/disabled states of all buttons based on:
     * - Current cart state
     * - Search status
     * - Input field validity
     * Also updates button appearances to reflect their states.
     */
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
    }

    /**
     * Calculates the discount percentage based on quantity ordered.
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
     * Formats a number as a currency string with $ and 2 decimal places.
     * 
     * @param amount The amount to format
     * @return A formatted currency string (e.g., "$10.99")
     */
    private String formatCurrency(double amount) {
        return String.format("$%.2f", amount);
    }
}
