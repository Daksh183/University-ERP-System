package edu.univ.erp.ui.admin;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {
    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Create Menu Bar (Same as before) ---
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem("Logout"));
        menuBar.add(fileMenu);

        JMenu manageMenu = new JMenu("Manage");
        manageMenu.add(new JMenuItem("Manage Users"));
        manageMenu.add(new JMenuItem("Manage Courses & Sections"));
        menuBar.add(manageMenu);

        JMenu systemMenu = new JMenu("System");
        systemMenu.add(new JMenuItem("Toggle Maintenance Mode"));
        menuBar.add(systemMenu);

        setJMenuBar(menuBar);

        // --- Create the Tabbed Pane ---
        JTabbedPane tabbedPane = new JTabbedPane();

        // Add your new panels as tabs
        tabbedPane.addTab("User Management", new UserManagementPanel());
        tabbedPane.addTab("Course Management", new CourseManagementPanel());
        tabbedPane.addTab("System Settings", new SettingsPanel());

        // Add the tabbed pane to the window
        // (Replaces the old "add(new JLabel(...))")
        add(tabbedPane);
    }
}