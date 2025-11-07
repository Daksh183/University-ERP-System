package edu.univ.erp.domain;

public class Grade {
    private int gradeId;
    private int enrollmentId; // Links to Enrollment.enrollmentId
    private String component; // e.g., "Quiz", "Midterm", "Final"
    private double score;
    private String finalGrade; // e.g., "A", "B+", "F"

    public Grade(int gradeId, int enrollmentId, String component, double score, String finalGrade) {
        this.gradeId = gradeId;
        this.enrollmentId = enrollmentId;
        this.component = component;
        this.score = score;
        this.finalGrade = finalGrade;
    }

    // --- Getters ---
    // (You can add all the getters here)
    public int getGradeId() { return gradeId; }
    public int getEnrollmentId() { return enrollmentId; }
    public String getComponent() { return component; }
    public double getScore() { return score; }
}