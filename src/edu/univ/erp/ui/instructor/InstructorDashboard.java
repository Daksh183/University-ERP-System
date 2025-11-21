package edu.univ.erp.ui.instructor;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.ui.auth.LoginWindow;
import edu.univ.erp.ui.auth.ChangePasswordDialog;

import javax.swing.*;
import java.awt.*;

public class InstructorDashboard extends JFrame {

    private JTabbedPane tabbedPane;

    public InstructorDashboard() {
        setTitle("Instructor Dashboard");
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem changePassItem = new JMenuItem("Change Password");
        JMenuItem logoutItem = new JMenuItem("Logout");
        fileMenu.add(changePassItem);
        fileMenu.addSeparator();
        fileMenu.add(logoutItem);
        menuBar.add(fileMenu);

        JMenu sectionsMenu = new JMenu("Sections");
        JMenuItem itemMySections = new JMenuItem("View My Sections");
        JMenuItem itemGradebook = new JMenuItem("View Gradebook");
        JMenuItem itemClassStats = new JMenuItem("View Class Stats");

        sectionsMenu.add(itemMySections);
        sectionsMenu.add(itemGradebook);
        sectionsMenu.add(itemClassStats);
        menuBar.add(sectionsMenu);

        setJMenuBar(menuBar);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("My Sections", new MySectionsPanel());
        tabbedPane.addTab("Gradebook", new GradebookPanel());
        tabbedPane.addTab("Class Stats", new ClassStatsPanel());

        add(tabbedPane);

        itemMySections.addActionListener(e -> tabbedPane.setSelectedIndex(0));
        itemGradebook.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        itemClassStats.addActionListener(e -> tabbedPane.setSelectedIndex(2));

        changePassItem.addActionListener(e -> new ChangePasswordDialog(this).setVisible(true));

        logoutItem.addActionListener(e -> {
            UserSession.getInstance().clearSession();
            this.dispose();
            new LoginWindow().setVisible(true);
        });
    }
}