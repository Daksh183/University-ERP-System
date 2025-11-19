package edu.univ.erp.ui.student;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.CatalogService;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.service.ServiceException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class CourseCatalogPanel extends JPanel {

    private JTable catalogTable;
    private DefaultTableModel tableModel;
    private JButton registerButton;

    private CatalogService catalogService;
    private StudentService studentService;
    private List<Section> sectionList;

    public CourseCatalogPanel() {
        this.catalogService = new CatalogService();
        this.studentService = new StudentService();

        setLayout(new BorderLayout(10, 10));

        // --- Create Table ---
        // Final Columns: Removed "Day/Time" and "Room"
        String[] columnNames = {"Course Code", "Title", "Credits", "Instructor", "Capacity"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            // Helper to sort numbers correctly
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if(columnIndex == 2 || columnIndex == 4) return Integer.class;
                return String.class;
            }
        };

        catalogTable = new JTable(tableModel);
        catalogTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(catalogTable);

        registerButton = new JButton("Register for Selected Section");
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(registerButton);

        // Simplified Label
        add(new JLabel("Available Courses", SwingConstants.CENTER), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        registerButton.addActionListener(e -> performRegistration());

        loadCatalogData();
    }

    private void loadCatalogData() {
        try {
            tableModel.setRowCount(0);
            this.sectionList = catalogService.getAllSections();

            for (Section section : sectionList) {
                Object[] row = {
                        section.getCourseCode(),
                        section.getCourseTitle(),
                        section.getCredits(),
                        section.getInstructorName(),
                        // Removed Day/Time and Room
                        section.getCapacity()
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading catalog: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performRegistration() {
        int selectedRow = catalogTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a section.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Section selectedSection = sectionList.get(selectedRow);
            int sectionId = selectedSection.getSectionId();
            int studentId = UserSession.getInstance().getCurrentUser().getUserId();

            studentService.registerForSection(studentId, sectionId);

            JOptionPane.showMessageDialog(this, "Successfully registered for " + selectedSection.getCourseCode(), "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (ServiceException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Registration failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}