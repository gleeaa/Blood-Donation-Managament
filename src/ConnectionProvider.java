import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionProvider {

    public static Connection getCon() {
        // --- YOUR DATABASE DETAILS ---
        // The host where your database is running. 'localhost' is the default for a local server.
        String host = "10.234.195.157";
        
        // The port number. 3306 is the default for MariaDB/MySQL.
        String port = "3307";
        
        // The name of the database you created for this project.
        // Make sure you have created this database in MariaDB first!
        String dbName = "blood_donation_db"; // <-- IMPORTANT: Change this to your actual database name
        
        // Your MariaDB username. 'root' is common for local development.
        String username = "root"; // <-- Change if you created a different user
        
        // Your MariaDB password.
        String password = "Alpha@123"; // <-- IMPORTANT: Change this to your root password

        // --- The Connection URL for MariaDB ---
        // Format: jdbc:mariadb://<host>:<port>/<databaseName>
        String connectionUrl = "jdbc:mariadb://127.0.0.1:3307/blood_donation_db";

        try {
            // Load the MariaDB JDBC driver class.
            // This line tells Java's DriverManager which driver to use.
            Class.forName("org.mariadb.jdbc.Driver");

            // Attempt to establish a connection to the database
            Connection con = DriverManager.getConnection(connectionUrl, username, password);
            
            // If the connection is successful, return the connection object
            return con;

        } catch (ClassNotFoundException e) {
            System.err.println("MariaDB JDBC Driver not found.");
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            System.err.println("Connection failed. Check your URL, username, and password.");
            e.printStackTrace();
            return null;
        }
    }
}