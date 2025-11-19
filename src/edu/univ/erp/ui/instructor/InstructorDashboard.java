package edu.univ.erp.ui.instructor;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.ui.auth.LoginWindow;

import javax.swing.*;
import java.awt.*;

public class InstructorDashboard extends JFrame {

    private JTabbedPane tabbedPane;

    public InstructorDashboard() {
        setTitle("Instructor Dashboard");
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Create Menu Bar ---
        JMenuBar menuBar = new JMenuBar();

        // 1. File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        fileMenu.add(logoutItem);
        menuBar.add(fileMenu);

        // 2. Sections Menu (Navigation)
        JMenu sectionsMenu = new JMenu("Sections");
        JMenuItem itemMySections = new JMenuItem("View My Sections");
        JMenuItem itemGradebook = new JMenuItem("View Gradebook");
        JMenuItem itemClassStats = new JMenuItem("View Class Stats"); // <--- Added this

        sectionsMenu.add(itemMySections);
        sectionsMenu.add(itemGradebook);
        sectionsMenu.add(itemClassStats);
        menuBar.add(sectionsMenu);

        setJMenuBar(menuBar);

        // --- Create the Tabbed Pane ---
        tabbedPane = new JTabbedPane();

        // Add all tabs in order
        tabbedPane.addTab("My Sections", new MySectionsPanel());    // Index 0
        tabbedPane.addTab("Gradebook", new GradebookPanel());       // Index 1
        tabbedPane.addTab("Class Stats", new ClassStatsPanel());    // Index 2

        add(tabbedPane);

        // --- 3. Menu Navigation Logic (The "Mapping") ---
        itemMySections.addActionListener(e -> tabbedPane.setSelectedIndex(0));
        itemGradebook.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        itemClassStats.addActionListener(e -> tabbedPane.setSelectedIndex(2));

        // --- 4. Logout Logic ---
        logoutItem.addActionListener(e -> {
            UserSession.getInstance().clearSession(); // Clear session
            this.dispose(); // Close this window
            new LoginWindow().setVisible(true); // Open login window
        });
    }
}