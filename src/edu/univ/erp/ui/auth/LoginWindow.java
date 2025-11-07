package edu.univ.erp.ui.auth;

import edu.univ.erp.auth.AuthDAO;
import edu.univ.erp.auth.AuthDAOImpl;
import edu.univ.erp.auth.AuthException;
import edu.univ.erp.auth.UserSession;
import edu.univ.erp.domain.User;
import edu.univ.erp.ui.admin.AdminDashboard;
import edu.univ.erp.ui.instructor.InstructorDashboard;
import edu.univ.erp.ui.student.StudentDashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginWindow extends JFrame {

    // 1. Declare the UI components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;

    // 2. Declare our login "brain" (the AuthDAO)
    private AuthDAO authDAO;

    public LoginWindow() {
        this.authDAO = new AuthDAOImpl(); // Create an instance of our login logic

        // --- Basic Window Setup ---
        setTitle("University ERP - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(400, 300));
        setLocationRelativeTo(null);

        // --- Create the Form (using a JPanel) ---
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);

        // Username Text Field
        usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(usernameField, gbc);

        // Password Label
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        // Password Field
        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(passwordField, gbc);

        // Login Button
        loginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Span 2 columns
        gbc.fill = GridBagConstraints.NONE;
        panel.add(loginButton, gbc);

        // Status Label (for error messages)
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(statusLabel, gbc);

        // Add the panel to the window
        add(panel);

        // --- Add the Button's Action ---
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // When the button is clicked, call our login method
                performLogin();
            }
        });
    }

    /**
     * The main login logic method.
     */
    private void performLogin() {
        String username = usernameField.getText();
        // getPassword() returns a char array for security
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Username and password cannot be empty.");
            return;
        }

        try {
            // 1. Call the "brain" (AuthDAO)
            User user = authDAO.login(username, password);

            // 2. Login was successful! Create the session.
            UserSession.getInstance().createUserSession(user);

            // 3. Open the correct dashboard based on role [cite: 5]
            openRoleBasedDashboard(user.getRole());

            // 4. Close this login window
            this.dispose();

        } catch (AuthException | SQLException ex) {
            // Login failed (wrong password or DB error)
            statusLabel.setText(ex.getMessage());
        }
    }

    private void openRoleBasedDashboard(String role) {
        // This is the "role-aware" part of Week 3
        if (role.equals("Student")) {
            new StudentDashboard().setVisible(true);
        } else if (role.equals("Instructor")) {
            new InstructorDashboard().setVisible(true);
        } else if (role.equals("Admin")) {
            new AdminDashboard().setVisible(true);
        }
    }
}