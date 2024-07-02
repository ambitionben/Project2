
/*
Name: Benjamin Belizaire
Course: CNT 4714 Summer 2024
Assignment title: Project 2 – A Two-tier Client-Server Application
Date: July 7, 2024
Class: DatabaseConfig
*/
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private Properties properties = new Properties();
    private boolean isAccountant = false;

    public DatabaseConfig(String dbName) {
        if ("theaccountant".equals(dbName)) {
            isAccountant = true;
        }
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(dbName + ".properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find " + dbName + ".properties");
                return;
            }
            properties.load(input);
            System.out.println("Loaded properties for " + dbName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDriverClass() {
        return properties.getProperty("MYSQL_DB_DRIVER_CLASS");
    }

    public String getDbUrl() {
        return properties.getProperty("MYSQL_DB_URL");
    }

    public String getUsername() {
        return isAccountant ? properties.getProperty("MYSQL_DB_USERNAME") : null;
    }

    public String getPassword() {
        return isAccountant ? properties.getProperty("MYSQL_DB_PASSWORD") : null;
    }
}
