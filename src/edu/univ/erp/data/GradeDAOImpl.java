package edu.univ.erp.data;

import edu.univ.erp.domain.Grade;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GradeDAOImpl implements GradeDAO {

    @Override
    public List<Grade> getGradesForEnrollment(int enrollmentId) throws SQLException {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT * FROM grades WHERE enrollment_id = ?";

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, enrollmentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(new Grade(
                            rs.getInt("grade_id"),
                            rs.getInt("enrollment_id"),
                            rs.getString("component"),
                            rs.getDouble("score"),
                            rs.getString("final_grade")
                    ));
                }
            }
        }
        return grades;
    }

    @Override
    public void saveOrUpdateGrade(int enrollmentId, String component, double score) throws SQLException {
        // This query inserts a new grade. If a grade for that enrollment and
        // component already exists, it updates the score instead.
        String sql = "INSERT INTO grades (enrollment_id, component, score) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE score = VALUES(score)";

        // To make this work, we need a UNIQUE constraint in our database.
        // Run this SQL in Workbench ONE TIME:
        // ALTER TABLE grades ADD UNIQUE KEY `idx_enroll_comp` (`enrollment_id`, `component`);

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, enrollmentId);
            stmt.setString(2, component);
            stmt.setDouble(3, score);
            stmt.executeUpdate();
        }
    }
}