package edu.univ.erp.ui.auth;

import edu.univ.erp.auth.AuthDAO;
import edu.univ.erp.auth.AuthDAOImpl;
import edu.univ.erp.auth.PasswordHasher;
import edu.univ.erp.auth.UserSession;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class ChangePasswordDialog extends JDialog {

    private JPasswordField oldPassField;
    private JPasswordField newPassField;
    private JPasswordField confirmPassField;
    private JButton changeButton;
    private JButton cancelButton;
    private AuthDAO authDAO;

    public ChangePasswordDialog(JFrame parent) {
        super(parent, "Change My Password", true); // true = Modal
        this.authDAO = new AuthDAOImpl();

        setLayout(new BorderLayout(10, 10));
        setSize(400, 250);
        setLocationRelativeTo(parent);

        // --- Form Panel ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Current Password:"), gbc);
        oldPassField = new JPasswordField(15);
        gbc.gridx = 1;
        formPanel.add(oldPassField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("New Password:"), gbc);
        newPassField = new JPasswordField(15);
        gbc.gridx = 1;
        formPanel.add(newPassField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Confirm New Password:"), gbc);
        confirmPassField = new JPasswordField(15);
        gbc.gridx = 1;
        formPanel.add(confirmPassField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        changeButton = new JButton("Change Password");
        cancelButton = new JButton("Cancel");

        buttonPanel.add(changeButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Actions ---
        changeButton.addActionListener(e -> performChangePassword());
        cancelButton.addActionListener(e -> dispose());
    }

    private void performChangePassword() {
        String oldPass = new String(oldPassField.getPassword());
        String newPass = new String(newPassField.getPassword());
        String confirmPass = new String(confirmPassField.getPassword());

        // 1. Basic Validation
        if (oldPass.isEmpty() || newPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int userId = UserSession.getInstance().getCurrentUser().getUserId();

            // 2. Verify Old Password
            String currentHash = authDAO.getPasswordHash(userId);
            if (currentHash == null || !PasswordHasher.checkPassword(oldPass, currentHash)) {
                JOptionPane.showMessageDialog(this, "Current password is incorrect.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 3. Hash New Password and Save
            String newHash = PasswordHasher.hashPassword(newPass);
            authDAO.updatePassword(userId, newHash);

            JOptionPane.showMessageDialog(this, "Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}