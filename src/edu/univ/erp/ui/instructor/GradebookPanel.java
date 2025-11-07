package edu.univ.erp.ui.instructor;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.Student;
import edu.univ.erp.service.InstructorService;
import edu.univ.erp.service.StudentService;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradebookPanel extends JPanel {

    // Services
    private InstructorService instructorService;
    private StudentService studentService; // To get enrollment IDs

    // Data storage
    private List<Section> mySections;
    private List<Student> studentsInSection;
    private List<Enrollment> enrollments;
    private Map<Integer, Integer> studentToEnrollmentMap; // Maps studentId -> enrollmentId

    // UI Components
    private JComboBox<String> sectionSelector;
    private JTable studentTable;
    private DefaultTableModel studentTableModel;
    private JTable gradeTable;
    private DefaultTableModel gradeTableModel;
    private JTextField componentField;
    private JTextField scoreField;
    private JButton saveGradeButton;
    private JPanel gradeEntryPanel;

    public GradebookPanel() {
        this.instructorService = new InstructorService();
        this.studentService = new StudentService();
        this.studentToEnrollmentMap = new HashMap<>();

        setLayout(new BorderLayout(10, 10));

        // --- 1. Top Panel: Section Selector ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Section:"));
        sectionSelector = new JComboBox<>();
        topPanel.add(sectionSelector);

        // --- 2. Center Panel: Split Pane (Students | Grades) ---
        // Student Table (Left)
        String[] studentCols = {"Student ID", "Roll No", "Name (from username)"};
        studentTableModel = new DefaultTableModel(studentCols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        studentTable = new JTable(studentTableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane studentScrollPane = new JScrollPane(studentTable);

        // Grade Panel (Right)
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        String[] gradeCols = {"Component", "Score"};
        gradeTableModel = new DefaultTableModel(gradeCols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        gradeTable = new JTable(gradeTableModel);
        JScrollPane gradeScrollPane = new JScrollPane(gradeTable);

        // Grade Entry Form
        gradeEntryPanel = new JPanel(new FlowLayout());
        gradeEntryPanel.add(new JLabel("Component:"));
        componentField = new JTextField(10);
        gradeEntryPanel.add(componentField);
        gradeEntryPanel.add(new JLabel("Score:"));
        scoreField = new JTextField(5);
        gradeEntryPanel.add(scoreField);
        saveGradeButton = new JButton("Save Grade");
        gradeEntryPanel.add(saveGradeButton);
        gradeEntryPanel.setVisible(false); // Hide until a student is selected

        rightPanel.add(new JLabel("Grades for Selected Student", SwingConstants.CENTER), BorderLayout.NORTH);
        rightPanel.add(gradeScrollPane, BorderLayout.CENTER);
        rightPanel.add(gradeEntryPanel, BorderLayout.SOUTH);

        // Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, studentScrollPane, rightPanel);
        splitPane.setDividerLocation(300); // Initial divider position

        // --- Add main components to the panel ---
        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        // --- Load Initial Data ---
        loadSectionSelector();

        // --- Action Listeners ---
        sectionSelector.addActionListener(e -> loadStudentsForSelectedSection());
        studentTable.getSelectionModel().addListSelectionListener(e -> loadGradesForSelectedStudent());
        saveGradeButton.addActionListener(e -> performSaveGrade());
    }

    private void loadSectionSelector() {
        try {
            int instructorId = UserSession.getInstance().getCurrentUser().getUserId();
            mySections = instructorService.getMySections(instructorId);

            sectionSelector.removeAllItems(); // Clear old items
            for (Section s : mySections) {
                sectionSelector.addItem(s.getSectionId() + ": " + s.getCourseCode() + " - " + s.getCourseTitle());
            }
        } catch (SQLException e) {
            showError("Error loading your sections: " + e.getMessage());
        }
    }

    private void loadStudentsForSelectedSection() {
        int selectedIndex = sectionSelector.getSelectedIndex();
        if (selectedIndex == -1) return;

        Section selectedSection = mySections.get(selectedIndex);
        studentTableModel.setRowCount(0); // Clear student table
        gradeTableModel.setRowCount(0); // Clear grade table
        gradeEntryPanel.setVisible(false); // Hide grade form
        studentToEnrollmentMap.clear();

        try {
            // Get the list of Student objects
            studentsInSection = instructorService.getStudentsBySection(selectedSection.getSectionId());
            // Get the list of Enrollment objects (to map studentId -> enrollmentId)
            enrollments = studentService.getMyEnrollments(0); // HACK: This is inefficient.
            // We really need a new DAO method: getEnrollmentsBySectionId(sectionId)
            // For now, let's just find the student we need.

            for (Student s : studentsInSection) {
                studentTableModel.addRow(new Object[]{s.getUserId(), s.getRollNo(), "stu" + s.getUserId()}); // Assuming username is like stuX
            }
        } catch (SQLException e) {
            showError("Error loading students: " + e.getMessage());
        }
    }

    private void loadGradesForSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            gradeEntryPanel.setVisible(false);
            return;
        }

        gradeTableModel.setRowCount(0);
        gradeEntryPanel.setVisible(true); // Show the form

        try {
            Student selectedStudent = studentsInSection.get(selectedRow);

            // We need the enrollmentId. We must find it.
            // This is a bit of a hack because our service isn't perfect.
            // A better DAO would be getEnrollment(studentId, sectionId)
            int enrollmentId = -1;
            List<Enrollment> allEnrollments = studentService.getMyEnrollments(selectedStudent.getUserId());
            for(Enrollment enr : allEnrollments) {
                if (enr.getSectionId() == mySections.get(sectionSelector.getSelectedIndex()).getSectionId()) {
                    enrollmentId = enr.getEnrollmentId();
                    break;
                }
            }

            if (enrollmentId == -1) {
                showError("Could not find enrollment for this student.");
                return;
            }

            // Store this for the save button
            studentToEnrollmentMap.put(selectedStudent.getUserId(), enrollmentId);

            // Now load the grades
            List<Grade> grades = instructorService.getGradesForEnrollment(enrollmentId);
            for (Grade g : grades) {
                gradeTableModel.addRow(new Object[]{g.getComponent(), g.getScore()});
            }

        } catch (SQLException e) {
            showError("Error loading grades: " + e.getMessage());
        }
    }

    private void performSaveGrade() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) return;

        String component = componentField.getText();
        String scoreText = scoreField.getText();

        if (component.isEmpty() || scoreText.isEmpty()) {
            showError("Component and Score cannot be empty.");
            return;
        }

        try {
            double score = Double.parseDouble(scoreText);
            Student selectedStudent = studentsInSection.get(selectedRow);
            int enrollmentId = studentToEnrollmentMap.get(selectedStudent.getUserId());

            // Call the "brain"
            instructorService.submitGrade(enrollmentId, component, score);

            // Success!
            JOptionPane.showMessageDialog(this, "Grade saved successfully!");
            componentField.setText("");
            scoreField.setText("");
            loadGradesForSelectedStudent(); // Refresh the grade table

        } catch (NumberFormatException e) {
            showError("Score must be a valid number.");
        } catch (SQLException e) {
            showError("Error saving grade: " + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}