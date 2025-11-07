package edu.univ.erp.ui.instructor;

import javax.swing.*;
import java.awt.*;

public class InstructorDashboard extends JFrame {
    public InstructorDashboard() {
        setTitle("Instructor Dashboard");
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

        // 3. Create Instructor-specific menus
        JMenu sectionsMenu = new JMenu("Sections");
        sectionsMenu.add(new JMenuItem("View My Sections"));
        sectionsMenu.add(new JMenuItem("View Gradebook"));
        menuBar.add(sectionsMenu);

        // 4. Add the menu bar to the window
        setJMenuBar(menuBar);

        // Add a welcome label
        add(new JLabel("Welcome, Instructor!", SwingConstants.CENTER));

        // TODO: Add action listeners to menu items (in a later week)
    }
}