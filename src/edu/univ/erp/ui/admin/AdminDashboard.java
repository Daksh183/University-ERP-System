package edu.univ.erp.ui.admin;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.ui.auth.LoginWindow;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private JTabbedPane tabbedPane;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Create Menu Bar ---
        JMenuBar menuBar = new JMenuBar();

        // 1. File Menu (Logout)
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        fileMenu.add(logoutItem);
        menuBar.add(fileMenu);

        // 2. Manage Menu (Consolidated)
        JMenu manageMenu = new JMenu("Manage");
        JMenuItem itemUsers = new JMenuItem("User Management");
        JMenuItem itemCourses = new JMenuItem("Course Management");
        JMenuItem itemSettings = new JMenuItem("System Settings"); // Moved here

        manageMenu.add(itemUsers);
        manageMenu.add(itemCourses);
        manageMenu.add(itemSettings);
        menuBar.add(manageMenu);

        // (Removed separate "System" menu)

        setJMenuBar(menuBar);

        // --- Create the Tabbed Pane ---
        tabbedPane = new JTabbedPane();

        // Add all tabs in order
        tabbedPane.addTab("User Management", new UserManagementPanel());    // Index 0
        tabbedPane.addTab("Course Management", new CourseManagementPanel()); // Index 1
        tabbedPane.addTab("System Settings", new SettingsPanel());           // Index 2

        add(tabbedPane);

        // --- 3. Menu Navigation Logic (Mapping) ---
        itemUsers.addActionListener(e -> tabbedPane.setSelectedIndex(0));
        itemCourses.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        itemSettings.addActionListener(e -> tabbedPane.setSelectedIndex(2));

        // --- 4. Logout Logic ---
        logoutItem.addActionListener(e -> {
            UserSession.getInstance().clearSession(); // Clear session
            this.dispose(); // Close this window
            new LoginWindow().setVisible(true); // Open login window
        });
    }
}