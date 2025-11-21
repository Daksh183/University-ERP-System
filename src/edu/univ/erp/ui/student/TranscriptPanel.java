package edu.univ.erp.ui.student;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.StudentService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class TranscriptPanel extends JPanel {

    private JTable transcriptTable;
    private DefaultTableModel tableModel;
    private JButton downloadButton;
    private StudentService studentService;

    public TranscriptPanel() {
        this.studentService = new StudentService();
        setLayout(new BorderLayout(10, 10));

        // --- 1. Header ---
        JLabel headerLabel = new JLabel("Official Academic Transcript Preview", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(headerLabel, BorderLayout.NORTH);

        // --- 2. The Table ---
        String[] columnNames = {"Course Code", "Course Title", "Credits", "Semester", "Year", "Final Grade"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transcriptTable = new JTable(tableModel);
        transcriptTable.setFillsViewportHeight(true);

        // --- STYLING START ---
        transcriptTable.setShowGrid(true);
        transcriptTable.setGridColor(new Color(100, 100, 100)); // Gray Grid
        transcriptTable.setIntercellSpacing(new Dimension(1, 1));
        transcriptTable.setRowHeight(35); // Comfortable height

        // Header: Center Aligned
        ((DefaultTableCellRenderer)transcriptTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        // Data: Left Aligned + Padding
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        leftRenderer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0)); // 10px padding left
        transcriptTable.setDefaultRenderer(Object.class, leftRenderer);
        // --- STYLING END ---

        JScrollPane scrollPane = new JScrollPane(transcriptTable);
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. Download Button ---
        downloadButton = new JButton("Download Transcript (CSV)");
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(downloadButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- Actions ---
        downloadButton.addActionListener(e -> generateTranscriptCSV());

        // Load data immediately
        refreshData();
    }

    // --- REQUIRED METHOD FOR DASHBOARD REFRESH ---
    public void refreshData() {
        loadTranscriptData();
    }

    private void loadTranscriptData() {
        try {
            tableModel.setRowCount(0);
            int studentId = UserSession.getInstance().getCurrentUser().getUserId();

            // Fetch all necessary data
            List<Enrollment> enrollments = studentService.getMyEnrollments(studentId);
            List<Section> sections = studentService.getMyTimetable(studentId); // Reusing this to get Section details

            for (Enrollment enrollment : enrollments) {
                // Find section details
                Section matchingSection = null;
                for (Section sec : sections) {
                    if (sec.getSectionId() == enrollment.getSectionId()) {
                        matchingSection = sec;
                        break;
                    }
                }

                if (matchingSection != null) {
                    // Find Final Grade
                    String finalGrade = "Pending"; // Default
                    List<Grade> grades = studentService.getGrades(enrollment.getEnrollmentId());

                    // Check if any grade entry has a final grade string
                    for (Grade g : grades) {
                        if (g.getFinalGrade() != null && !g.getFinalGrade().isEmpty()) {
                            finalGrade = g.getFinalGrade();
                            break;
                        }
                    }

                    // Add row to table
                    Object[] row = {
                            matchingSection.getCourseCode(),
                            matchingSection.getCourseTitle(),
                            matchingSection.getCredits(),
                            matchingSection.getSemester(),
                            matchingSection.getYear(),
                            finalGrade
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            if(this.isShowing()) {
                JOptionPane.showMessageDialog(this, "Error loading transcript: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void generateTranscriptCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Transcript");
        fileChooser.setSelectedFile(new File("transcript.csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(file)) {
                // Write Header
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    writer.write(tableModel.getColumnName(i) + (i == tableModel.getColumnCount() - 1 ? "" : ","));
                }
                writer.write("\n");

                // Write Rows from the Table Model
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        writer.write(tableModel.getValueAt(i, j) + (j == tableModel.getColumnCount() - 1 ? "" : ","));
                    }
                    writer.write("\n");
                }

                JOptionPane.showMessageDialog(this, "Transcript saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}