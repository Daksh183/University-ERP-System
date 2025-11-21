package edu.univ.erp.ui.admin;

import edu.univ.erp.data.SettingsDAO;
import edu.univ.erp.data.SettingsDAOImpl;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;

public class SettingsPanel extends JPanel {

    private SettingsDAO settingsDAO;

    // UI Components
    private JLabel statusLabel;
    private JButton toggleButton;

    // Date Components
    private JTextField regDeadlineField;
    private JTextField dropDeadlineField;
    private JButton updateDatesButton;
    private JLabel currentDatesLabel;

    private boolean isMaintenanceOn = false;

    public SettingsPanel() {
        this.settingsDAO = new SettingsDAOImpl();
        setLayout(new BorderLayout(10, 10));

        // --- Panel 1: System Control (Maintenance) ---
        JPanel maintPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        maintPanel.setBorder(BorderFactory.createTitledBorder("System Control"));

        statusLabel = new JLabel("Loading...");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        toggleButton = new JButton("Toggle Mode");

        maintPanel.add(statusLabel);
        maintPanel.add(Box.createHorizontalStrut(20));
        maintPanel.add(toggleButton);

        // --- Panel 2: Academic Calendar (Set Dates) ---
        JPanel datePanel = new JPanel(new GridBagLayout());
        datePanel.setBorder(BorderFactory.createTitledBorder("Academic Calendar (Set Dates)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); gbc.fill = GridBagConstraints.HORIZONTAL;

        // Registration Deadline Row
        gbc.gridx=0; gbc.gridy=0;
        datePanel.add(new JLabel("Registration Deadline (YYYY-MM-DD):"), gbc);
        regDeadlineField = new JTextField(10);
        gbc.gridx=1;
        datePanel.add(regDeadlineField, gbc);

        // Drop Deadline Row
        gbc.gridx=0; gbc.gridy=1;
        datePanel.add(new JLabel("Drop Deadline (YYYY-MM-DD):"), gbc);
        dropDeadlineField = new JTextField(10);
        gbc.gridx=1;
        datePanel.add(dropDeadlineField, gbc);

        // Button
        updateDatesButton = new JButton("Update Dates");
        gbc.gridx=1; gbc.gridy=2;
        datePanel.add(updateDatesButton, gbc);

        // Status Label
        currentDatesLabel = new JLabel("Current: -");
        currentDatesLabel.setForeground(Color.BLUE);
        gbc.gridx=0; gbc.gridy=3; gbc.gridwidth=2;
        datePanel.add(currentDatesLabel, gbc);

        // --- Add Panels to Main ---
        JPanel container = new JPanel(new GridLayout(2, 1, 10, 10));
        container.add(maintPanel);
        container.add(datePanel);
        add(container, BorderLayout.NORTH);

        // --- Listeners ---
        toggleButton.addActionListener(e -> performToggle());
        updateDatesButton.addActionListener(e -> performUpdateDates());

        loadCurrentStatus();
    }

    private void loadCurrentStatus() {
        try {
            // 1. Maintenance
            this.isMaintenanceOn = settingsDAO.isMaintenanceModeOn();
            updateUILabel();

            // 2. Dates
            LocalDate regDate = settingsDAO.getRegistrationDeadline();
            LocalDate dropDate = settingsDAO.getDropDeadline();

            regDeadlineField.setText(regDate.toString());
            dropDeadlineField.setText(dropDate.toString());

            currentDatesLabel.setText("Current Settings -> Register by: " + regDate + " | Drop by: " + dropDate);

        } catch (SQLException e) {
            statusLabel.setText("Error loading data.");
        }
    }

    private void performUpdateDates() {
        try {
            String regStr = regDeadlineField.getText();
            String dropStr = dropDeadlineField.getText();

            // Validate
            LocalDate.parse(regStr);
            LocalDate.parse(dropStr);

            // Save
            settingsDAO.setRegistrationDeadline(regStr);
            settingsDAO.setDropDeadline(dropStr);

            JOptionPane.showMessageDialog(this, "Academic dates updated successfully!");
            loadCurrentStatus();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid Date Format (Use YYYY-MM-DD)", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performToggle() {
        try {
            boolean newStatus = !this.isMaintenanceOn;
            settingsDAO.setMaintenanceMode(newStatus);
            this.isMaintenanceOn = newStatus;
            updateUILabel();
            JOptionPane.showMessageDialog(this, "Maintenance Mode is now " + (newStatus ? "ON" : "OFF"));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updateUILabel() {
        if (this.isMaintenanceOn) {
            statusLabel.setText("Maintenance Mode: ON");
            statusLabel.setForeground(Color.RED);
        } else {
            statusLabel.setText("Maintenance Mode: OFF");
            statusLabel.setForeground(new Color(0, 128, 0));
        }
    }
}