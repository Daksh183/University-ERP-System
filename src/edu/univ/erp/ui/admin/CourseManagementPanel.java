package edu.univ.erp.ui.admin;

import edu.univ.erp.service.AdminService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class CourseManagementPanel extends JPanel {

    private AdminService adminService;

    // Components for "Create Course"
    private JTextField courseCodeField;
    private JTextField courseTitleField;
    private JTextField creditsField;
    private JButton createCourseButton;

    // Components for "Create Section"
    private JTextField sectionCourseIdField;
    private JTextField sectionInstructorIdField;
    private JTextField dayTimeField;
    private JTextField roomField;
    private JTextField capacityField;
    private JButton createSectionButton;

    public CourseManagementPanel() {
        this.adminService = new AdminService();
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        // --- 1. Create Course Tab ---
        JPanel createCoursePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcCourse = new GridBagConstraints();
        gbcCourse.insets = new Insets(5, 5, 5, 5);
        gbcCourse.fill = GridBagConstraints.HORIZONTAL;

        gbcCourse.gridx = 0; gbcCourse.gridy = 0;
        createCoursePanel.add(new JLabel("Course Code:"), gbcCourse);
        courseCodeField = new JTextField(10);
        gbcCourse.gridx = 1; gbcCourse.gridy = 0;
        createCoursePanel.add(courseCodeField, gbcCourse);

        gbcCourse.gridx = 0; gbcCourse.gridy = 1;
        createCoursePanel.add(new JLabel("Course Title:"), gbcCourse);
        courseTitleField = new JTextField(20);
        gbcCourse.gridx = 1; gbcCourse.gridy = 1;
        createCoursePanel.add(courseTitleField, gbcCourse);

        gbcCourse.gridx = 0; gbcCourse.gridy = 2;
        createCoursePanel.add(new JLabel("Credits:"), gbcCourse);
        creditsField = new JTextField(5);
        gbcCourse.gridx = 1; gbcCourse.gridy = 2;
        createCoursePanel.add(creditsField, gbcCourse);

        createCourseButton = new JButton("Create Course");
        gbcCourse.gridx = 0; gbcCourse.gridy = 3; gbcCourse.gridwidth = 2;
        createCoursePanel.add(createCourseButton, gbcCourse);

        tabbedPane.addTab("Create New Course", createCoursePanel);

        // --- 2. Create Section Tab ---
        JPanel createSectionPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcSection = new GridBagConstraints();
        gbcSection.insets = new Insets(5, 5, 5, 5);
        gbcSection.fill = GridBagConstraints.HORIZONTAL;

        gbcSection.gridx = 0; gbcSection.gridy = 0;
        createSectionPanel.add(new JLabel("Course ID:"), gbcSection);
        sectionCourseIdField = new JTextField(10);
        gbcSection.gridx = 1; gbcSection.gridy = 0;
        createSectionPanel.add(sectionCourseIdField, gbcSection);

        gbcSection.gridx = 0; gbcSection.gridy = 1;
        createSectionPanel.add(new JLabel("Instructor ID:"), gbcSection);
        sectionInstructorIdField = new JTextField(10);
        gbcSection.gridx = 1; gbcSection.gridy = 1;
        createSectionPanel.add(sectionInstructorIdField, gbcSection);

        gbcSection.gridx = 0; gbcSection.gridy = 2;
        createSectionPanel.add(new JLabel("Day/Time:"), gbcSection);
        dayTimeField = new JTextField(15);
        gbcSection.gridx = 1; gbcSection.gridy = 2;
        createSectionPanel.add(dayTimeField, gbcSection);

        gbcSection.gridx = 0; gbcSection.gridy = 3;
        createSectionPanel.add(new JLabel("Room:"), gbcSection);
        roomField = new JTextField(10);
        gbcSection.gridx = 1; gbcSection.gridy = 3;
        createSectionPanel.add(roomField, gbcSection);

        gbcSection.gridx = 0; gbcSection.gridy = 4;
        createSectionPanel.add(new JLabel("Capacity:"), gbcSection);
        capacityField = new JTextField(5);
        gbcSection.gridx = 1; gbcSection.gridy = 4;
        createSectionPanel.add(capacityField, gbcSection);

        createSectionButton = new JButton("Create Section");
        gbcSection.gridx = 0; gbcSection.gridy = 5; gbcSection.gridwidth = 2;
        createSectionPanel.add(createSectionButton, gbcSection);

        tabbedPane.addTab("Create New Section", createSectionPanel);

        // --- Add main tabbed pane to the panel ---
        add(tabbedPane, BorderLayout.NORTH); // Place forms at the top

        // --- Action Listeners ---
        createCourseButton.addActionListener(e -> performCreateCourse());
        createSectionButton.addActionListener(e -> performCreateSection());
    }

    private void performCreateCourse() {
        try {
            String code = courseCodeField.getText();
            String title = courseTitleField.getText();
            int credits = Integer.parseInt(creditsField.getText());

            if (code.isEmpty() || title.isEmpty()) {
                showError("Code and Title are required.");
                return;
            }

            adminService.createCourse(code, title, credits);
            showMessage("Successfully created course: " + code);

            // Clear fields
            courseCodeField.setText("");
            courseTitleField.setText("");
            creditsField.setText("");

        } catch (NumberFormatException e) {
            showError("Credits must be a number.");
        } catch (SQLException e) {
            showError("Error creating course: " + e.getMessage());
        }
    }

    private void performCreateSection() {
        try {
            int courseId = Integer.parseInt(sectionCourseIdField.getText());
            int instructorId = Integer.parseInt(sectionInstructorIdField.getText());
            String dayTime = dayTimeField.getText();
            String room = roomField.getText();
            int capacity = Integer.parseInt(capacityField.getText());

            adminService.createSection(courseId, instructorId, dayTime, room, capacity);
            showMessage("Successfully created new section for Course ID: " + courseId);

            // Clear fields
            sectionCourseIdField.setText("");
            sectionInstructorIdField.setText("");
            dayTimeField.setText("");
            roomField.setText("");
            capacityField.setText("");

        } catch (NumberFormatException e) {
            showError("Course ID, Instructor ID, and Capacity must be numbers.");
        } catch (SQLException e) {
            showError("Error creating section: " + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}