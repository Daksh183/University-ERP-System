package edu.univ.erp.data;

import edu.univ.erp.domain.Enrollment; // <-- Add this import
import edu.univ.erp.domain.Section;

import java.sql.SQLException;
import java.util.List; // <-- Add this import

public interface EnrollmentDAO {

    // ... (keep any existing methods) ...

    /**
     * Gets all enrollments for a specific student.
     * @param studentId The student's user ID.
     * @return A list of Enrollment objects.
     * @throws SQLException
     */
    List<Enrollment> getEnrollmentsByStudentId(int studentId) throws SQLException;

    /**
     * Adds a new enrollment record.
     * @param studentId The student's user ID.
     * @param sectionId The section's ID.
     * @throws SQLException if a database error occurs (e.g., duplicate entry)
     */
    void addEnrollment(int studentId, int sectionId) throws SQLException;

    // ... inside EnrollmentDAO.java ...

    /**
     * Deletes an enrollment record from the database.
     * @param enrollmentId The ID of the enrollment to delete.
     * @throws SQLException
     */
    void deleteEnrollment(int enrollmentId) throws SQLException;
    // ... inside EnrollmentDAO.java ...

    /**
     * Gets all Section details for a specific student's enrollments.
     * @param studentId The student's user ID.
     * @return A list of Section objects.
     * @throws SQLException
     */
    List<Section> getEnrolledSectionsByStudentId(int studentId) throws SQLException;
}