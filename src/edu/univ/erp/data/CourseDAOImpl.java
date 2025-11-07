package edu.univ.erp.data;

import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Section;
import edu.univ.erp.auth.AuthDAOImpl; // Used for DB name, or can be removed if hardcoded

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseDAOImpl implements CourseDAO {

    /**
     * Retrieves all sections from the database.
     * Joins with courses, instructors, and users_auth to get full details.
     */
    @Override
    public List<Section> getAllSections() throws SQLException {
        List<Section> sections = new ArrayList<>();

        // This SQL query joins 4 tables to get all the info we need
        // It even joins across your two databases!
        String sql = "SELECT s.*, c.code, c.title, ua.username as instructor_name " +
                "FROM sections s " +
                "JOIN courses c ON s.course_id = c.course_id " +
                "LEFT JOIN instructors i ON s.instructor_id = i.user_id " +
                "LEFT JOIN university_auth_db.users_auth ua ON i.user_id = ua.user_id";

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // 1. Create the Section object from the DB row
                Section section = new Section(
                        rs.getInt("section_id"),
                        rs.getInt("course_id"),
                        rs.getInt("instructor_id"), // This might be 0 if no instructor
                        rs.getString("day_time"),
                        rs.getString("room"),
                        rs.getInt("capacity"),
                        rs.getString("semester"),
                        rs.getInt("year")
                );

                // 2. Set the extra fields we just queried
                section.setCourseCode(rs.getString("code"));
                section.setCourseTitle(rs.getString("title"));
                section.setInstructorName(rs.getString("instructor_name"));

                sections.add(section);
            }
        }
        return sections;
    }

    /**
     * Retrieves all sections taught by a specific instructor.
     * @param instructorId The instructor's user ID.
     * @return A list of Section objects.
     * @throws SQLException
     */
    @Override
    public List<Section> getSectionsByInstructorId(int instructorId) throws SQLException {
        List<Section> sections = new ArrayList<>();
        // This is the same query as getAllSections, but with a WHERE clause
        String sql = "SELECT s.*, c.code, c.title, ua.username as instructor_name " +
                "FROM sections s " +
                "JOIN courses c ON s.course_id = c.course_id " +
                "LEFT JOIN instructors i ON s.instructor_id = i.user_id " +
                "LEFT JOIN university_auth_db.users_auth ua ON i.user_id = ua.user_id " +
                "WHERE s.instructor_id = ?"; // <-- The new part

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, instructorId); // Set the instructor ID parameter

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
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
                    section.setCourseCode(rs.getString("code"));
                    section.setCourseTitle(rs.getString("title"));
                    section.setInstructorName(rs.getString("instructor_name"));
                    sections.add(section);
                }
            }
        }
        return sections;
    }

    // You will add other methods here later, like createCourse, etc.
}