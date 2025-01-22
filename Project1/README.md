# Nile Dot Com - E-Store Simulation
**Course:** CNT 4714 - Spring 2025  
**Project:** Enterprise Computing - Project 1  
**Author:** Colin Kirby  
**Date:** January 2025

## Project Description
An event-driven enterprise simulation of an e-commerce store (Nile Dot Com) built using Java Swing. The application allows users to:
- Search for items in inventory
- Add items to a shopping cart
- Process orders with automatic discounts
- Generate invoices
- Log transactions

## Project Structure

### Source Files
- `Main.java` - Entry point of the application.
- `InventoryGUI.java` - Main GUI implementation with shopping cart functionality.
- `InventoryItem.java` - Class representing individual inventory items.
- `CartItem.java` - Class representing items in the shopping cart.
- `InventoryLoader.java` - Handles loading inventory data from CSV file.

### Data Files
- `inventory.csv` - Contains the store's inventory data.
- `transactions.csv` - Log file for all completed transactions.

### Documentation
- `screenshots/` - Directory containing application screenshots.
- `progress_photos.pdf` - Development progress documentation.

## How to Run

1. Ensure you have Java JDK 8 or higher installed
2. Compile the source files:
   ```bash
    javac *.java
   ```
3. Run the application:
   ```bash
   java Main
   ```

## Features

### Shopping Cart
- Maximum 5 items per order
- Automatic quantity-based discounts:
  - 20% off for 15+ items
  - 15% off for 10-14 items
  - 10% off for 5-9 items
  - No discount for less than 5 items
- 6% sales tax applied to all orders

### User Interface
- Item search functionality
- Real-time cart updates
- Dynamic subtotal calculation
- Order confirmation with detailed invoice
- Transaction logging

## File Descriptions

### Source Files
- `Main.java`: Application entry point that initializes the inventory system and launches the GUI
- `InventoryGUI.java`: Main GUI class implementing the shopping interface and cart management
- `InventoryItem.java`: Data class for inventory items with properties like ID, description, price, etc.
- `CartItem.java`: Shopping cart item class with quantity tracking and discount calculations
- `InventoryLoader.java`: Utility class for reading inventory data from CSV files

### Data Files
- `inventory.csv`: CSV file containing item data in format:
  ```
  ItemID, "Description", InStock, Quantity, Price
  ```
- `transactions.csv`: Transaction log file recording all completed orders

## Usage Notes

1. **Starting a New Order**
   - Enter Item ID and quantity
   - Click "Search" to view item details
   - Click "Add to Cart" if details are correct

2. **Managing the Cart**
   - View current items in cart
   - Delete last added item if needed
   - Empty cart to start over
   - Maximum 5 items per order

3. **Checkout Process**
   - Review cart contents
   - Click "Check Out" to process order
   - Review invoice with discounts and tax
   - Transaction automatically logged

4. **Error Handling**
   - Invalid item IDs
   - Out-of-stock items
   - Quantity exceeding available stock
   - Cart capacity limits 