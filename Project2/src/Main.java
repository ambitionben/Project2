import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    private static Connection connection;
    private static JComboBox<String> userDropdown;
    private static JComboBox<String> dbDropdown;
    private static JTextField usernameField;
    private static JPasswordField passwordField;
    private static JTextArea commandInput;
    private static JTextArea commandOutput;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Database Client Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel userLabel = new JLabel("Select User:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(userLabel, gbc);

        userDropdown = new JComboBox<>(new String[] { "root", "client1", "client2", "project2app", "theaccountant" });
        gbc.gridx = 1;
        panel.add(userDropdown, gbc);

        JLabel dbLabel = new JLabel("Select Database:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(dbLabel, gbc);

        dbDropdown = new JComboBox<>(new String[] { "operationslog", "bikedb", "project2" });
        gbc.gridx = 1;
        panel.add(dbDropdown, gbc);

        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        JButton connectButton = new JButton("Connect");
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(connectButton, gbc);

        JButton disconnectButton = new JButton("Disconnect");
        gbc.gridx = 1;
        panel.add(disconnectButton, gbc);

        commandInput = new JTextArea(10, 50);
        JScrollPane inputScroll = new JScrollPane(commandInput);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(inputScroll, gbc);

        JButton executeButton = new JButton("Execute");
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(executeButton, gbc);

        JButton clearCommandButton = new JButton("Clear Command");
        gbc.gridx = 1;
        panel.add(clearCommandButton, gbc);

        commandOutput = new JTextArea(10, 50);
        JScrollPane outputScroll = new JScrollPane(commandOutput);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        panel.add(outputScroll, gbc);

        JButton clearOutputButton = new JButton("Clear Output");
        gbc.gridx = 0;
        gbc.gridy = 8;
        panel.add(clearOutputButton, gbc);

        frame.getContentPane().add(panel);
        frame.setVisible(true);

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToDatabase();
            }
        });

        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disconnectFromDatabase();
            }
        });

        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeCommand();
            }
        });

        clearCommandButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                commandInput.setText("");
            }
        });

        clearOutputButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                commandOutput.setText("");
            }
        });
    }

    private static void connectToDatabase() {
        String selectedUser = (String) userDropdown.getSelectedItem();
        String selectedDb = (String) dbDropdown.getSelectedItem();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        DatabaseConfig dbConfig = new DatabaseConfig(selectedDb);
        String expectedUsername = null;
        String expectedPassword = null;

        if ("theaccountant".equals(selectedUser)) {
            if (!"operationslog".equals(selectedDb)) {
                commandOutput.append("Invalid database for theaccountant.\n");
                return;
            }
            expectedUsername = dbConfig.getUsername();
            expectedPassword = dbConfig.getPassword();
        } else {
            UserConfig userConfig = new UserConfig(selectedUser);
            expectedUsername = userConfig.getUsername();
            expectedPassword = userConfig.getPassword();
            if ("client1".equals(selectedUser) && !("project2".equals(selectedDb) || "bikedb".equals(selectedDb))) {
                commandOutput.append("Invalid database for client1.\n");
                return;
            } else if ("client2".equals(selectedUser)
                    && !("project2".equals(selectedDb) || "bikedb".equals(selectedDb))) {
                commandOutput.append("Invalid database for client2.\n");
                return;
            } else if ("project2app".equals(selectedUser) && !"operationslog".equals(selectedDb)) {
                commandOutput.append("Invalid database for project2app.\n");
                return;
            }
        }

        if (username.equals(expectedUsername) && password.equals(expectedPassword)) {
            try {
                Class.forName(dbConfig.getDriverClass());
                connection = DriverManager.getConnection(dbConfig.getDbUrl(), username, password);
                commandOutput.append("Connection established successfully!\n");
            } catch (ClassNotFoundException | SQLException ex) {
                ex.printStackTrace();
                commandOutput.append("Failed to establish connection: " + ex.getMessage() + "\n");
            }
        } else {
            commandOutput.append("Invalid credentials.\n");
        }
    }

    private static void disconnectFromDatabase() {
        if (connection != null) {
            try {
                connection.close();
                commandOutput.append("Disconnected successfully!\n");
            } catch (SQLException ex) {
                ex.printStackTrace();
                commandOutput.append("Failed to disconnect: " + ex.getMessage() + "\n");
            }
        } else {
            commandOutput.append("No connection to disconnect.\n");
        }
    }

    private static void executeCommand() {
        String command = commandInput.getText();
        if (connection != null) {
            try {
                if (command.trim().toLowerCase().startsWith("select")) {
                    PreparedStatement stmt = connection.prepareStatement(command);
                    ResultSet rs = stmt.executeQuery();
                    int columnCount = rs.getMetaData().getColumnCount();
                    while (rs.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            commandOutput.append(rs.getString(i) + "\t");
                        }
                        commandOutput.append("\n");
                    }
                } else {
                    PreparedStatement stmt = connection.prepareStatement(command);
                    int result = stmt.executeUpdate();
                    commandOutput.append("Command executed successfully, affected rows: " + result + "\n");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                commandOutput.append("Failed to execute command: " + ex.getMessage() + "\n");
            }
        } else {
            commandOutput.append("No connection established.\n");
        }
    }
}