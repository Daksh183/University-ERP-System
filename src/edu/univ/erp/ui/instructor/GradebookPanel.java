package edu.univ.erp.ui.instructor;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.Student;
import edu.univ.erp.service.InstructorService;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.service.ServiceException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradebookPanel extends JPanel {

    private InstructorService instructorService;
    private StudentService studentService;

    private List<Section> mySections;
    private List<Student> studentsInSection;
    private Map<Integer, Integer> studentToEnrollmentMap;

    private JComboBox<String> sectionSelector;
    private JTable studentTable;
    private DefaultTableModel studentTableModel;

    private JTable gradeTable;
    private DefaultTableModel gradeTableModel;

    private JTextField componentField;
    private JTextField scoreField;
    private JTextField maxMarksField;
    private JTextField weightageField;
    private JButton saveGradeButton;
    private JButton publishButton;
    private JPanel gradeEntryPanel;

    public GradebookPanel() {
        this.instructorService = new InstructorService();
        this.studentService = new StudentService();
        this.studentToEnrollmentMap = new HashMap<>();

        setLayout(new BorderLayout(10, 10));

        // --- Top: Section Selector ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Section:"));
        sectionSelector = new JComboBox<>();
        topPanel.add(sectionSelector);

        // --- Center: Split Pane ---
        // Left: Students
        String[] studentCols = {"Student ID", "Roll No", "Name"};
        studentTableModel = new DefaultTableModel(studentCols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        studentTable = new JTable(studentTableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane studentScroll = new JScrollPane(studentTable);

        // Right: Grades
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));

        // REMOVED "Status" (Final Grade) column
        String[] gradeCols = {"Component", "Score", "Max Marks", "Weightage %"};
        gradeTableModel = new DefaultTableModel(gradeCols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        gradeTable = new JTable(gradeTableModel);
        JScrollPane gradeScroll = new JScrollPane(gradeTable);

        // --- Grade Entry Form ---
        gradeEntryPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        gradeEntryPanel.setBorder(BorderFactory.createTitledBorder("Manage Grades"));

        gradeEntryPanel.add(new JLabel("Component Name:"));
        componentField = new JTextField();
        gradeEntryPanel.add(componentField);

        gradeEntryPanel.add(new JLabel("Score Obtained:"));
        scoreField = new JTextField();
        gradeEntryPanel.add(scoreField);

        gradeEntryPanel.add(new JLabel("Max Marks (e.g. 50):"));
        maxMarksField = new JTextField();
        gradeEntryPanel.add(maxMarksField);

        gradeEntryPanel.add(new JLabel("Weightage % (e.g. 20):"));
        weightageField = new JTextField();
        gradeEntryPanel.add(weightageField);

        gradeEntryPanel.add(new JLabel(""));
        saveGradeButton = new JButton("Save Grade");
        gradeEntryPanel.add(saveGradeButton);

        gradeEntryPanel.add(new JLabel(""));
        publishButton = new JButton("Compute & Publish Final Grade");
        publishButton.setBackground(new Color(220, 255, 220));
        gradeEntryPanel.add(publishButton);

        gradeEntryPanel.setVisible(false);

        rightPanel.add(new JLabel("Grades for Selected Student", SwingConstants.CENTER), BorderLayout.NORTH);
        rightPanel.add(gradeScroll, BorderLayout.CENTER);
        rightPanel.add(gradeEntryPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, studentScroll, rightPanel);
        splitPane.setDividerLocation(300);

        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        // Listeners
        loadSectionSelector();
        sectionSelector.addActionListener(e -> loadStudentsForSelectedSection());
        studentTable.getSelectionModel().addListSelectionListener(e -> loadGradesForSelectedStudent());
        saveGradeButton.addActionListener(e -> performSaveGrade());
        publishButton.addActionListener(e -> performPublishGrade());
    }

    private void loadSectionSelector() {
        try {
            int instructorId = UserSession.getInstance().getCurrentUser().getUserId();
            mySections = instructorService.getMySections(instructorId);
            sectionSelector.removeAllItems();
            for (Section s : mySections) {
                sectionSelector.addItem(s.getSectionId() + ": " + s.getCourseCode());
            }
        } catch (SQLException e) { showError("Error loading sections: " + e.getMessage()); }
    }

    private void loadStudentsForSelectedSection() {
        int idx = sectionSelector.getSelectedIndex();
        if (idx == -1) return;
        Section sec = mySections.get(idx);

        studentTableModel.setRowCount(0);
        gradeTableModel.setRowCount(0);
        gradeEntryPanel.setVisible(false);
        studentToEnrollmentMap.clear();

        try {
            studentsInSection = instructorService.getStudentsBySection(sec.getSectionId());
            for (Student s : studentsInSection) {
                studentTableModel.addRow(new Object[]{s.getUserId(), s.getRollNo(), s.getUsername()});
            }
        } catch (SQLException e) { showError("Error loading students: " + e.getMessage()); }
    }

    private void loadGradesForSelectedStudent() {
        int row = studentTable.getSelectedRow();
        if (row == -1) {
            gradeEntryPanel.setVisible(false);
            return;
        }

        gradeTableModel.setRowCount(0);
        gradeEntryPanel.setVisible(true);

        try {
            Student s = studentsInSection.get(row);

            int enrollmentId = -1;
            List<Enrollment> enrs = studentService.getMyEnrollments(s.getUserId());
            int currentSecId = mySections.get(sectionSelector.getSelectedIndex()).getSectionId();

            for(Enrollment e : enrs) {
                if(e.getSectionId() == currentSecId) {
                    enrollmentId = e.getEnrollmentId();
                    break;
                }
            }

            if(enrollmentId == -1) { showError("Enrollment not found"); return; }
            studentToEnrollmentMap.put(s.getUserId(), enrollmentId);

            List<Grade> grades = instructorService.getGradesForEnrollment(enrollmentId);
            for (Grade g : grades) {
                gradeTableModel.addRow(new Object[]{
                        g.getComponent(),
                        g.getScore(),
                        g.getMaxMarks(),
                        g.getWeightage()
                        // Removed Final Grade from row
                });
            }

        } catch (SQLException e) { showError("Error: " + e.getMessage()); }
    }

    private void performSaveGrade() {
        int row = studentTable.getSelectedRow();
        if (row == -1) return;
        try {
            String component = componentField.getText();
            double score = Double.parseDouble(scoreField.getText());
            double maxMarks = Double.parseDouble(maxMarksField.getText());
            double weightage = Double.parseDouble(weightageField.getText());
            if (component.isEmpty()) { showError("Component name required"); return; }
            Student s = studentsInSection.get(row);
            int enrollmentId = studentToEnrollmentMap.get(s.getUserId());
            instructorService.submitGrade(enrollmentId, component, score, maxMarks, weightage);
            JOptionPane.showMessageDialog(this, "Grade Saved!");
            componentField.setText(""); scoreField.setText("");
            maxMarksField.setText(""); weightageField.setText("");
            loadGradesForSelectedStudent();
        } catch (NumberFormatException e) { showError("Numbers required."); }
        catch (SQLException | ServiceException e) { showError("Error: " + e.getMessage()); }
    }

    private void performPublishGrade() {
        int row = studentTable.getSelectedRow();
        if (row == -1) return;

        String input = JOptionPane.showInputDialog(this, "Enter Passing Percentage (e.g. 40):");
        if (input == null || input.isEmpty()) return;

        try {
            double passingThreshold = Double.parseDouble(input);
            Student s = studentsInSection.get(row);
            int enrollmentId = studentToEnrollmentMap.get(s.getUserId());

            String finalStatus = instructorService.computeAndPublishFinalGrade(enrollmentId, passingThreshold);

            JOptionPane.showMessageDialog(this, "Final Status Published: " + finalStatus);
            loadGradesForSelectedStudent();

        } catch (NumberFormatException e) {
            showError("Please enter a valid number for passing percentage.");
        } catch (SQLException | ServiceException e) {
            showError("Error publishing: " + e.getMessage());
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}