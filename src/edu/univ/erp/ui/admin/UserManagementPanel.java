package edu.univ.erp.ui.admin;

import edu.univ.erp.service.AdminService;
// We no longer need StudentService for the exception
import edu.univ.erp.service.ServiceException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class UserManagementPanel extends JPanel {

    // 1. Declare UI components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JTextField rollOrDeptField;
    private JLabel rollOrDeptLabel;
    private JButton createUserButton;

    // 2. Declare our "brain"
    private AdminService adminService;

    public UserManagementPanel() {
        this.adminService = new AdminService();

        // Use GridBagLayout for a clean form
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Username ---
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Username:"), gbc);

        usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(usernameField, gbc);

        // --- Password ---
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(passwordField, gbc);

        // --- Role ---
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Role:"), gbc);

        String[] roles = {"Student", "Instructor"};
        roleComboBox = new JComboBox<>(roles);
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(roleComboBox, gbc);

        // --- Roll Number / Department ---
        rollOrDeptLabel = new JLabel("Roll Number:"); // Default label
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(rollOrDeptLabel, gbc);

        rollOrDeptField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(rollOrDeptField, gbc);

        // --- Create User Button ---
        createUserButton = new JButton("Create User");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; // Span both columns
        gbc.fill = GridBagConstraints.NONE;
        add(createUserButton, gbc);

        // --- Action Listeners ---

        // Change the label based on the selected role
        roleComboBox.addActionListener(e -> {
            String selectedRole = (String) roleComboBox.getSelectedItem();
            if ("Student".equals(selectedRole)) {
                rollOrDeptLabel.setText("Roll Number:");
            } else if ("Instructor".equals(selectedRole)) {
                rollOrDeptLabel.setText("Department:");
            }
        });

        // Handle the button click
        createUserButton.addActionListener(e -> performCreateUser());
    }

    private void performCreateUser() {
        // 1. Get all data from the form
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();
        String rollOrDept = rollOrDeptField.getText();

        // 2. Simple Validation
        if (username.isEmpty() || password.isEmpty() || rollOrDept.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // 3. Call the "brain"
            adminService.createNewUser(username, password, role, rollOrDept);

            // 4. Success!
            JOptionPane.showMessageDialog(this, "Successfully created user: " + username, "User Created", JOptionPane.INFORMATION_MESSAGE);

            // 5. Clear the form for the next entry
            usernameField.setText("");
            passwordField.setText("");
            rollOrDeptField.setText("");

            // ========= FIX #1 & #2: CHANGE THIS CATCH BLOCK =========
        } catch (SQLException | ServiceException ex) {
            // 6. Handle errors (like duplicate username)
            JOptionPane.showMessageDialog(this, "Error creating user: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}