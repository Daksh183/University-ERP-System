package edu.univ.erp.ui.student;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.CatalogService;
import edu.univ.erp.service.StudentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class CourseCatalogPanel extends JPanel {

    // 1. Declare UI components
    private JTable catalogTable;
    private DefaultTableModel tableModel;
    private JButton registerButton;

    // 2. Declare our "brain" services
    private CatalogService catalogService;
    private StudentService studentService;

    // 3. Store the data from the DB
    private List<Section> sectionList;

    public CourseCatalogPanel() {
        // Initialize services
        this.catalogService = new CatalogService();
        this.studentService = new StudentService();

        // Set the layout for this panel
        setLayout(new BorderLayout(10, 10)); // 10px padding

        // --- 4. Create the Table ---
        String[] columnNames = {"Course Code", "Title", "Instructor", "Day/Time", "Room", "Capacity"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            // Make the table non-editable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        catalogTable = new JTable(tableModel);
        catalogTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Only one row at a time

        // Put the table inside a JScrollPane so we can scroll
        JScrollPane scrollPane = new JScrollPane(catalogTable);

        // --- 5. Create the "Register" Button ---
        registerButton = new JButton("Register for Selected Section");

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(registerButton);

        // --- 6. Add components to the panel ---
        add(new JLabel("Available Courses", SwingConstants.CENTER), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- 7. Add Action Listener for the button ---
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRegistration();
            }
        });

        // --- 8. Load the data ---
        loadCatalogData();
    }

    /**
     * Fetches data from the CatalogService and populates the JTable.
     */
    private void loadCatalogData() {
        try {
            // Clear any old data
            tableModel.setRowCount(0);

            // Call the "brain"
            this.sectionList = catalogService.getAllSections();

            // Loop through the results and add them to the table
            for (Section section : sectionList) {
                Object[] row = {
                        section.getCourseCode(),
                        section.getCourseTitle(),
                        section.getInstructorName(),
                        section.getDayTime(),
                        section.getRoom(),
                        section.getCapacity()
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading course catalog: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Called when the "Register" button is clicked.
     */
    private void performRegistration() {
        // 1. Get selected row
        int selectedRow = catalogTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a section from the table first.",
                    "No Section Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 2. Get the corresponding Section object
            Section selectedSection = sectionList.get(selectedRow);
            int sectionId = selectedSection.getSectionId();

            // 3. Get the current student's ID
            int studentId = UserSession.getInstance().getCurrentUser().getUserId();

            // 4. Call the "brain" to register
            studentService.registerForSection(studentId, sectionId);

            // 5. Success!
            JOptionPane.showMessageDialog(this, "Successfully registered for " + selectedSection.getCourseCode() + "!",
                    "Registration Successful", JOptionPane.INFORMATION_MESSAGE);

        } catch (StudentService.ServiceException | SQLException ex) {
            // 6. Handle errors (like "Already registered")
            JOptionPane.showMessageDialog(this, "Registration failed: " + ex.getMessage(),
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}