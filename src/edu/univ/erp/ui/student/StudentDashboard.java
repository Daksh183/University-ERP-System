package edu.univ.erp.ui.student;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.ui.auth.LoginWindow;

import javax.swing.*;
import java.awt.*;

public class StudentDashboard extends JFrame {

    private JTabbedPane tabbedPane;

    public StudentDashboard() {
        setTitle("Student Dashboard");
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Create Menu Bar ---
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        fileMenu.add(logoutItem);
        menuBar.add(fileMenu);

        // Navigation Menu (Shortcuts to tabs)
        JMenu viewMenu = new JMenu("View");
        JMenuItem itemCatalog = new JMenuItem("Course Catalog");
        JMenuItem itemRegistrations = new JMenuItem("My Registrations");
        JMenuItem itemTimetable = new JMenuItem("My Timetable");
        JMenuItem itemGrades = new JMenuItem("My Grades");
        JMenuItem itemTranscript = new JMenuItem("Transcript"); // Updated

        viewMenu.add(itemCatalog);
        viewMenu.add(itemRegistrations);
        viewMenu.add(itemTimetable);
        viewMenu.add(itemGrades);
        viewMenu.add(itemTranscript);
        menuBar.add(viewMenu);

        setJMenuBar(menuBar);

        // --- Create the Tabbed Pane ---
        tabbedPane = new JTabbedPane();

        // Add all tabs in order
        // Index 0: Catalog (Home Page)
        tabbedPane.addTab("Course Catalog", new CourseCatalogPanel());
        // Index 1: Registrations
        tabbedPane.addTab("My Registrations", new MyRegistrationsPanel());
        // Index 2: Timetable
        tabbedPane.addTab("My Timetable", new TimetablePanel());
        // Index 3: Grades
        tabbedPane.addTab("My Grades", new MyGradesPanel());
        // Index 4: Transcript (NEW)
        tabbedPane.addTab("Transcript", new TranscriptPanel());

        add(tabbedPane);

        // --- Set Default View (Home Page) ---
        // This ensures the app always opens on the Course Catalog
        tabbedPane.setSelectedIndex(0);

        // --- Menu Navigation Logic ---
        itemCatalog.addActionListener(e -> tabbedPane.setSelectedIndex(0));
        itemRegistrations.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        itemTimetable.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        itemGrades.addActionListener(e -> tabbedPane.setSelectedIndex(3));
        itemTranscript.addActionListener(e -> tabbedPane.setSelectedIndex(4));

        // Logout Logic
        logoutItem.addActionListener(e -> {
            UserSession.getInstance().clearSession();
            this.dispose();
            new LoginWindow().setVisible(true);
        });
    }
}