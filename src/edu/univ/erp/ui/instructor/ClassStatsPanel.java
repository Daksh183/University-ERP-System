package edu.univ.erp.ui.instructor;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.Student;
import edu.univ.erp.service.InstructorService;
import edu.univ.erp.service.StudentService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class ClassStatsPanel extends JPanel {

    private InstructorService instructorService;
    private StudentService studentService;
    private JComboBox<String> sectionSelector;
    private JTextArea statsArea;
    private List<Section> mySections;

    public ClassStatsPanel() {
        this.instructorService = new InstructorService();
        this.studentService = new StudentService();
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Section for Stats:"));
        sectionSelector = new JComboBox<>();
        topPanel.add(sectionSelector);

        add(topPanel, BorderLayout.NORTH);

        statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        add(new JScrollPane(statsArea), BorderLayout.CENTER);

        loadSectionSelector();
        sectionSelector.addActionListener(e -> calculateStats());
    }

    private void loadSectionSelector() {
        try {
            int instructorId = UserSession.getInstance().getCurrentUser().getUserId();
            mySections = instructorService.getMySections(instructorId);
            sectionSelector.removeAllItems();
            for (Section s : mySections) {
                sectionSelector.addItem(s.getSectionId() + ": " + s.getCourseCode());
            }
        } catch (SQLException e) {
            statsArea.setText("Error loading sections.");
        }
    }

    private void calculateStats() {
        int idx = sectionSelector.getSelectedIndex();
        if (idx == -1) return;
        Section sec = mySections.get(idx);

        statsArea.setText("Calculating comprehensive stats for " + sec.getCourseCode() + "...\n");

        try {
            List<Student> students = instructorService.getStudentsBySection(sec.getSectionId());

            if (students.isEmpty()) {
                statsArea.append("No students enrolled.");
                return;
            }

            // Storage for stats: Component Name -> List of Scores
            Map<String, List<Double>> componentScores = new HashMap<>();
            List<Double> finalTotalScores = new ArrayList<>();

            for (Student s : students) {
                // Find enrollment
                List<Enrollment> enrs = studentService.getMyEnrollments(s.getUserId());
                int enrollmentId = -1;
                for(Enrollment e : enrs) {
                    if(e.getSectionId() == sec.getSectionId()) {
                        enrollmentId = e.getEnrollmentId();
                        break;
                    }
                }

                if (enrollmentId != -1) {
                    List<Grade> grades = instructorService.getGradesForEnrollment(enrollmentId);
                    double studentWeightedTotal = 0.0;

                    for (Grade g : grades) {
                        // Add to component list
                        componentScores.putIfAbsent(g.getComponent(), new ArrayList<>());
                        componentScores.get(g.getComponent()).add(g.getScore());

                        // Calculate contribution to final grade
                        if (g.getMaxMarks() > 0) {
                            double percentage = g.getScore() / g.getMaxMarks();
                            studentWeightedTotal += (percentage * g.getWeightage());
                        }
                    }
                    finalTotalScores.add(studentWeightedTotal);
                }
            }

            // --- Display Report ---
            StringBuilder sb = new StringBuilder();
            sb.append("=== CLASS STATISTICS ===\n");
            sb.append("Total Students: ").append(students.size()).append("\n\n");

            // 1. Component Stats
            sb.append("--- Component Analysis ---\n");
            sb.append(String.format("%-15s | %-8s | %-8s | %-8s\n", "Component", "Average", "Median", "Mode"));
            sb.append("----------------------------------------------------\n");

            for (String comp : componentScores.keySet()) {
                List<Double> scores = componentScores.get(comp);
                sb.append(String.format("%-15s | %-8.2f | %-8.2f | %-8.2f\n",
                        comp,
                        calcAverage(scores),
                        calcMedian(scores),
                        calcMode(scores)));
            }

            // 2. Final Overall Stats
            sb.append("\n--- Overall Final Score Analysis ---\n");
            sb.append(String.format("Average Final: %.2f\n", calcAverage(finalTotalScores)));
            sb.append(String.format("Median Final:  %.2f\n", calcMedian(finalTotalScores)));
            sb.append(String.format("Mode Final:    %.2f\n", calcMode(finalTotalScores)));

            statsArea.setText(sb.toString());

        } catch (SQLException e) {
            statsArea.setText("Error calculating stats: " + e.getMessage());
        }
    }

    // --- Helper Math Functions ---

    private double calcAverage(List<Double> scores) {
        if (scores == null || scores.isEmpty()) return 0.0;
        double sum = 0;
        for (double s : scores) sum += s;
        return sum / scores.size();
    }

    private double calcMedian(List<Double> scores) {
        if (scores == null || scores.isEmpty()) return 0.0;
        Collections.sort(scores);
        int middle = scores.size() / 2;
        if (scores.size() % 2 == 1) {
            return scores.get(middle);
        } else {
            return (scores.get(middle - 1) + scores.get(middle)) / 2.0;
        }
    }

    private double calcMode(List<Double> scores) {
        if (scores == null || scores.isEmpty()) return 0.0;
        Map<Double, Integer> frequencyMap = new HashMap<>();
        for (Double s : scores) {
            frequencyMap.put(s, frequencyMap.getOrDefault(s, 0) + 1);
        }

        double mode = scores.get(0);
        int maxCount = 0;
        for (Map.Entry<Double, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mode = entry.getKey();
            }
        }
        return mode;
    }
}