package edu.univ.erp.ui.student;

import javax.swing.*;
import java.awt.*;

public class StudentDashboard extends JFrame {
    public StudentDashboard() {
        setTitle("Student Dashboard");
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Create Menu Bar (Same as before) ---
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem("Logout"));
        menuBar.add(fileMenu);

        JMenu coursesMenu = new JMenu("Courses");
        coursesMenu.add(new JMenuItem("Browse Course Catalog"));
        coursesMenu.add(new JMenuItem("My Registrations"));
        menuBar.add(coursesMenu);

        JMenu myMenu = new JMenu("My Info");
        myMenu.add(new JMenuItem("View Timetable"));
        myMenu.add(new JMenuItem("View Grades"));
        myMenu.add(new JMenuItem("Download Transcript"));
        menuBar.add(myMenu);

        setJMenuBar(menuBar);

        // --- Create the Tabbed Pane ---
        JTabbedPane tabbedPane = new JTabbedPane();

        // Add your new panels as tabs
        tabbedPane.addTab("Course Catalog", new CourseCatalogPanel());
        tabbedPane.addTab("My Registrations", new MyRegistrationsPanel());
        tabbedPane.addTab("My Timetable", new TimetablePanel());

        // Add the tabbed pane to the window
        // (Replaces the old "add(new JLabel(...))")
        add(tabbedPane);
    }
}