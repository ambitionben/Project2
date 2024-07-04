/*
Name: Benjamin Belizaire
Course: CNT 4714 Summer 2024
Assignment title: Project 2 – A Two-tier Client-Server Application
Date: July 7, 2024
Class: Main
*/

import javax.swing.*;

import com.mysql.cj.jdbc.result.ResultSetMetaData;

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
    private static JTextArea statusOutput;

    public static void main(String[] args) {
        // Define a high-contrast color palette
        Color backgroundColor = new Color(255, 255, 255); // White background
        Color panelColor = new Color(220, 220, 220); // Light gray for panels
        Color textColor = new Color(0, 0, 0); // Black text color
        Color connectButtonColor = new Color(0, 120, 215); // Blue color
        Color disconnectButtonColor = new Color(255, 69, 0); // Red color
        Color executeButtonColor = new Color(34, 139, 34); // Green color
        Color clearButtonColor = new Color(255, 215, 0); // Yellow color
        Color statusBackgroundColor = new Color(255, 255, 200); // Light yellow for status area
        Color statusTextColor = new Color(0, 0, 0); // Black text color

        Font boldFont = new Font("Arial", Font.PLAIN, 14);
        Font bolderFont = new Font("Arial", Font.BOLD, 15); // for the buttons for user to see

        JFrame frame = new JFrame("Database Client Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.getContentPane().setBackground(backgroundColor);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(panelColor);
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel userLabel = new JLabel("User Properties:");
        userLabel.setForeground(textColor);
        userLabel.setFont(boldFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(userLabel, gbc);

        userDropdown = new JComboBox<>(new String[] { "root", "client1", "client2",
                "project2app", "theaccountant" });
        userDropdown.setFont(boldFont);
        gbc.gridx = 1;
        panel.add(userDropdown, gbc);

        JLabel dbLabel = new JLabel("DB URL Properties:");
        dbLabel.setForeground(textColor);
        dbLabel.setFont(boldFont);
        gbc.gridx = 2;
        panel.add(dbLabel, gbc);

        dbDropdown = new JComboBox<>(
                new String[] { "operationslog", "bikedb", "project2" });
        dbDropdown.setFont(boldFont);
        gbc.gridx = 3;
        panel.add(dbDropdown, gbc);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(textColor);
        usernameLabel.setFont(boldFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setFont(boldFont);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(textColor);
        passwordLabel.setFont(boldFont);
        gbc.gridx = 2;
        panel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(boldFont);
        gbc.gridx = 3;
        panel.add(passwordField, gbc);

        JButton connectButton = new JButton("Connect");
        connectButton.setBackground(connectButtonColor);
        connectButton.setForeground(Color.WHITE);
        connectButton.setFont(bolderFont);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        // gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(connectButton, gbc);

        JButton disconnectButton = new JButton("Disconnect");
        disconnectButton.setBackground(disconnectButtonColor);
        disconnectButton.setForeground(Color.WHITE);
        disconnectButton.setFont(bolderFont);
        gbc.gridx = 2;
        gbc.gridwidth = 2;
        panel.add(disconnectButton, gbc);

        statusOutput = new JTextArea(2, 50); // A small area for status messages
        statusOutput.setBackground(statusBackgroundColor);
        statusOutput.setForeground(statusTextColor);
        statusOutput.setFont(new Font("Courier New", Font.PLAIN, 14)); // Set to monospaced font
        statusOutput.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Add a border
        statusOutput.setEditable(false); // Make it read-only
        JScrollPane statusScroll = new JScrollPane(statusOutput);
        statusScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        statusScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(statusScroll, gbc);

        commandInput = new JTextArea(5, 50);
        commandInput.setBackground(Color.WHITE);
        commandInput.setForeground(textColor);
        commandInput.setFont(boldFont);
        JScrollPane inputScroll = new JScrollPane(commandInput);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(inputScroll, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBackground(panelColor);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        panel.add(buttonPanel, gbc);

        JButton executeButton = new JButton("Execute");
        executeButton.setBackground(executeButtonColor);
        executeButton.setForeground(Color.WHITE);
        executeButton.setFont(bolderFont);
        buttonPanel.add(executeButton);

        JButton clearCommandButton = new JButton("Clear Command");
        clearCommandButton.setBackground(clearButtonColor);
        clearCommandButton.setForeground(Color.WHITE);
        clearCommandButton.setFont(bolderFont);
        buttonPanel.add(clearCommandButton);

        commandOutput = new JTextArea(15, 70); // Increase the size for better visibility
        commandOutput.setBackground(Color.WHITE);
        commandOutput.setForeground(textColor);
        commandOutput.setFont(new Font("Courier New", Font.PLAIN, 14)); // Set to monospaced font
        commandOutput.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Add a border
        commandOutput.setEditable(false); // Make it read-only
        JScrollPane outputScroll = new JScrollPane(commandOutput);
        outputScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        outputScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH; // Make the scroll pane expand to fill the space
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel.add(outputScroll, gbc);

        JButton clearOutputButton = new JButton("Clear Output");
        clearOutputButton.setBackground(clearButtonColor);
        clearOutputButton.setForeground(Color.WHITE);
        clearOutputButton.setFont(bolderFont);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 4;
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
                statusOutput.append("Invalid database for theaccountant.\n");
                return;
            }
            expectedUsername = dbConfig.getUsername();
            expectedPassword = dbConfig.getPassword();
        } else {
            UserConfig userConfig = new UserConfig(selectedUser);
            expectedUsername = userConfig.getUsername();
            expectedPassword = userConfig.getPassword();
            if ("client1".equals(selectedUser) && !("project2".equals(selectedDb) || "bikedb".equals(selectedDb))) {
                statusOutput.append("Invalid database for client1.\n");
                return;
            } else if ("client2".equals(selectedUser)
                    && !("project2".equals(selectedDb) || "bikedb".equals(selectedDb))) {
                statusOutput.append("Invalid database for client2.\n");
                return;
            } else if ("project2app".equals(selectedUser) && !"operationslog".equals(selectedDb)) {
                statusOutput.append("Invalid database for project2app.\n");
                return;
            }
        }

        if (username.equals(expectedUsername) && password.equals(expectedPassword)) {
            try {
                Class.forName(dbConfig.getDriverClass());
                connection = DriverManager.getConnection(dbConfig.getDbUrl(), username, password);
                statusOutput.append("Connection established successfully!\n");
                statusOutput.append("Connected to database URL: " + dbConfig.getDbUrl() + "\n");
            } catch (ClassNotFoundException | SQLException ex) {
                ex.printStackTrace();
                statusOutput.append("Failed to establish connection: " + ex.getMessage() + "\n");
            }
        } else {
            statusOutput.append("Invalid credentials.\n");
        }
    }

    private static void disconnectFromDatabase() {
        if (connection != null) {
            try {
                connection.close();
                statusOutput.append("Disconnected successfully!\n");
            } catch (SQLException ex) {
                ex.printStackTrace();
                statusOutput.append("Failed to disconnect: " + ex.getMessage() + "\n");
            }
        } else {
            statusOutput.append("No connection to disconnect.\n");
        }
    }

    private static void executeCommand() {
        String command = commandInput.getText();
        if (connection != null) {
            try {
                if (command.trim().toLowerCase().startsWith("select")) {
                    PreparedStatement stmt = connection.prepareStatement(command, ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_READ_ONLY);
                    ResultSet rs = stmt.executeQuery();
                    java.sql.ResultSetMetaData rsmd = rs.getMetaData();
                    int columnCount = rsmd.getColumnCount();

                    // Determine the width of each column
                    int[] columnWidths = new int[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        columnWidths[i - 1] = Math.max(rsmd.getColumnName(i).length(), 20); // minimum width of 20
                    }
                    while (rs.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            int length = rs.getString(i) != null ? rs.getString(i).length() : 0;
                            if (length > columnWidths[i - 1]) {
                                columnWidths[i - 1] = Math.min(length, 30); // maximum width of 30
                            }
                        }
                    }
                    rs.beforeFirst(); // Move back to the beginning of the ResultSet

                    // Print top border
                    StringBuilder border = new StringBuilder("+");
                    for (int width : columnWidths) {
                        border.append("-".repeat(width + 2)).append("+");
                    }
                    commandOutput.append(border.toString() + "\n");

                    // Print column names
                    StringBuilder header = new StringBuilder("|");
                    for (int i = 1; i <= columnCount; i++) {
                        header.append(String.format(" %-" + columnWidths[i - 1] + "s ", rsmd.getColumnName(i)))
                                .append("|");
                    }
                    commandOutput.append(header.toString() + "\n");
                    commandOutput.append(border.toString() + "\n");

                    // Print rows
                    while (rs.next()) {
                        StringBuilder row = new StringBuilder("|");
                        for (int i = 1; i <= columnCount; i++) {
                            String value = rs.getString(i);
                            if (value != null && value.length() > columnWidths[i - 1]) {
                                value = value.substring(0, columnWidths[i - 1] - 3) + "...";
                            }
                            row.append(String.format(" %-" + columnWidths[i - 1] + "s ", value)).append("|");
                        }
                        commandOutput.append(row.toString() + "\n");
                    }

                    // Print bottom border
                    commandOutput.append(border.toString() + "\n");

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
