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
        String[] columnNames = {"Course Code", "Title", "Day/Time", "Room", "Semester", "Year"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // View-only
            }
        };
        sectionsTable = new JTable(tableModel);

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
                        section.getDayTime(),
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
}