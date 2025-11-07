package edu.univ.erp.service;

import edu.univ.erp.data.EnrollmentDAO;
import edu.univ.erp.data.EnrollmentDAOImpl;
import edu.univ.erp.domain.Enrollment; // Import your Enrollment class
import edu.univ.erp.domain.Section; // <-- ADD THIS if it's missing

import java.sql.SQLException;
import java.util.List;

public class StudentService {

    private EnrollmentDAO enrollmentDAO;

    public StudentService() {
        this.enrollmentDAO = new EnrollmentDAOImpl(); // We'll need to code this DAO
    }

    /**
     * Registers a student for a section.
     * @param studentId The ID of the student.
     * @param sectionId The ID of the section.
     */
    public void registerForSection(int studentId, int sectionId) throws SQLException, ServiceException {
        // TODO: Add logic from the brief:
        // 1. Check if section is full (requires CourseDAO)
        // 2. Check for duplicate enrollment (our DAO will do this)
        // 3. Check if maintenance mode is ON (requires AccessControl)

        try {
            enrollmentDAO.addEnrollment(studentId, sectionId);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // MySQL code for duplicate entry
                throw new ServiceException("You are already registered for this section.");
            }
            throw new ServiceException("Database error. Could not register.");
        }
    }
    // ... inside StudentService.java ...

    /**
     * Drops a student from a section.
     * @param enrollmentId The ID of the enrollment record to remove.
     */
    public void dropSection(int enrollmentId) throws SQLException, ServiceException {
        // TODO: Add logic from the brief:
        // 1. Check for drop deadline (we'll skip this for now)
        // 2. Check if maintenance mode is ON (requires AccessControl)

        // For now, we just tell the DAO to delete the enrollment
        enrollmentDAO.deleteEnrollment(enrollmentId);
    }

    /**
     * Gets all enrollments for a specific student.
     * @param studentId The ID of the student.
     */
    public List<Enrollment> getMyEnrollments(int studentId) throws SQLException {
        return enrollmentDAO.getEnrollmentsByStudentId(studentId);
    }

    // We need a custom exception for service errors
    public static class ServiceException extends Exception {
        public ServiceException(String message) {
            super(message);
        }
    }
    // ... inside StudentService.java ...

    /**
     * Gets a list of all Section objects a student is enrolled in.
     * @param studentId The student's user ID.
     * @return A list of Section objects with full details.
     * @throws SQLException
     */
    public List<Section> getMyTimetable(int studentId) throws SQLException {
        // This just passes the call down to the DAO
        return enrollmentDAO.getEnrolledSectionsByStudentId(studentId);
    }
}