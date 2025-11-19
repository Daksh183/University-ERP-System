package edu.univ.erp.ui.instructor;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.InstructorService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MySectionsPanel extends JPanel {

    private JTable sectionsTable;
    private DefaultTableModel tableModel;
    private InstructorService instructorService;

    public MySectionsPanel() {
        this.instructorService = new InstructorService();
        setLayout(new BorderLayout(10, 10));

        // --- Create the Table ---
        String[] columnNames = {"Course Code", "Title", "Schedule (Day/Time)", "Room", "Semester", "Year"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // View-only
            }
        };
        sectionsTable = new JTable(tableModel);

        // KEY CHANGE: Increase row height to fit multiple lines (same as Student Timetable)
        sectionsTable.setRowHeight(60);

        JScrollPane scrollPane = new JScrollPane(sectionsTable);

        // --- Add components to the panel ---
        add(new JLabel("My Assigned Sections", SwingConstants.CENTER), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // --- Load the data ---
        loadSectionsData();
    }

    private void loadSectionsData() {
        try {
            // Clear old data
            tableModel.setRowCount(0);

            // Get current instructor's ID from the session
            int instructorId = UserSession.getInstance().getCurrentUser().getUserId();

            // Call the "brain"
            List<Section> sections = instructorService.getMySections(instructorId);

            // Populate the table
            for (Section section : sections) {
                Object[] row = {
                        section.getCourseCode(),
                        section.getCourseTitle(),
                        formatDayTimeHTML(section.getDayTime()), // <--- Use HTML Formatter
                        section.getRoom(),
                        section.getSemester(),
                        section.getYear()
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading assigned sections: " + e.getMessage(),
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
                sb.append(raw); // Fallback
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