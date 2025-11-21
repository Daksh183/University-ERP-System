package edu.univ.erp.ui.admin;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.ui.auth.LoginWindow;
import edu.univ.erp.ui.auth.ChangePasswordDialog;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private JTabbedPane tabbedPane;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
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

        JMenu manageMenu = new JMenu("Manage");
        JMenuItem itemUsers = new JMenuItem("User Management");
        JMenuItem itemCourses = new JMenuItem("Course Management");
        JMenuItem itemSettings = new JMenuItem("System Settings");

        manageMenu.add(itemUsers);
        manageMenu.add(itemCourses);
        manageMenu.add(itemSettings);
        menuBar.add(manageMenu);

        setJMenuBar(menuBar);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("User Management", new UserManagementPanel());
        tabbedPane.addTab("Course Management", new CourseManagementPanel());
        tabbedPane.addTab("System Settings", new SettingsPanel());

        add(tabbedPane);

        itemUsers.addActionListener(e -> tabbedPane.setSelectedIndex(0));
        itemCourses.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        itemSettings.addActionListener(e -> tabbedPane.setSelectedIndex(2));

        changePassItem.addActionListener(e -> new ChangePasswordDialog(this).setVisible(true));

        logoutItem.addActionListener(e -> {
            UserSession.getInstance().clearSession();
            this.dispose();
            new LoginWindow().setVisible(true);
        });
    }
}