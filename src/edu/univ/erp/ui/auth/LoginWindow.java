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

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;
    private AuthDAO authDAO;
    private Timer lockoutTimer; // To handle the countdown

    public LoginWindow() {
        this.authDAO = new AuthDAOImpl();

        setTitle("University ERP - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(400, 300));
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);

        usernameField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0;
        panel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(passwordField, gbc);

        loginButton = new JButton("Login");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(loginButton, gbc);

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(statusLabel, gbc);

        add(panel);

        loginButton.addActionListener(e -> performLogin());
    }

    private void performLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Username and password cannot be empty.");
            return;
        }

        try {
            User user = authDAO.login(username, password);
            UserSession.getInstance().createUserSession(user);
            openRoleBasedDashboard(user.getRole());
            this.dispose();

        } catch (AuthException ex) {
            // CHECK FOR LOCKOUT TIMER
            if (ex.getWaitSeconds() > 0) {
                startCountdown(ex.getWaitSeconds());
            } else {
                statusLabel.setText(ex.getMessage());
            }
        } catch (SQLException ex) {
            statusLabel.setText("Database Error: " + ex.getMessage());
        }
    }

    private void startCountdown(long seconds) {
        loginButton.setEnabled(false); // Disable button
        usernameField.setEnabled(false);
        passwordField.setEnabled(false);

        // Create a Timer that ticks every 1 second (1000ms)
        lockoutTimer = new Timer(1000, new ActionListener() {
            long timeLeft = seconds;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (timeLeft > 0) {
                    statusLabel.setText("Locked! Try again in " + timeLeft + " seconds.");
                    timeLeft--;
                } else {
                    ((Timer)e.getSource()).stop(); // Stop timer
                    loginButton.setEnabled(true); // Re-enable inputs
                    usernameField.setEnabled(true);
                    passwordField.setEnabled(true);
                    statusLabel.setText("Login unlocked. Please try again.");
                    statusLabel.setForeground(new Color(0, 128, 0)); // Green
                }
            }
        });

        lockoutTimer.setInitialDelay(0); // Start immediately
        lockoutTimer.start();
    }

    private void openRoleBasedDashboard(String role) {
        if (role.equals("Student")) {
            new StudentDashboard().setVisible(true);
        } else if (role.equals("Instructor")) {
            new InstructorDashboard().setVisible(true);
        } else if (role.equals("Admin")) {
            new AdminDashboard().setVisible(true);
        }
    }
}