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
        String[] columnNames = {"Course Code", "Title", "Schedule", "Room", "Instructor"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // View-only
            }
        };
        timetableTable = new JTable(tableModel);

        // Increase row height to fit multiple lines
        timetableTable.setRowHeight(60);

        JScrollPane scrollPane = new JScrollPane(timetableTable);

        add(new JLabel("My Weekly Timetable", SwingConstants.CENTER), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        loadTimetableData();
    }

    private void loadTimetableData() {
        try {
            tableModel.setRowCount(0);
            int studentId = UserSession.getInstance().getCurrentUser().getUserId();
            List<Section> sections = studentService.getMyTimetable(studentId);

            for (Section section : sections) {
                Object[] row = {
                        section.getCourseCode(),
                        section.getCourseTitle(),
                        formatDayTimeHTML(section.getDayTime()), // Use Smart HTML formatter
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

    /**
     * Helper to convert diverse time formats into HTML with line breaks.
     * Handles both:
     * 1. New Format: "Mon 10:00-11:00, Wed 14:00-15:00"
     * 2. Old Format: "MWF 10:00-10:50"
     */
    private String formatDayTimeHTML(String raw) {
        if (raw == null || raw.isEmpty()) return "";

        StringBuilder sb = new StringBuilder("<html>");

        // Case 1: New Format (Comma Separated)
        if (raw.contains(",")) {
            String[] parts = raw.split(",");
            for (String part : parts) {
                sb.append(part.trim()).append("<br>");
            }
        }
        // Case 2: Old Format (MWF 10:00)
        else {
            // Try to split "Days" from "Time"
            String[] parts = raw.split(" ", 2);
            if (parts.length < 2) {
                sb.append(raw); // Fallback if format is unknown
            } else {
                String days = parts[0];
                String time = parts[1];

                if (days.contains("M")) sb.append("Mon: ").append(time).append("<br>");

                if (days.contains("Th")) {
                    sb.append("Thu: ").append(time).append("<br>");
                    days = days.replace("Th", "");
                }

                if (days.contains("T")) sb.append("Tue: ").append(time).append("<br>");
                if (days.contains("W")) sb.append("Wed: ").append(time).append("<br>");
                if (days.contains("F")) sb.append("Fri: ").append(time).append("<br>");
                if (days.contains("S")) sb.append("Sat: ").append(time).append("<br>");
            }
        }

        sb.append("</html>");
        return sb.toString();
    }
}