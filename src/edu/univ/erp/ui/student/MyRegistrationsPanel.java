package edu.univ.erp.ui.student;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section; // We might need this later
import edu.univ.erp.service.StudentService;
import edu.univ.erp.service.ServiceException; // ========= FIX #1: ADD THIS IMPORT =========

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
    private List<Enrollment> enrollmentList;

    public MyRegistrationsPanel() {
        this.studentService = new StudentService();

        setLayout(new BorderLayout(10, 10));

        // --- Create the Table ---
        // For now, we'll show simple IDs.
        // A better version would join tables to show course names.
        String[] columnNames = {"Enrollment ID", "Section ID", "Status"};
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

            // Call the "brain"
            this.enrollmentList = studentService.getMyEnrollments(studentId);

            for (Enrollment enr : enrollmentList) {
                Object[] row = {
                        enr.getEnrollmentId(),
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
            // Get the ID from the table
            int enrollmentId = (int) tableModel.getValueAt(selectedRow, 0);

            // Call the "brain"
            studentService.dropSection(enrollmentId);

            // Success!
            JOptionPane.showMessageDialog(this, "Successfully dropped section.",
                    "Drop Successful", JOptionPane.INFORMATION_MESSAGE);

            // Refresh the table
            loadRegistrationData();

            // ========= FIX #2: CHANGE THIS CATCH BLOCK =========
        } catch (ServiceException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Drop failed: " + ex.getMessage(),
                    "Drop Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}