package edu.univ.erp.ui.student;

import javax.swing.*;
import java.awt.*;

public class StudentDashboard extends JFrame {
    public StudentDashboard() {
        setTitle("Student Dashboard");
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

        // 3. Create Student-specific menus
        JMenu coursesMenu = new JMenu("Courses");
        coursesMenu.add(new JMenuItem("Browse Course Catalog"));
        coursesMenu.add(new JMenuItem("My Registrations"));
        menuBar.add(coursesMenu);

        JMenu myMenu = new JMenu("My Info");
        myMenu.add(new JMenuItem("View Timetable"));
        myMenu.add(new JMenuItem("View Grades"));
        myMenu.add(new JMenuItem("Download Transcript"));
        menuBar.add(myMenu);

        // 4. Add the menu bar to the window
        setJMenuBar(menuBar);

        // Add a welcome label
        add(new JLabel("Welcome, Student!", SwingConstants.CENTER));

        // TODO: Add action listeners to menu items (in a later week)
    }
}