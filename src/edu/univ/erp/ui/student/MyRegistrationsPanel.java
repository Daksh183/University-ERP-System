package edu.univ.erp.ui.student;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.service.ServiceException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class MyRegistrationsPanel extends JPanel {

    private JTable registrationsTable;
    private DefaultTableModel tableModel;
    private JButton dropButton;

    private StudentService studentService;
    // We keep this list in memory so we can look up the Enrollment ID
    // corresponding to the selected row index.
    private List<Enrollment> enrollmentList;

    public MyRegistrationsPanel() {
        this.studentService = new StudentService();

        setLayout(new BorderLayout(10, 10));

        // --- Create the Table ---
        // CHANGED: First column is now "Course Code" instead of "Enrollment ID"
        String[] columnNames = {"Course Code", "Section ID", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        registrationsTable = new JTable(tableModel);
        registrationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(registrationsTable);

        // --- Create the "Drop" Button ---
        dropButton = new JButton("Drop Selected Section");
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(dropButton);

        // --- Add components to the panel ---
        add(new JLabel("My Registered Sections", SwingConstants.CENTER), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- Add Action Listeners ---
        dropButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performDrop();
            }
        });

        // --- Load the data ---
        loadRegistrationData();
    }

    private void loadRegistrationData() {
        try {
            // Clear old data
            tableModel.setRowCount(0);

            // Get current student's ID
            int studentId = UserSession.getInstance().getCurrentUser().getUserId();

            // 1. Fetch Enrollments (contains IDs needed for dropping)
            this.enrollmentList = studentService.getMyEnrollments(studentId);

            // 2. Fetch Section Details (contains Course Codes needed for display)
            List<Section> mySections = studentService.getMyTimetable(studentId);

            for (Enrollment enr : enrollmentList) {
                // Find the matching section to get the Course Code
                String courseCode = "Unknown"; // Fallback
                for (Section sec : mySections) {
                    if (sec.getSectionId() == enr.getSectionId()) {
                        courseCode = sec.getCourseCode();
                        break;
                    }
                }

                Object[] row = {
                        courseCode,      // Display Course Code (e.g., "CS101")
                        enr.getSectionId(),
                        enr.getStatus()
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading registrations: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performDrop() {
        int selectedRow = registrationsTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a section to drop.",
                    "No Section Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // LOGIC CHANGE:
            // We don't get the ID from the table anymore (because it's not there!).
            // Instead, we get the Enrollment object from our list at the same index.
            Enrollment selectedEnrollment = enrollmentList.get(selectedRow);
            int enrollmentId = selectedEnrollment.getEnrollmentId();

            // Call the "brain"
            studentService.dropSection(enrollmentId);

            // Success!
            JOptionPane.showMessageDialog(this, "Successfully dropped section.",
                    "Drop Successful", JOptionPane.INFORMATION_MESSAGE);

            // Refresh the table
            loadRegistrationData();

        } catch (ServiceException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Drop failed: " + ex.getMessage(),
                    "Drop Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}