import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCConnectionTest {
    public static void main(String[] args) {
        // Change this to test with different users
        String propertiesFile = "database.properties"; // Can be "client1.properties" to test client1
        
        try {
            Properties props = new Properties();
            FileInputStream input = new FileInputStream(propertiesFile);
            props.load(input);
            input.close();

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.username");
            String password = props.getProperty("db.password");
            String driver = props.getProperty("db.driver");

            // Load MySQL JDBC Driver
            Class.forName(driver);

            // Establish Connection
            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connection successful! Connected as: " + user);

            // Close Connection
            connection.close();
            System.out.println("Connection closed.");
        } catch (IOException e) {
            System.out.println("Error: Could not read properties file.");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Error: MySQL JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error: Database connection failed.");
            e.printStackTrace();
        }
    }
}
