package edu.univ.erp.ui.student;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.StudentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MyGradesPanel extends JPanel {

    private JComboBox<String> courseSelector;
    private JTable gradesTable;
    private DefaultTableModel tableModel;

    private JLabel currentScoreLabel;
    private JLabel finalGradeLabel;

    private List<Enrollment> myEnrollments;
    private List<Section> mySections;
    private StudentService studentService;

    public MyGradesPanel() {
        this.studentService = new StudentService();
        setLayout(new BorderLayout(10, 10));

        // --- Top: Course Selector ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Course to View:"));
        courseSelector = new JComboBox<>();
        topPanel.add(courseSelector);
        add(topPanel, BorderLayout.NORTH);

        // --- Center: Table ---
        // Updated Columns for clarity
        String[] columnNames = {"Component", "Marks Obtained / Max", "Weightage"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        gradesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(gradesTable);
        add(scrollPane, BorderLayout.CENTER);

        // --- Bottom: Summary ---
        JPanel summaryPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Weighted Grade Summary"));

        currentScoreLabel = new JLabel("Current Cumulative Grade: -");
        currentScoreLabel.setFont(new Font("Arial", Font.BOLD, 15));

        finalGradeLabel = new JLabel("Official Final Grade: Pending");
        finalGradeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        finalGradeLabel.setForeground(Color.BLUE);

        summaryPanel.add(currentScoreLabel);
        summaryPanel.add(finalGradeLabel);
        add(summaryPanel, BorderLayout.SOUTH);

        loadCourseSelector();
        courseSelector.addActionListener(e -> loadGradesForSelectedCourse());
    }

    private void loadCourseSelector() {
        try {
            int studentId = UserSession.getInstance().getCurrentUser().getUserId();
            myEnrollments = studentService.getMyEnrollments(studentId);
            mySections = studentService.getMyTimetable(studentId);

            courseSelector.removeAllItems();
            for (Enrollment enrollment : myEnrollments) {
                String displayName = "Section ID: " + enrollment.getSectionId();
                for (Section section : mySections) {
                    if (section.getSectionId() == enrollment.getSectionId()) {
                        displayName = section.getCourseCode() + " - " + section.getCourseTitle();
                        break;
                    }
                }
                courseSelector.addItem(displayName);
            }
        } catch (SQLException e) { }
    }

    private void loadGradesForSelectedCourse() {
        int idx = courseSelector.getSelectedIndex();
        if (idx == -1) return;

        Enrollment selectedEnrollment = myEnrollments.get(idx);

        try {
            tableModel.setRowCount(0);
            List<Grade> grades = studentService.getGrades(selectedEnrollment.getEnrollmentId());

            double totalEarnedWeight = 0.0;
            double totalPossibleWeight = 0.0;
            String finalGradeStatus = "Pending";

            for (Grade grade : grades) {
                // 1. Format the "Marks" column: "30.0 / 60.0"
                String marksDisplay = grade.getScore() + " / " + grade.getMaxMarks();

                // 2. Format Weightage: "30.0%"
                String weightDisplay = grade.getWeightage() + "%";

                tableModel.addRow(new Object[]{
                        grade.getComponent(),
                        marksDisplay,
                        weightDisplay
                });

                // 3. Calculate Weighted Contribution
                // Formula: (Score / Max) * Weightage
                if (grade.getMaxMarks() > 0) {
                    double percentage = grade.getScore() / grade.getMaxMarks();
                    double earnedWeight = percentage * grade.getWeightage();

                    totalEarnedWeight += earnedWeight;
                    totalPossibleWeight += grade.getWeightage();
                }

                if (grade.getFinalGrade() != null && !grade.getFinalGrade().isEmpty()) {
                    finalGradeStatus = grade.getFinalGrade();
                }
            }

            // 4. Display the result: "25.0 / 50.0"
            currentScoreLabel.setText(String.format("Current Cumulative Grade: %.2f / %.2f", totalEarnedWeight, totalPossibleWeight));

            if ("Pending".equals(finalGradeStatus)) {
                finalGradeLabel.setText("Official Final Grade: Pending");
                finalGradeLabel.setForeground(Color.GRAY);
            } else {
                finalGradeLabel.setText("Official Final Grade: " + finalGradeStatus);
                finalGradeLabel.setForeground(new Color(0, 128, 0));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}