package edu.univ.erp.ui.admin;

import edu.univ.erp.domain.Section;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.service.CatalogService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CourseManagementPanel extends JPanel {

    private AdminService adminService;
    private CatalogService catalogService;

    // --- Tab 1 Components (Create Course) ---
    private JTextField courseCodeField, courseTitleField, creditsField;
    private JButton createCourseButton;

    // --- Tab 2 Components (Create Section) ---
    private JTextField secCourseId, secInstId, secRoom, secCap;
    private JButton createSectionButton;
    // Schedule Builder Components
    private JCheckBox[] dayCheckboxes;
    private JTextField[] startFields;
    private JTextField[] endFields;
    private final String[] DAYS = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

    // --- Tab 3 Components (Edit Section) ---
    private JComboBox<String> sectionSelector;
    private JTextField editCourseId, editInstId, editDayTime, editRoom, editCap, editSem, editYear;
    private JButton updateSectionButton;
    private List<Section> allSections;

    public CourseManagementPanel() {
        this.adminService = new AdminService();
        this.catalogService = new CatalogService();

        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();

        // ==================================================
        // TAB 1: CREATE COURSE
        // ==================================================
        JPanel tab1 = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx=0; gbc.gridy=0; tab1.add(new JLabel("Course Code:"), gbc);
        courseCodeField = new JTextField(15);
        gbc.gridx=1; tab1.add(courseCodeField, gbc);

        gbc.gridx=0; gbc.gridy=1; tab1.add(new JLabel("Title:"), gbc);
        courseTitleField = new JTextField(15);
        gbc.gridx=1; tab1.add(courseTitleField, gbc);

        gbc.gridx=0; gbc.gridy=2; tab1.add(new JLabel("Credits:"), gbc);
        creditsField = new JTextField(5);
        gbc.gridx=1; tab1.add(creditsField, gbc);

        createCourseButton = new JButton("Create Course");
        gbc.gridx=1; gbc.gridy=3; tab1.add(createCourseButton, gbc);

        tabbedPane.addTab("Create New Course", tab1);

        // ==================================================
        // TAB 2: CREATE NEW SECTION (With Schedule Builder)
        // ==================================================
        JPanel tab2 = new JPanel(new GridBagLayout());
        // Reusing gbc constraints
        gbc.gridx=0; gbc.gridy=0; tab2.add(new JLabel("Course ID:"), gbc);
        secCourseId = new JTextField(10); gbc.gridx=1; tab2.add(secCourseId, gbc);

        gbc.gridx=0; gbc.gridy=1; tab2.add(new JLabel("Instructor ID:"), gbc);
        secInstId = new JTextField(10); gbc.gridx=1; tab2.add(secInstId, gbc);

        // --- Schedule Builder UI ---
        gbc.gridx=0; gbc.gridy=2; tab2.add(new JLabel("Schedule:"), gbc);

        JPanel schedulePanel = new JPanel(new GridLayout(6, 1, 2, 2));
        dayCheckboxes = new JCheckBox[6];
        startFields = new JTextField[6];
        endFields = new JTextField[6];

        for (int i = 0; i < 6; i++) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

            dayCheckboxes[i] = new JCheckBox(DAYS[i]);
            dayCheckboxes[i].setPreferredSize(new Dimension(55, 25));

            startFields[i] = new JTextField("10:00", 5);
            endFields[i] = new JTextField("10:50", 5);

            startFields[i].setEnabled(false);
            endFields[i].setEnabled(false);

            int finalI = i;
            dayCheckboxes[i].addActionListener(e -> {
                boolean isSelected = dayCheckboxes[finalI].isSelected();
                startFields[finalI].setEnabled(isSelected);
                endFields[finalI].setEnabled(isSelected);
            });

            row.add(dayCheckboxes[i]);
            row.add(startFields[i]);
            row.add(new JLabel("-"));
            row.add(endFields[i]);

            schedulePanel.add(row);
        }
        gbc.gridx=1; gbc.gridy=2; tab2.add(schedulePanel, gbc);
        // ---------------------------

        gbc.gridx=0; gbc.gridy=3; tab2.add(new JLabel("Room:"), gbc);
        secRoom = new JTextField(10); gbc.gridx=1; tab2.add(secRoom, gbc);

        gbc.gridx=0; gbc.gridy=4; tab2.add(new JLabel("Capacity:"), gbc);
        secCap = new JTextField(5); gbc.gridx=1; tab2.add(secCap, gbc);

        createSectionButton = new JButton("Create Section");
        gbc.gridx=1; gbc.gridy=5; tab2.add(createSectionButton, gbc);

        tabbedPane.addTab("Create New Section", tab2);

        // ==================================================
        // TAB 3: EDIT EXISTING SECTION
        // ==================================================
        JPanel tab3 = new JPanel(new GridBagLayout());

        gbc.gridx=0; gbc.gridy=0; tab3.add(new JLabel("Select Section:"), gbc);
        sectionSelector = new JComboBox<>();
        gbc.gridx=1; tab3.add(sectionSelector, gbc);

        gbc.gridx=0; gbc.gridy=1; tab3.add(new JLabel("Course ID:"), gbc);
        editCourseId = new JTextField(10); gbc.gridx=1; tab3.add(editCourseId, gbc);

        gbc.gridx=0; gbc.gridy=2; tab3.add(new JLabel("Instructor ID:"), gbc);
        editInstId = new JTextField(10); gbc.gridx=1; tab3.add(editInstId, gbc);

        gbc.gridx=0; gbc.gridy=3; tab3.add(new JLabel("Day/Time (Text):"), gbc);
        editDayTime = new JTextField(20); // Simple text edit for simplicity
        gbc.gridx=1; tab3.add(editDayTime, gbc);

        gbc.gridx=0; gbc.gridy=4; tab3.add(new JLabel("Room:"), gbc);
        editRoom = new JTextField(10); gbc.gridx=1; tab3.add(editRoom, gbc);

        gbc.gridx=0; gbc.gridy=5; tab3.add(new JLabel("Capacity:"), gbc);
        editCap = new JTextField(5); gbc.gridx=1; tab3.add(editCap, gbc);

        gbc.gridx=0; gbc.gridy=6; tab3.add(new JLabel("Semester:"), gbc);
        editSem = new JTextField("Monsoon", 10); gbc.gridx=1; tab3.add(editSem, gbc);

        gbc.gridx=0; gbc.gridy=7; tab3.add(new JLabel("Year:"), gbc);
        editYear = new JTextField("2025", 5); gbc.gridx=1; tab3.add(editYear, gbc);

        updateSectionButton = new JButton("Update Section");
        gbc.gridx=1; gbc.gridy=8; tab3.add(updateSectionButton, gbc);

        tabbedPane.addTab("Edit Existing Section", tab3);

        add(tabbedPane, BorderLayout.CENTER);

        // --- Listeners ---
        createCourseButton.addActionListener(e -> performCreateCourse());
        createSectionButton.addActionListener(e -> performCreateSection());

        // Refresh dropdown when clicking the Edit Tab
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 2) {
                loadSectionSelector();
            }
        });

        sectionSelector.addActionListener(e -> loadSectionDetails());
        updateSectionButton.addActionListener(e -> performUpdateSection());
    }

    // --- Helper to build the time string from checkboxes ---
    private String buildScheduleString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int i = 0; i < 6; i++) {
            if (dayCheckboxes[i].isSelected()) {
                if (!first) sb.append(", ");
                sb.append(DAYS[i]).append(" ").append(startFields[i].getText())
                        .append("-").append(endFields[i].getText());
                first = false;
            }
        }
        return sb.toString();
    }

    private void performCreateCourse() {
        try {
            String code = courseCodeField.getText();
            String title = courseTitleField.getText();
            int credits = Integer.parseInt(creditsField.getText());
            adminService.createCourse(code, title, credits);
            JOptionPane.showMessageDialog(this, "Course Created!");
            courseCodeField.setText(""); courseTitleField.setText(""); creditsField.setText("");
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void performCreateSection() {
        try {
            int cid = Integer.parseInt(secCourseId.getText());
            int iid = Integer.parseInt(secInstId.getText());
            String dt = buildScheduleString(); // Use the builder!
            String rm = secRoom.getText();
            int cap = Integer.parseInt(secCap.getText());

            if (dt.isEmpty()) { showError("Select at least one day."); return; }

            adminService.createSection(cid, iid, dt, rm, cap);
            JOptionPane.showMessageDialog(this, "Section Created:\n" + dt);

            // Clear inputs
            secCourseId.setText(""); secInstId.setText(""); secRoom.setText(""); secCap.setText("");
            for(int i=0; i<6; i++) {
                dayCheckboxes[i].setSelected(false);
                startFields[i].setEnabled(false);
            }
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void loadSectionSelector() {
        try {
            sectionSelector.removeAllItems();
            allSections = catalogService.getAllSections();
            for (Section s : allSections) {
                sectionSelector.addItem("Sec " + s.getSectionId() + " - " + s.getCourseCode());
            }
        } catch (SQLException e) { showError("Error loading sections"); }
    }

    private void loadSectionDetails() {
        int idx = sectionSelector.getSelectedIndex();
        if (idx == -1 || allSections == null || idx >= allSections.size()) return;

        Section s = allSections.get(idx);
        editCourseId.setText(String.valueOf(s.getCourseId()));
        editInstId.setText(String.valueOf(s.getInstructorId()));
        editDayTime.setText(s.getDayTime());
        editRoom.setText(s.getRoom());
        editCap.setText(String.valueOf(s.getCapacity()));
        editSem.setText(s.getSemester());
        editYear.setText(String.valueOf(s.getYear()));
    }

    private void performUpdateSection() {
        try {
            int idx = sectionSelector.getSelectedIndex();
            if (idx == -1) return;
            int sectionId = allSections.get(idx).getSectionId();

            int cid = Integer.parseInt(editCourseId.getText());
            int iid = Integer.parseInt(editInstId.getText());
            String dt = editDayTime.getText();
            String rm = editRoom.getText();
            int cap = Integer.parseInt(editCap.getText());
            String sem = editSem.getText();
            int yr = Integer.parseInt(editYear.getText());

            adminService.updateSection(sectionId, cid, iid, dt, rm, cap, sem, yr);
            JOptionPane.showMessageDialog(this, "Section Updated!");
            loadSectionSelector();

        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}