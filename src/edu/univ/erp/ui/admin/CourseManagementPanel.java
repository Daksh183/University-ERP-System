package edu.univ.erp.ui.admin;

import edu.univ.erp.domain.Section;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.service.CatalogService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class CourseManagementPanel extends JPanel {

    private AdminService adminService;
    private CatalogService catalogService;

    // --- Tab 1 Components (Create Course) ---
    private JTextField courseCodeField, courseTitleField, creditsField;
    private JButton createCourseButton;

    // --- Tab 2 Components (Create Section) ---
    private JTextField secCourseId, secInstUsername, secRoom, secCap;
    private JButton createSectionButton;
    // Create Schedule Components
    private JCheckBox[] createCheckboxes;
    private JTextField[] createStartFields, createEndFields;

    // --- Tab 3 Components (Edit Section) ---
    private JComboBox<String> sectionSelector;
    private JTextField editCourseId, editInstUsername, editRoom, editCap, editSem, editYear;
    private JButton updateSectionButton;
    private List<Section> allSections;
    // Edit Schedule Components
    private JCheckBox[] editCheckboxes;
    private JTextField[] editStartFields, editEndFields;

    private final String[] DAYS = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

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
        gbc.gridx=0; gbc.gridy=0; tab2.add(new JLabel("Course ID:"), gbc);
        secCourseId = new JTextField(10); gbc.gridx=1; tab2.add(secCourseId, gbc);

        gbc.gridx=0; gbc.gridy=1; tab2.add(new JLabel("Instructor Username:"), gbc);
        secInstUsername = new JTextField(10); gbc.gridx=1; tab2.add(secInstUsername, gbc);

        // --- Schedule Builder (Create) ---
        gbc.gridx=0; gbc.gridy=2; tab2.add(new JLabel("Schedule:"), gbc);

        JPanel createSchedPanel = new JPanel(new GridLayout(6, 1, 2, 2));
        createCheckboxes = new JCheckBox[6];
        createStartFields = new JTextField[6];
        createEndFields = new JTextField[6];
        initScheduleComponents(createSchedPanel, createCheckboxes, createStartFields, createEndFields);

        gbc.gridx=1; gbc.gridy=2; tab2.add(createSchedPanel, gbc);

        gbc.gridx=0; gbc.gridy=3; tab2.add(new JLabel("Room:"), gbc);
        secRoom = new JTextField(10); gbc.gridx=1; tab2.add(secRoom, gbc);

        gbc.gridx=0; gbc.gridy=4; tab2.add(new JLabel("Capacity:"), gbc);
        secCap = new JTextField(5); gbc.gridx=1; tab2.add(secCap, gbc);

        createSectionButton = new JButton("Create Section");
        gbc.gridx=1; gbc.gridy=5; tab2.add(createSectionButton, gbc);

        tabbedPane.addTab("Create New Section", tab2);

        // ==================================================
        // TAB 3: EDIT EXISTING SECTION (With Schedule Builder)
        // ==================================================
        JPanel tab3 = new JPanel(new GridBagLayout());

        gbc.gridx=0; gbc.gridy=0; tab3.add(new JLabel("Select Section:"), gbc);
        sectionSelector = new JComboBox<>();
        gbc.gridx=1; tab3.add(sectionSelector, gbc);

        gbc.gridx=0; gbc.gridy=1; tab3.add(new JLabel("Course ID:"), gbc);
        editCourseId = new JTextField(10); gbc.gridx=1; tab3.add(editCourseId, gbc);

        gbc.gridx=0; gbc.gridy=2; tab3.add(new JLabel("Instructor Username:"), gbc);
        editInstUsername = new JTextField(10); gbc.gridx=1; tab3.add(editInstUsername, gbc);

        // --- Schedule Builder (Edit) ---
        gbc.gridx=0; gbc.gridy=3; tab3.add(new JLabel("Schedule:"), gbc);

        JPanel editSchedPanel = new JPanel(new GridLayout(6, 1, 2, 2));
        editCheckboxes = new JCheckBox[6];
        editStartFields = new JTextField[6];
        editEndFields = new JTextField[6];
        initScheduleComponents(editSchedPanel, editCheckboxes, editStartFields, editEndFields);

        gbc.gridx=1; gbc.gridy=3; tab3.add(editSchedPanel, gbc);
        // -------------------------------

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

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 2) {
                loadSectionSelector();
            }
        });

        sectionSelector.addActionListener(e -> loadSectionDetails());
        updateSectionButton.addActionListener(e -> performUpdateSection());
    }

    // Helper to create the 6-row schedule UI
    private void initScheduleComponents(JPanel panel, JCheckBox[] checks, JTextField[] starts, JTextField[] ends) {
        for (int i = 0; i < 6; i++) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            checks[i] = new JCheckBox(DAYS[i]);
            checks[i].setPreferredSize(new Dimension(55, 25));

            starts[i] = new JTextField("10:00", 5);
            ends[i] = new JTextField("10:50", 5);

            starts[i].setEnabled(false);
            ends[i].setEnabled(false);

            int finalI = i;
            checks[i].addActionListener(e -> {
                boolean isSelected = checks[finalI].isSelected();
                starts[finalI].setEnabled(isSelected);
                ends[finalI].setEnabled(isSelected);
            });

            row.add(checks[i]);
            row.add(starts[i]);
            row.add(new JLabel("-"));
            row.add(ends[i]);
            panel.add(row);
        }
    }

    // Helper to turn Checkboxes -> String "Mon 10:00-11:00, ..."
    private String buildScheduleString(JCheckBox[] checks, JTextField[] starts, JTextField[] ends) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int i = 0; i < 6; i++) {
            if (checks[i].isSelected()) {
                if (!first) sb.append(", ");
                sb.append(DAYS[i]).append(" ").append(starts[i].getText())
                        .append("-").append(ends[i].getText());
                first = false;
            }
        }
        return sb.toString();
    }

    // --- NEW SMART PARSER: Handles "MWF" AND "Mon 10:00..." ---
    private void parseScheduleStringToUI(String schedule) {
        // 1. Reset all fields first
        for(int i=0; i<6; i++) {
            editCheckboxes[i].setSelected(false);
            editStartFields[i].setEnabled(false);
            editEndFields[i].setEnabled(false);
            editStartFields[i].setText("10:00");
            editEndFields[i].setText("10:50");
        }

        if(schedule == null || schedule.isEmpty()) return;

        // CASE 1: New Format (Comma Separated) -> "Mon 10:00-10:50, Wed..."
        if (schedule.contains(",")) {
            String[] daySegments = schedule.split(",");
            for (String segment : daySegments) {
                segment = segment.trim();
                String[] parts = segment.split(" "); // ["Mon", "10:00-10:50"]
                if(parts.length < 2) continue;

                String day = parts[0];
                String timeRange = parts[1];
                String[] times = timeRange.split("-");
                String start = times.length > 0 ? times[0] : "";
                String end = times.length > 1 ? times[1] : "";

                setDayActive(day, start, end);
            }
        }
        // CASE 2: Old Format -> "MWF 10:00-10:50"
        else {
            String[] parts = schedule.split(" ", 2);
            if(parts.length >= 2) {
                String days = parts[0]; // "MWF"
                String timeRange = parts[1]; // "10:00-10:50"
                String[] times = timeRange.split("-");
                String start = times.length > 0 ? times[0] : "";
                String end = times.length > 1 ? times[1] : "";

                if (days.contains("M")) setDayActive("Mon", start, end);
                if (days.contains("Th")) { setDayActive("Thu", start, end); days = days.replace("Th", ""); }
                if (days.contains("T")) setDayActive("Tue", start, end);
                if (days.contains("W")) setDayActive("Wed", start, end);
                if (days.contains("F")) setDayActive("Fri", start, end);
                if (days.contains("S")) setDayActive("Sat", start, end);
            }
        }
    }

    private void setDayActive(String dayName, String start, String end) {
        for(int i=0; i<DAYS.length; i++) {
            if(DAYS[i].equals(dayName)) {
                editCheckboxes[i].setSelected(true);
                editStartFields[i].setEnabled(true);
                editEndFields[i].setEnabled(true);
                editStartFields[i].setText(start);
                editEndFields[i].setText(end);
                break;
            }
        }
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
            String instUser = secInstUsername.getText();
            String rm = secRoom.getText();
            int cap = Integer.parseInt(secCap.getText());
            String dt = buildScheduleString(createCheckboxes, createStartFields, createEndFields);

            if (dt.isEmpty()) { showError("Select at least one day."); return; }

            adminService.createSection(cid, instUser, dt, rm, cap);
            JOptionPane.showMessageDialog(this, "Section Created!");

            secCourseId.setText(""); secInstUsername.setText(""); secRoom.setText(""); secCap.setText("");
            for(int i=0; i<6; i++) {
                createCheckboxes[i].setSelected(false);
                createStartFields[i].setEnabled(false);
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
        editInstUsername.setText(s.getInstructorName());
        editRoom.setText(s.getRoom());
        editCap.setText(String.valueOf(s.getCapacity()));
        editSem.setText(s.getSemester());
        editYear.setText(String.valueOf(s.getYear()));

        // --- Populate the Schedule Builder ---
        parseScheduleStringToUI(s.getDayTime());
    }

    private void performUpdateSection() {
        try {
            int idx = sectionSelector.getSelectedIndex();
            if (idx == -1) return;
            int sectionId = allSections.get(idx).getSectionId();

            int cid = Integer.parseInt(editCourseId.getText());
            String instUser = editInstUsername.getText();
            String rm = editRoom.getText();
            int cap = Integer.parseInt(editCap.getText());
            String sem = editSem.getText();
            int yr = Integer.parseInt(editYear.getText());

            String dt = buildScheduleString(editCheckboxes, editStartFields, editEndFields);
            if (dt.isEmpty()) { showError("Select at least one day."); return; }

            adminService.updateSection(sectionId, cid, instUser, dt, rm, cap, sem, yr);
            JOptionPane.showMessageDialog(this, "Section Updated!");
            loadSectionSelector();

        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}