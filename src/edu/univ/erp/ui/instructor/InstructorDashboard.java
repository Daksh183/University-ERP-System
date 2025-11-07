package edu.univ.erp.ui.instructor;

import javax.swing.*;
import java.awt.*;

public class InstructorDashboard extends JFrame {
    public InstructorDashboard() {
        setTitle("Instructor Dashboard");
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Create Menu Bar (Same as before) ---
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem("Logout"));
        menuBar.add(fileMenu);

        JMenu sectionsMenu = new JMenu("Sections");
        sectionsMenu.add(new JMenuItem("View My Sections"));
        sectionsMenu.add(new JMenuItem("View Gradebook"));
        menuBar.add(sectionsMenu);

        setJMenuBar(menuBar);

        // --- Create the Tabbed Pane ---
        JTabbedPane tabbedPane = new JTabbedPane();

        // Add your new panels as tabs
        tabbedPane.addTab("My Sections", new MySectionsPanel());
        tabbedPane.addTab("Gradebook", new GradebookPanel());

        // Add the tabbed pane to the window
        // (Replaces the old "add(new JLabel(...))")
        add(tabbedPane);
    }
}