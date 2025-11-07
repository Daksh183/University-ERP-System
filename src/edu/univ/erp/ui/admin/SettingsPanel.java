package edu.univ.erp.ui.admin;

import edu.univ.erp.data.SettingsDAO;
import edu.univ.erp.data.SettingsDAOImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class SettingsPanel extends JPanel {

    private SettingsDAO settingsDAO;

    // UI Components
    private JLabel statusLabel;
    private JButton toggleButton;

    private boolean isMaintenanceOn = false; // Local state

    public SettingsPanel() {
        this.settingsDAO = new SettingsDAOImpl();

        // Simple layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // --- Status Label ---
        statusLabel = new JLabel("Loading status...");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(statusLabel, gbc);

        // --- Toggle Button ---
        toggleButton = new JButton("Toggle Mode");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(toggleButton, gbc);

        // --- Action Listener ---
        toggleButton.addActionListener(e -> performToggle());

        // --- Load initial status ---
        loadCurrentStatus();
    }

    /**
     * Reads the current status from the DB and updates the UI.
     */
    private void loadCurrentStatus() {
        try {
            this.isMaintenanceOn = settingsDAO.isMaintenanceModeOn();
            updateUILabel();
        } catch (SQLException e) {
            statusLabel.setText("Error loading status.");
            statusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, "Error loading status: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Flips the maintenance mode setting.
     */
    private void performToggle() {
        try {
            // Flip the boolean
            boolean newStatus = !this.isMaintenanceOn;

            // Call the "brain" to update the DB
            settingsDAO.setMaintenanceMode(newStatus);

            // Update our local state
            this.isMaintenanceOn = newStatus;

            // Update the UI
            updateUILabel();

            JOptionPane.showMessageDialog(this, "Maintenance Mode is now " + (newStatus ? "ON" : "OFF"),
                    "Status Updated", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating status: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Helper method to update the label text and color.
     */
    private void updateUILabel() {
        if (this.isMaintenanceOn) {
            statusLabel.setText("Maintenance Mode is currently: ON");
            statusLabel.setForeground(Color.RED);
        } else {
            statusLabel.setText("Maintenance Mode is currently: OFF");
            statusLabel.setForeground(Color.BLACK);
        }
    }
}