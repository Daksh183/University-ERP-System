package edu.univ.erp.ui.admin;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {
    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 1. Create the Menu Bar
        JMenuBar menuBar = new JMenuBar();

        // 2. Create the "File" menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        fileMenu.add(logoutItem);
        menuBar.add(fileMenu);

        // 3. Create Admin-specific menus
        JMenu manageMenu = new JMenu("Manage");
        manageMenu.add(new JMenuItem("Manage Users"));
        manageMenu.add(new JMenuItem("Manage Courses & Sections"));
        menuBar.add(manageMenu);

        JMenu systemMenu = new JMenu("System");
        systemMenu.add(new JMenuItem("Toggle Maintenance Mode"));
        menuBar.add(systemMenu);

        // 4. Add the menu bar to the window
        setJMenuBar(menuBar);

        // Add a welcome label
        add(new JLabel("Welcome, Admin!", SwingConstants.CENTER));

        // TODO: Add action listeners to menu items (in a later week)
    }
}