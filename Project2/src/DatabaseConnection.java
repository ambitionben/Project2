import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static void main(String[] args) {
        // Load database configuration
        DatabaseConfig dbConfig = new DatabaseConfig("project2");
        // Load user configuration
        UserConfig userConfig = new UserConfig("root");

        try {
            // Load the MySQL JDBC driver
            Class.forName(dbConfig.getDriverClass());
            // Establish the database connection
            try (Connection connection = DriverManager.getConnection(dbConfig.getDbUrl(), userConfig.getUsername(),
                    userConfig.getPassword())) {
                if (connection != null) {
                    System.out.println("Connection established successfully!");
                } else {
                    System.out.println("Failed to make connection!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
