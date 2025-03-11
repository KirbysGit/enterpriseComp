import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DatabaseGUI extends JFrame {
    // Define properties folder path and status messages
    private static final String PROPERTIES_FOLDER = "properties/";
    private static final String STATUS_NO_CONNECTION = "NO CONNECTION ESTABLISHED";
    private static final String STATUS_INVALID_CREDENTIALS = "NOT CONNECTED - User Credentials Do Not Match Properties File!";
    private static final String STATUS_CONNECTED = "CONNECTED TO: %s";
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> dbPropertiesDropdown;
    private JComboBox<String> userPropertiesDropdown;
    private JButton connectButton;
    private JButton disconnectButton;
    private JLabel statusLabel;
    
    // New components for query execution
    private JTextArea queryArea;
    private JButton executeButton;
    private JButton clearButton;
    private JButton clearCommandButton;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private Connection connection;
    private JPanel queryPanel;
    
    public DatabaseGUI() {
        setTitle("SQL Client App - CNT 4714 Project 3");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Create login panel
        JPanel loginPanel = new JPanel(new GridLayout(7, 2, 5, 5));  // Increased rows for new dropdowns
        loginPanel.setBorder(BorderFactory.createTitledBorder("Connection Details"));

        // UI Components for login
        loginPanel.add(new JLabel("DB Properties File:"));
        String[] dbProps = {"project3.properties", "bikedb.properties"};
        dbPropertiesDropdown = new JComboBox<>(dbProps);
        loginPanel.add(dbPropertiesDropdown);

        loginPanel.add(new JLabel("User Properties File:"));
        String[] userProps = {"root.properties", "client1.properties", "client2.properties"};
        userPropertiesDropdown = new JComboBox<>(userProps);
        loginPanel.add(userPropertiesDropdown);

        loginPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        usernameField.setEditable(true);  // Allow user input
        loginPanel.add(usernameField);

        loginPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        passwordField.setEditable(true);  // Allow user input
        loginPanel.add(passwordField);

        connectButton = new JButton("Connect to Database");
        loginPanel.add(connectButton);

        disconnectButton = new JButton("Disconnect From Database");
        disconnectButton.setEnabled(false);
        loginPanel.add(disconnectButton);

        statusLabel = new JLabel(STATUS_NO_CONNECTION);
        statusLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        loginPanel.add(statusLabel);

        // Create query panel
        queryPanel = new JPanel(new BorderLayout(5, 5));
        queryPanel.setBorder(BorderFactory.createTitledBorder("SQL Command"));
        
        queryArea = new JTextArea(5, 40);
        queryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        queryArea.setEnabled(false);
        queryPanel.add(new JScrollPane(queryArea), BorderLayout.CENTER);
        
        // Create SQL command button panel
        JPanel sqlButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        executeButton = new JButton("Execute SQL Command");
        executeButton.setEnabled(false);
        clearCommandButton = new JButton("Clear SQL Command");
        clearCommandButton.setEnabled(false);
        
        sqlButtonPanel.add(executeButton);
        sqlButtonPanel.add(clearCommandButton);
        queryPanel.add(sqlButtonPanel, BorderLayout.SOUTH);

        // Create results panel with JTable
        JPanel resultsPanel = new JPanel(new BorderLayout(5, 5));
        resultsPanel.setBorder(BorderFactory.createTitledBorder("SQL Execution Result"));
        
        // Initialize table with no data
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        resultTable = new JTable(tableModel);
        resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // Changed to stretch columns
        resultTable.getTableHeader().setReorderingAllowed(false);
        resultTable.setFillsViewportHeight(true); // Make table fill the viewport
        
        // Add table to scroll pane with adjusted size
        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setPreferredSize(new Dimension(980, 400));
        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        // Create results button panel
        JPanel resultsButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clearButton = new JButton("Clear Results Window");
        clearButton.setEnabled(false);
        JButton closeButton = new JButton("Close Application");
        
        resultsButtonPanel.add(clearButton);
        resultsButtonPanel.add(closeButton);
        resultsPanel.add(resultsButtonPanel, BorderLayout.SOUTH);

        // Add panels to frame
        add(loginPanel, BorderLayout.NORTH);
        add(queryPanel, BorderLayout.CENTER);
        add(resultsPanel, BorderLayout.SOUTH);

        // Connect Button Action
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

        // Execute Button Action
        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeQuery();
            }
        });

        // Clear Results Button Action
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearResults();
            }
        });

        // Clear SQL Command Button Action
        clearCommandButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                queryArea.setText("");
            }
        });

        // Close Application Button Action
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeDatabaseConnection();
                System.exit(0);
            }
        });

        // Window Closing Event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeDatabaseConnection();
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void clearResults() {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
    }

    private void closeDatabaseConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean connectToDatabase(String dbPropsFile, String userPropsFile) {
        try {
            closeDatabaseConnection();

            // Load database properties
            Properties dbProps = new Properties();
            FileInputStream dbInput = new FileInputStream(PROPERTIES_FOLDER + dbPropsFile);
            dbProps.load(dbInput);
            dbInput.close();

            // Load user properties
            Properties userProps = new Properties();
            FileInputStream userInput = new FileInputStream(PROPERTIES_FOLDER + userPropsFile);
            userProps.load(userInput);
            userInput.close();

            // Get connection details from properties
            String url = dbProps.getProperty("db.url");
            String driver = dbProps.getProperty("db.driver");
            String propUsername = userProps.getProperty("db.username");
            String propPassword = userProps.getProperty("db.password");

            // Get user input
            String inputUsername = usernameField.getText();
            String inputPassword = new String(passwordField.getPassword());

            // Validate credentials against properties file
            if (!inputUsername.equals(propUsername) || !inputPassword.equals(propPassword)) {
                statusLabel.setText(STATUS_INVALID_CREDENTIALS);
                return false;
            }

            // Load driver and establish connection
            Class.forName(driver);
            connection = DriverManager.getConnection(url, inputUsername, inputPassword);
            
            // Update status with actual connection URL
            statusLabel.setText(String.format(STATUS_CONNECTED, url));
            
            return true;
        } catch (IOException | ClassNotFoundException | SQLException e) {
            statusLabel.setText(STATUS_NO_CONNECTION);
            JOptionPane.showMessageDialog(this, 
                "Database Error: " + e.getMessage(), 
                "Connection Failed", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    private void executeQuery() {
        if (connection == null) {
            JOptionPane.showMessageDialog(this, 
                "No database connection established.", 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = queryArea.getText().trim();
        if (sql.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter an SQL command.", 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Statement stmt = connection.createStatement();
            boolean isQuery = stmt.execute(sql);

            if (isQuery) {
                // Handle SELECT queries
                ResultSet rs = stmt.getResultSet();
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                clearResults();

                // Add column names
                for (int i = 1; i <= columnCount; i++) {
                    tableModel.addColumn(metaData.getColumnName(i));
                }

                // Add row data
                while (rs.next()) {
                    Object[] rowData = new Object[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        rowData[i - 1] = rs.getObject(i);
                    }
                    tableModel.addRow(rowData);
                }

                // Auto-adjust column widths
                for (int i = 0; i < columnCount; i++) {
                    int maxWidth = getColumnPreferredWidth(resultTable, i);
                    resultTable.getColumnModel().getColumn(i).setPreferredWidth(maxWidth);
                }

                rs.close();
                
                JOptionPane.showMessageDialog(this, 
                    "Query executed successfully.", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Handle INSERT, UPDATE, DELETE queries
                int updateCount = stmt.getUpdateCount();
                clearResults();
                
                JOptionPane.showMessageDialog(this, 
                    String.format("Successful Update... %d rows updated.", updateCount),
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            }

            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getColumnPreferredWidth(JTable table, int columnIndex) {
        int maxWidth = 0;
        TableColumn column = table.getColumnModel().getColumn(columnIndex);
        
        // Check header width
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

    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(DatabaseGUI::new);
    }
}
