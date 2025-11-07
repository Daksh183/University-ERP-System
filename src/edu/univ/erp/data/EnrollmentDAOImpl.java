package edu.univ.erp.data;

import edu.univ.erp.domain.Enrollment; // <-- Add this import
import edu.univ.erp.domain.Section;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList; // <-- Add this import
import java.util.List; // <-- Add this import

public class EnrollmentDAOImpl implements EnrollmentDAO {

    // ... (keep any existing methods) ...

    @Override
    public List<Enrollment> getEnrollmentsByStudentId(int studentId) throws SQLException {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM enrollments WHERE student_id = ?";

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(new Enrollment(
                            rs.getInt("enrollment_id"),
                            rs.getInt("student_id"),
                            rs.getInt("section_id"),
                            rs.getString("status")
                    ));
                }
            }
        }
        return enrollments;
    }

    @Override
    public void addEnrollment(int studentId, int sectionId) throws SQLException {
        // The database has a UNIQUE constraint on (student_id, section_id)
        // This will automatically throw a SQLException if a duplicate is added.
        String sql = "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, 'Enrolled')";

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            stmt.executeUpdate();
        }
    }

    // ... inside EnrollmentDAOImpl.java ...

    @Override
    public void deleteEnrollment(int enrollmentId) throws SQLException {
        String sql = "DELETE FROM enrollments WHERE enrollment_id = ?";

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, enrollmentId);
            stmt.executeUpdate();
        }
    }
    // ... inside EnrollmentDAOImpl.java ...

    @Override
    public List<Section> getEnrolledSectionsByStudentId(int studentId) throws SQLException {
        List<Section> sections = new ArrayList<>();

        // This query joins enrollments, sections, courses, instructors, and users_auth
        String sql = "SELECT s.*, c.code, c.title, ua.username as instructor_name " +
                "FROM enrollments e " +
                "JOIN sections s ON e.section_id = s.section_id " +
                "JOIN courses c ON s.course_id = c.course_id " +
                "LEFT JOIN instructors i ON s.instructor_id = i.user_id " +
                "LEFT JOIN university_auth_db.users_auth ua ON i.user_id = ua.user_id " +
                "WHERE e.student_id = ? AND e.status = 'Enrolled'";

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // 1. Create the Section object
                    Section section = new Section(
                            rs.getInt("section_id"),
                            rs.getInt("course_id"),
                            rs.getInt("instructor_id"),
                            rs.getString("day_time"),
                            rs.getString("room"),
                            rs.getInt("capacity"),
                            rs.getString("semester"),
                            rs.getInt("year")
                    );

                    // 2. Set the extra fields from the JOINs
                    section.setCourseCode(rs.getString("code"));
                    section.setCourseTitle(rs.getString("title"));
                    section.setInstructorName(rs.getString("instructor_name"));

                    sections.add(section);
                }
            }
        }
        return sections;
    }
}