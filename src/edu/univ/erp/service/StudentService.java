package edu.univ.erp.service;

import edu.univ.erp.access.AccessControlService; // <-- ADD THIS IMPORT
import edu.univ.erp.data.EnrollmentDAO;
import edu.univ.erp.data.EnrollmentDAOImpl;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section; // <-- ADD THIS IMPORT
import edu.univ.erp.service.ServiceException;

import java.sql.SQLException;
import java.util.List;

public class StudentService {

    private EnrollmentDAO enrollmentDAO;
    private AccessControlService accessControl; // <-- ADD THIS

    public StudentService() {
        this.enrollmentDAO = new EnrollmentDAOImpl();
        this.accessControl = new AccessControlService(); // <-- ADD THIS
    }

    /**
     * Registers a student for a section.
     */
    public void registerForSection(int studentId, int sectionId) throws SQLException, ServiceException {
        // --- MAINTENANCE MODE CHECK ---
        if (accessControl.isMaintenanceModeOn()) {
            throw new ServiceException("Maintenance Mode is ON. Registration is temporarily disabled.");
        }

        // --- (Original logic) ---
        try {
            enrollmentDAO.addEnrollment(studentId, sectionId);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // MySQL code for duplicate entry
                throw new ServiceException("You are already registered for this section.");
            }
            throw new ServiceException("Database error. Could not register.");
        }
    }

    /**
     * Drops a student from a section.
     */
    public void dropSection(int enrollmentId) throws SQLException, ServiceException {
        // --- MAINTENANCE MODE CHECK ---
        if (accessControl.isMaintenanceModeOn()) {
            throw new ServiceException("Maintenance Mode is ON. Dropping courses is temporarily disabled.");
        }

        // --- (Original logic) ---
        enrollmentDAO.deleteEnrollment(enrollmentId);
    }

    /**
     * Gets all enrollments for a specific student. (READ-ONLY)
     */
    public List<Enrollment> getMyEnrollments(int studentId) throws SQLException {
        return enrollmentDAO.getEnrollmentsByStudentId(studentId);
    }

    /**
     * Gets a student's timetable. (READ-ONLY)
     */
    public List<Section> getMyTimetable(int studentId) throws SQLException {
        return enrollmentDAO.getEnrolledSectionsByStudentId(studentId);
    }

    // We need a custom exception for service errors

}