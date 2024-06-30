import java.io.InputStream;
import java.util.Properties;

public class UserConfig {
    private Properties properties = new Properties();

    public UserConfig(String username) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(username + ".properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find " + username + ".properties");
                return;
            }
            properties.load(input);
            System.out.println("Loaded properties for " + username);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return properties.getProperty("MYSQL_DB_USERNAME");
    }

    public String getPassword() {
        return properties.getProperty("MYSQL_DB_PASSWORD");
    }
}
