package edu.univ.erp.ui.student;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.StudentService;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class TimetablePanel extends JPanel {

    private JTable timetableTable;
    private DefaultTableModel tableModel;
    private StudentService studentService;

    public TimetablePanel() {
        this.studentService = new StudentService();
        setLayout(new BorderLayout(10, 10));

        // --- Create the Table ---
        String[] columnNames = {"Course Code", "Title", "Day/Time", "Room", "Instructor"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // View-only
            }
        };
        timetableTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(timetableTable);

        // --- Add components to the panel ---
        add(new JLabel("My Weekly Timetable", SwingConstants.CENTER), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // --- Load the data ---
        loadTimetableData();
    }

    private void loadTimetableData() {
        try {
            // Clear old data
            tableModel.setRowCount(0);

            // Get current student's ID
            int studentId = UserSession.getInstance().getCurrentUser().getUserId();

            // Call the "brain"
            List<Section> sections = studentService.getMyTimetable(studentId);

            for (Section section : sections) {
                Object[] row = {
                        section.getCourseCode(),
                        section.getCourseTitle(),
                        section.getDayTime(),
                        section.getRoom(),
                        section.getInstructorName()
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading timetable: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}