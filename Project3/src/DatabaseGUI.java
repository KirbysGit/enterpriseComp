/*
    Name: Colin Kirby
    Course: CNT 4714 Spring 2025
    Assignment title: Project 3 – A Two-tier Client-Server Application
    Date: March 11, 2025

    Class: DatabaseGUI
*/

// Imports.
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

// Main class for the DatabaseGUI.
public class DatabaseGUI extends JFrame {
    
    // Define Proper Path & Status Messages.
    private static final String PROPERTIES_FOLDER = "properties/";
    private static final String STATUS_NO_CONNECTION = "NO CONNECTION ESTABLISHED";
    private static final String STATUS_INVALID_CREDENTIALS = "NOT CONNECTED - User Credentials Do Not Match Properties File!";
    private static final String STATUS_CONNECTED = "CONNECTED TO: %s";
    
    // Define Fields.
    private String currentConnectionUrl;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> dbPropertiesDropdown;
    private JComboBox<String> userPropertiesDropdown;
    private JButton connectButton;
    private JButton disconnectButton;
    private JLabel statusLabel;
    
    // Define Query Execution Components.
    private JTextArea queryArea;
    private JButton executeButton;
    private JButton clearButton;
    private JButton clearCommandButton;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private Connection connection;
    private JPanel queryPanel;
    
    // Define Logging Connection.
    private Connection loggingConnection;

    // Define Color Constants.
    private static final Color HEADER_COLOR = new Color(51, 102, 153);
    private static final Color SUCCESS_COLOR = new Color(46, 125, 50);
    private static final Color ERROR_COLOR = new Color(198, 40, 40);
    private static final Color BUTTON_TEXT = new Color(33, 37, 41);  // Dark gray text
    private static final Color BUTTON_BORDER = new Color(33, 37, 41);  // Dark border
    private static final Color CONNECT_BG = new Color(200, 230, 201);  // Light green
    private static final Color DISCONNECT_BG = new Color(255, 205, 210);  // Light red
    private static final Color EXECUTE_BG = new Color(179, 229, 252);  // Light blue
    private static final Color CLEAR_BG = new Color(224, 224, 224);  // Light gray
    private static final Color CLOSE_BG = new Color(255, 205, 210);  // Light red
    private static final Color PANEL_BACKGROUND = new Color(245, 245, 245);
    private static final Color TABLE_HEADER_BG = new Color(230, 235, 240);
    private static final Color TABLE_HEADER_FG = new Color(33, 37, 41);  // Dark gray text
    private static final Color TABLE_GRID_COLOR = new Color(180, 180, 180);
    private static final Color DISABLED_BG = new Color(220, 220, 220);
    private static final Color DISABLED_FG = new Color(150, 150, 150);

    // Define Constructor.
    public DatabaseGUI() {
        // Set Title, Size, Minimum Size, Default Close Operation, and Layout.
        setTitle("SQL CLIENT APPLICATION - CNT 4714 Project 3");
        setSize(1000, 850);
        setMinimumSize(new Dimension(800, 800));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 5));
        getContentPane().setBackground(PANEL_BACKGROUND);

        // Create Main Content Panel.
        JPanel mainContent = new JPanel(new BorderLayout(10, 3));
        mainContent.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        mainContent.setBackground(PANEL_BACKGROUND);

        // Create Login Panel.
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(HEADER_COLOR, 2), 
            "Connection Details"),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        loginPanel.setBackground(PANEL_BACKGROUND);
        loginPanel.setPreferredSize(new Dimension(1000, 250));

        // Setup GridBagConstraints with Reduced Spacing.
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;

        // Style Labels.
        JLabel[] labels = {
            new JLabel("DB Properties File:"),
            new JLabel("User Properties File:"),
            new JLabel("Username:"),
            new JLabel("Password:")
        };
        for (JLabel label : labels) {
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setForeground(HEADER_COLOR);
        }

        // Add Components with Proper Constraints.
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        loginPanel.add(labels[0], gbc);

        // Add Database Properties Dropdown.
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        dbPropertiesDropdown = new JComboBox<>(new String[]{"project3.properties", "bikedb.properties"});
        styleComboBox(dbPropertiesDropdown);
        loginPanel.add(dbPropertiesDropdown, gbc);

        // Add User Properties Dropdown.
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        loginPanel.add(labels[1], gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        userPropertiesDropdown = new JComboBox<>(new String[]{"root.properties", "client1.properties", "client2.properties"});
        styleComboBox(userPropertiesDropdown);
        loginPanel.add(userPropertiesDropdown, gbc);

        // Add Username Field.
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.2;
        loginPanel.add(labels[2], gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        usernameField = new JTextField();
        styleTextField(usernameField);
        loginPanel.add(usernameField, gbc);

        // Add Password Field.
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.2;
        loginPanel.add(labels[3], gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        passwordField = new JPasswordField();
        styleTextField(passwordField);
        loginPanel.add(passwordField, gbc);

        // Create Button Panel.
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(PANEL_BACKGROUND);
        
        // Style Buttons.
        connectButton = createStyledButton("Connect to Database", CONNECT_BG);
        disconnectButton = createStyledButton("Disconnect From Database", DISCONNECT_BG);
        connectButton.setPreferredSize(new Dimension(160, 30));
        disconnectButton.setPreferredSize(new Dimension(160, 30));
        disconnectButton.setEnabled(false);

        buttonPanel.add(connectButton);
        buttonPanel.add(disconnectButton);

        // Add Button Panel.
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(buttonPanel, gbc);

        // Add Status Label.
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.anchor = GridBagConstraints.WEST;
        statusLabel = new JLabel(STATUS_NO_CONNECTION);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setForeground(ERROR_COLOR);
        loginPanel.add(statusLabel, gbc);

        // Style Query Panel.
        queryPanel = new JPanel(new BorderLayout(5, 10)); // Reduced vertical gap
        queryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(HEADER_COLOR, 2), 
            "SQL Command"),
            BorderFactory.createEmptyBorder(5, 10, 5, 10) // Reduced padding
        ));
        queryPanel.setBackground(PANEL_BACKGROUND);
        
        // Style Query Area.
        queryArea = new JTextArea();
        queryArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        queryArea.setEnabled(false);
        queryArea.setMargin(new Insets(5, 10, 5, 10)); // Reduced padding
        queryArea.setBackground(new Color(250, 250, 250));
        queryArea.setLineWrap(true);
        queryArea.setWrapStyleWord(true);
        queryArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(HEADER_COLOR),
            BorderFactory.createEmptyBorder(5, 5, 5, 5) // Reduced padding
        ));
        
        JScrollPane queryScroll = new JScrollPane(queryArea);
        queryScroll.setPreferredSize(new Dimension(980, 80)); // Reduced height
        queryScroll.setMinimumSize(new Dimension(980, 80));
        queryPanel.add(queryScroll, BorderLayout.CENTER);

        // Style SQL Command Buttons.
        JPanel sqlButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5)); // Reduced horizontal gap
        sqlButtonPanel.setBackground(PANEL_BACKGROUND);
        executeButton = createStyledButton("Execute SQL Command", EXECUTE_BG);
        clearCommandButton = createStyledButton("Clear SQL Command", CLEAR_BG);
        executeButton.setPreferredSize(new Dimension(160, 30));
        clearCommandButton.setPreferredSize(new Dimension(160, 30));
        executeButton.setEnabled(false);
        clearCommandButton.setEnabled(false);

        sqlButtonPanel.add(executeButton);
        sqlButtonPanel.add(clearCommandButton);
        queryPanel.add(sqlButtonPanel, BorderLayout.SOUTH);

        // Style Results Panel.
        JPanel resultsPanel = new JPanel(new BorderLayout(5, 10)); // Reduced vertical gap
        resultsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(HEADER_COLOR, 2), 
            "SQL Execution Result"),
            BorderFactory.createEmptyBorder(5, 10, 5, 10) // Reduced padding
        ));
        resultsPanel.setBackground(PANEL_BACKGROUND);

        // Style Table.
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultTable = new JTable(tableModel);
        styleTable();

        // Add Table Scroll.
        JScrollPane tableScroll = new JScrollPane(resultTable);
        tableScroll.setPreferredSize(new Dimension(980, 200)); // Reduced height
        tableScroll.setMinimumSize(new Dimension(980, 200));
        resultsPanel.add(tableScroll, BorderLayout.CENTER);

        // Style Results Buttons.
        JPanel resultsButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5)); // Reduced horizontal gap
        resultsButtonPanel.setBackground(PANEL_BACKGROUND);
        clearButton = createStyledButton("Clear Results Window", CLEAR_BG);
        JButton closeButton = createStyledButton("Close Application", CLOSE_BG);
        clearButton.setPreferredSize(new Dimension(160, 30));
        closeButton.setPreferredSize(new Dimension(160, 30));
        clearButton.setEnabled(false);

        resultsButtonPanel.add(clearButton);
        resultsButtonPanel.add(closeButton);
        resultsPanel.add(resultsButtonPanel, BorderLayout.SOUTH);

        // Add Panels to Main Content.
        mainContent.add(loginPanel, BorderLayout.NORTH);
        mainContent.add(queryPanel, BorderLayout.CENTER);
        mainContent.add(resultsPanel, BorderLayout.SOUTH);

        // Add Main Content to Frame.
        add(mainContent, BorderLayout.CENTER);

        // Add Hover Effects & Visual Feedback.
        addTextFieldFocusEffects();

        // Add Document Listener to Query Area to Clear Error Messages.
        queryArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                clearErrorMessage();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                clearErrorMessage();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                clearErrorMessage();
            }
        });

        // Connect Button Action.
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dbPropsFile = (String) dbPropertiesDropdown.getSelectedItem();
                String userPropsFile = (String) userPropertiesDropdown.getSelectedItem();

                if (connectToDatabase(dbPropsFile, userPropsFile)) {
                    queryArea.setEnabled(true);
                    executeButton.setEnabled(true);
                    clearButton.setEnabled(true);
                    clearCommandButton.setEnabled(true);
                    disconnectButton.setEnabled(true);
                    connectButton.setEnabled(false);
                    clearResults();
                }
            }
        });

        // Disconnect Button Action
        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeDatabaseConnection();
                statusLabel.setText(STATUS_NO_CONNECTION);
                queryArea.setEnabled(false);
                executeButton.setEnabled(false);
                clearButton.setEnabled(false);
                clearCommandButton.setEnabled(false);
                disconnectButton.setEnabled(false);
                connectButton.setEnabled(true);
                queryArea.setText("");
                clearResults();
            }
        });

        // Execute Button Action.
        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeQuery();
            }
        });

        // Clear Results Button Action.
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearResults();
            }
        });

        // Clear SQL Command Button Action.
        clearCommandButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                queryArea.setText("");
            }
        });

        // Close Application Button Action.
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeDatabaseConnection();
                System.exit(0);
            }
        });

        // Window Closing Event.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeDatabaseConnection();
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Clear Results.
    private void clearResults() {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
    }

    // Connect to Database.
    private boolean connectToDatabase(String dbPropsFile, String userPropsFile) {
        try {
            // Ensure Previous Connections are Properly Closed.
            closeDatabaseConnection();

            // Load Database Properties.
            Properties dbProps = new Properties();
            try (FileInputStream dbInput = new FileInputStream(PROPERTIES_FOLDER + dbPropsFile)) {
                dbProps.load(dbInput);
            }

            // Load User Properties.
            Properties userProps = new Properties();
            try (FileInputStream userInput = new FileInputStream(PROPERTIES_FOLDER + userPropsFile)) {
                userProps.load(userInput);
            }

            // Get Connection Details from Properties.
            String url = dbProps.getProperty("db.url");
            String driver = dbProps.getProperty("db.driver");
            String propUsername = userProps.getProperty("db.username");
            String propPassword = userProps.getProperty("db.password");

            // Get User Input.
            String inputUsername = usernameField.getText().trim();
            String inputPassword = new String(passwordField.getPassword());

            // Validate Credentials Against Properties File.
            if (!inputUsername.equals(propUsername) || !inputPassword.equals(propPassword)) {
                showErrorDialog(STATUS_INVALID_CREDENTIALS);
                statusLabel.setText(STATUS_NO_CONNECTION);
                return false;
            }

            // Load Driver and Establish Main Connection.
            Class.forName(driver);
            connection = DriverManager.getConnection(url, inputUsername, inputPassword);
            currentConnectionUrl = url; // Store the URL when connecting
            statusLabel.setText(String.format(STATUS_CONNECTED, url));

            // Only Establish Logging Connection for Non-Accountant Users.
            if (!userPropsFile.equals("theaccountant.properties")) {
                try {
                    Properties logProps = new Properties();
                    try (FileInputStream logInput = new FileInputStream(PROPERTIES_FOLDER + "project3app.properties")) {
                        logProps.load(logInput);
                    }

                    String logUrl = logProps.getProperty("db.url");
                    String logUsername = logProps.getProperty("db.username");
                    String logPassword = logProps.getProperty("db.password");

                    // Establish Logging Connection with project3app credentials.
                    loggingConnection = DriverManager.getConnection(logUrl, logUsername, logPassword);
                } catch (Exception e) {
                    System.err.println("Warning: Could not establish logging connection: " + e.getMessage());
                    // Don't Fail the Main Connection if Logging Connection Fails.
                }
            }
            
            return true;
        } catch (IOException e) {
            showErrorDialog("Error reading properties files: " + e.getMessage());
            statusLabel.setText(STATUS_NO_CONNECTION);
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            showErrorDialog("Database driver not found: " + e.getMessage());
            statusLabel.setText(STATUS_NO_CONNECTION);
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            showSQLErrorDialog(e);
            statusLabel.setText(STATUS_NO_CONNECTION);
            e.printStackTrace();
            return false;
        }
    }

    // Execute Query.
    private void executeQuery() {
        // Check if Database Connection is Established.
        if (connection == null) {
            showErrorDialog("No database connection established.");
            return;
        }

        // Get SQL Command from Query Area.
        String sql = queryArea.getText().trim();
        if (sql.isEmpty()) {
            showErrorDialog("Please enter an SQL command.");
            return;
        }

        // Count number of parameters (?)
        int paramCount = countParameters(sql);
        Object[] parameters = new Object[paramCount];

        // If there are parameters, prompt for values
        if (paramCount > 0) {
            for (int i = 0; i < paramCount; i++) {
                String value = JOptionPane.showInputDialog(this,
                    String.format("Enter value for parameter %d:", i + 1),
                    "Parameter Input",
                    JOptionPane.QUESTION_MESSAGE);
                
                if (value == null) {
                    // User cancelled input
                    return;
                }
                parameters[i] = value;
            }
        }

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Set parameters if any exist
            for (int i = 0; i < paramCount; i++) {
                pstmt.setObject(i + 1, parameters[i]);
            }

            boolean isQuery = sql.toUpperCase().trim().startsWith("SELECT");
            
            if (isQuery) {
                // Handle SELECT Queries.
                try (ResultSet rs = pstmt.executeQuery()) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    // Clear Results.
                    clearResults();

                    // Add Column Names.
                    for (int i = 1; i <= columnCount; i++) {
                        tableModel.addColumn(metaData.getColumnName(i));
                    }

                    // Add Row Data.
                    while (rs.next()) {
                        Object[] rowData = new Object[columnCount];
                        for (int i = 1; i <= columnCount; i++) {
                            rowData[i - 1] = rs.getObject(i);
                        }
                        tableModel.addRow(rowData);
                    }
                }

                // Log Successful SELECT Query.
                logOperation("SELECT");
                showSuccessDialog("Query executed successfully.");

            } else {
                // Handle UPDATE, INSERT, DELETE Queries.
                int updateCount = pstmt.executeUpdate();
                clearResults();

                // Log Successful DML Operation.
                logOperation("UPDATE");
                showSuccessDialog(String.format("Successful Update... %d rows updated.", updateCount));
            }

        } catch (SQLException e) {
            showSQLErrorDialog(e);
            clearResults();
        }
    }

    // Helper method to count parameters in SQL query
    private int countParameters(String sql) {
        int count = 0;
        boolean inString = false;
        char[] chars = sql.toCharArray();
        
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '\'') {
                inString = !inString;
            } else if (chars[i] == '?' && !inString) {
                count++;
            }
        }
        return count;
    }

    // Show Error Dialog.
    private void showErrorDialog(String message) {
        UIManager.put("OptionPane.messageForeground", ERROR_COLOR);
        JOptionPane.showMessageDialog(
            this,
            message,
            "Database Error",
            JOptionPane.ERROR_MESSAGE
        );
        UIManager.put("OptionPane.messageForeground", null);
    }

    // Show Success Dialog.
    private void showSuccessDialog(String message) {
        UIManager.put("OptionPane.messageForeground", SUCCESS_COLOR);
        JOptionPane.showMessageDialog(
            this,
            message,
            "Success",
            JOptionPane.INFORMATION_MESSAGE
        );
        UIManager.put("OptionPane.messageForeground", null);
    }

    // Show SQL Error Dialog.
    private void showSQLErrorDialog(SQLException e) {
        // Extract Error Message.
        String errorMessage = e.getMessage();
        String formattedMessage;

        // Customize Error Messages for Better Readability. (For sake of my debugging)
        if (errorMessage.toLowerCase().contains("access denied") || 
            errorMessage.toLowerCase().contains("permission")) {
            formattedMessage = "Permission Denied: You do not have sufficient privileges for this operation.";
        } else if (errorMessage.toLowerCase().contains("syntax")) {
            formattedMessage = "SQL Syntax Error: Please check your SQL command.";
        } else if (errorMessage.toLowerCase().contains("doesn't exist") || 
                   errorMessage.toLowerCase().contains("not found")) {
            formattedMessage = "Table or Database Not Found: The specified table or database does not exist.";
        } else if (errorMessage.toLowerCase().contains("foreign key")) {
            formattedMessage = "Foreign Key Violation: The referenced key does not exist.";
        } else if (errorMessage.toLowerCase().contains("duplicate")) {
            formattedMessage = "Duplicate Entry: This value already exists in the database.";
        } else if (errorMessage.toLowerCase().contains("constraint")) {
            formattedMessage = "Constraint Violation: The operation violates a database constraint.";
        } else {
            formattedMessage = "Database Error: " + errorMessage;
        }

        UIManager.put("OptionPane.messageForeground", ERROR_COLOR);
        JOptionPane.showMessageDialog(
            this,
            formattedMessage,
            "Database Error",
            JOptionPane.ERROR_MESSAGE
        );
        UIManager.put("OptionPane.messageForeground", null);
    }

    // Log Operation.
    private void logOperation(String operationType) {
        // Check if Logging Connection is Established.
        if (loggingConnection == null) return;

        try {
            String username = usernameField.getText().trim();

            // Use Transaction to Ensure Atomic Operation.
            loggingConnection.setAutoCommit(false);
            try {
                // Check if user exists.
                String checkQuery = "SELECT COUNT(*) FROM operationscount WHERE login_username = ?";
                try (PreparedStatement checkStmt = loggingConnection.prepareStatement(checkQuery)) {
                    checkStmt.setString(1, username);
                    ResultSet rs = checkStmt.executeQuery();
                    rs.next();
                    boolean exists = rs.getInt(1) > 0;
                    rs.close();

                    if (exists) {
                        // Update Existing Row.
                        String updateQuery = operationType.equals("SELECT") 
                            ? "UPDATE operationscount SET num_queries = num_queries + 1 WHERE login_username = ?"
                            : "UPDATE operationscount SET num_updates = num_updates + 1 WHERE login_username = ?";
                        
                        try (PreparedStatement updateStmt = loggingConnection.prepareStatement(updateQuery)) {
                            updateStmt.setString(1, username);
                            updateStmt.executeUpdate();
                        }
                    } else {
                        // Insert New Row.
                        String insertQuery = "INSERT INTO operationscount (login_username, num_queries, num_updates) VALUES (?, ?, ?)";
                        try (PreparedStatement insertStmt = loggingConnection.prepareStatement(insertQuery)) {
                            insertStmt.setString(1, username);
                            insertStmt.setInt(2, operationType.equals("SELECT") ? 1 : 0);
                            insertStmt.setInt(3, operationType.equals("SELECT") ? 0 : 1);
                            insertStmt.executeUpdate();
                        }
                    }
                }
                loggingConnection.commit();
            } catch (SQLException e) {
                loggingConnection.rollback();
                throw e;
            } finally {
                loggingConnection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error logging operation: " + e.getMessage());
            e.printStackTrace();
            // Don't Show Errors to User as Logging Should be Silent.
        }
    }

    // Close Database Connection.
    private void closeDatabaseConnection() {
        try {
            if (connection != null) {
                connection.close();
                connection = null;
                System.out.println("Main database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing main connection: " + e.getMessage());
        }

        try {
            if (loggingConnection != null) {
                loggingConnection.close();
                loggingConnection = null;
                System.out.println("Logging connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing logging connection: " + e.getMessage());
        }
    }

    // Get Column Preferred Width.
    private int getColumnPreferredWidth(JTable table, int columnIndex) {
        int maxWidth = 0;
        TableColumn column = table.getColumnModel().getColumn(columnIndex);
        
        // Check Header Width.
        TableCellRenderer headerRenderer = column.getHeaderRenderer();
        if (headerRenderer == null) {
            headerRenderer = table.getTableHeader().getDefaultRenderer();
        }
        Object headerValue = column.getHeaderValue();
        Component headerComp = headerRenderer.getTableCellRendererComponent(table, headerValue, false, false, 0, columnIndex);
        maxWidth = headerComp.getPreferredSize().width;

        // Check data width
        for (int row = 0; row < table.getRowCount(); row++) {
            TableCellRenderer renderer = table.getCellRenderer(row, columnIndex);
            Component comp = table.prepareRenderer(renderer, row, columnIndex);
            maxWidth = Math.max(maxWidth, comp.getPreferredSize().width);
        }

        return maxWidth + 20; // Add padding
    }

    // Update Button Style.
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setForeground(BUTTON_TEXT);  // Dark text
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BUTTON_BORDER, 1),
            BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(160, 30));
        
        // Add Modern Hover Effect.
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(bgColor.darker());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(bgColor);
                }
            }
        });

        // Add Property Change Listener for Enabled State.
        button.addPropertyChangeListener("enabled", evt -> {
            boolean enabled = (Boolean) evt.getNewValue();
            if (!enabled) {
                button.setBackground(DISABLED_BG);
                button.setForeground(DISABLED_FG);
            } else {
                button.setBackground(bgColor);
                button.setForeground(BUTTON_TEXT);
            }
        });
        
        return button;
    }

    // Update Text Field Style.
    private void styleTextField(JTextField textField) {
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setMargin(new Insets(5, 10, 5, 10));
        textField.setPreferredSize(new Dimension(300, 35));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        textField.setBackground(new Color(250, 250, 250));
    }

    // Update Combo Box Style.
    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(Color.WHITE);
        comboBox.setPreferredSize(new Dimension(300, 35));
        comboBox.setMaximumRowCount(5);
        
        // Style the Renderer for Consistent Appearance.
        DefaultListCellRenderer renderer = new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return this;
            }
        };
        renderer.setOpaque(true);
        comboBox.setRenderer(renderer);
        
        // Enhanced Border with More Padding.
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    // Add Focus Effects to Text Fields.
    private void addTextFieldFocusEffects() {
        Component[] components = {usernameField, passwordField, queryArea};
        
        for (Component comp : components) {
            if (comp instanceof JTextField || comp instanceof JTextArea) {
                JComponent textComp = (JComponent) comp;
                
                textComp.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        textComp.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(HEADER_COLOR),
                            BorderFactory.createEmptyBorder(2, 5, 2, 5)
                        ));
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        textComp.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(158, 158, 158)),
                            BorderFactory.createEmptyBorder(2, 5, 2, 5)
                        ));
                    }
                });
            }
        }
    }

    // Update Table Styling.
    private void styleTable() {
        resultTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resultTable.setRowHeight(25);
        resultTable.setGridColor(TABLE_GRID_COLOR);
        resultTable.setShowGrid(true);
        resultTable.setShowHorizontalLines(true);
        resultTable.setShowVerticalLines(true);
        resultTable.setIntercellSpacing(new Dimension(1, 1));
        resultTable.setSelectionBackground(new Color(232, 240, 254));
        resultTable.setSelectionForeground(Color.BLACK);
        resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Enhanced Header Styling.
        JTableHeader header = resultTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TABLE_HEADER_FG);
        header.setBorder(BorderFactory.createLineBorder(TABLE_HEADER_BG.darker()));
        header.setReorderingAllowed(false);
        
        // Make Header Style Persistent.
        ((DefaultTableCellRenderer)header.getDefaultRenderer())
            .setHorizontalAlignment(SwingConstants.LEFT);
        ((DefaultTableCellRenderer)header.getDefaultRenderer())
            .setBackground(TABLE_HEADER_BG);
        ((DefaultTableCellRenderer)header.getDefaultRenderer())
            .setForeground(TABLE_HEADER_FG);
    }

    // Clear Error Message.
    private void clearErrorMessage() {
        if (connection != null) {
            statusLabel.setText(String.format(STATUS_CONNECTED, currentConnectionUrl));
        } else {
            statusLabel.setText(STATUS_NO_CONNECTION);
        }
    }

    // Main Method.
    public static void main(String[] args) {
        try {
            // Set System Look and Feel.
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(DatabaseGUI::new);
    }
}
