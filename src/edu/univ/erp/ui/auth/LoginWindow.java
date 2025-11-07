package edu.univ.erp.ui.auth;

import javax.swing.*;
import java.awt.*;

// JFrame is the class for a window
public class LoginWindow extends JFrame {

    public LoginWindow() {
        // 1. Set up the window
        setTitle("University ERP - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Quits app on close
        setMinimumSize(new Dimension(400, 300));
        setLocationRelativeTo(null); // Centers the window on screen

        // 2. Add a simple "hello" label
        JLabel welcomeLabel = new JLabel("Week 1 Prototype Window - Success!");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the text

        // 3. Add the label to the window's content area
        add(welcomeLabel);
    }
}