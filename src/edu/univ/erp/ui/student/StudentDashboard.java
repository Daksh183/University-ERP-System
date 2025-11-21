package edu.univ.erp.ui.student;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.ui.auth.LoginWindow;
import edu.univ.erp.ui.auth.ChangePasswordDialog;

import javax.swing.*;
import java.awt.*;

public class StudentDashboard extends JFrame {

    private JTabbedPane tabbedPane;

    public StudentDashboard() {
        setTitle("Student Dashboard");
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Menu Bar Setup ---
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem changePassItem = new JMenuItem("Change Password");
        JMenuItem logoutItem = new JMenuItem("Logout");
        fileMenu.add(changePassItem);
        fileMenu.addSeparator();
        fileMenu.add(logoutItem);
        menuBar.add(fileMenu);

        JMenu viewMenu = new JMenu("View");
        JMenuItem itemCatalog = new JMenuItem("Course Catalog");
        JMenuItem itemRegistrations = new JMenuItem("My Registrations");
        JMenuItem itemTimetable = new JMenuItem("My Timetable");
        JMenuItem itemGrades = new JMenuItem("My Grades");
        JMenuItem itemTranscript = new JMenuItem("Transcript");

        viewMenu.add(itemCatalog);
        viewMenu.add(itemRegistrations);
        viewMenu.add(itemTimetable);
        viewMenu.add(itemGrades);
        viewMenu.add(itemTranscript);
        menuBar.add(viewMenu);

        setJMenuBar(menuBar);

        // --- Tabs Setup ---
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Course Catalog", new CourseCatalogPanel());
        tabbedPane.addTab("My Registrations", new MyRegistrationsPanel());
        tabbedPane.addTab("My Timetable", new TimetablePanel());
        tabbedPane.addTab("My Grades", new MyGradesPanel());
        tabbedPane.addTab("Transcript", new TranscriptPanel());

        add(tabbedPane);
        tabbedPane.setSelectedIndex(0);

        // --- EVENT LISTENERS ---

        // 1. Menu Item Actions
        itemCatalog.addActionListener(e -> tabbedPane.setSelectedIndex(0));
        itemRegistrations.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        itemTimetable.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        itemGrades.addActionListener(e -> tabbedPane.setSelectedIndex(3));
        itemTranscript.addActionListener(e -> tabbedPane.setSelectedIndex(4));

        changePassItem.addActionListener(e -> new ChangePasswordDialog(this).setVisible(true));

        logoutItem.addActionListener(e -> {
            UserSession.getInstance().clearSession();
            this.dispose();
            new LoginWindow().setVisible(true);
        });

        // 2. Tab Change Listener (UPDATED)
        // This now refreshes ALL panels when you click their tab
        tabbedPane.addChangeListener(e -> {
            Component selected = tabbedPane.getSelectedComponent();

            if (selected instanceof MyRegistrationsPanel) {
                ((MyRegistrationsPanel) selected).refreshData();
            }
            else if (selected instanceof TimetablePanel) {
                ((TimetablePanel) selected).refreshData();
            }
            else if (selected instanceof MyGradesPanel) {
                ((MyGradesPanel) selected).refreshData();
            }
            else if (selected instanceof TranscriptPanel) {
                ((TranscriptPanel) selected).refreshData();
            }
        });
    }
}