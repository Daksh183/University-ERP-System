package edu.univ.erp.service;

import edu.univ.erp.access.AccessControlService;
import edu.univ.erp.data.*;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade; // <--- Import Grade
import edu.univ.erp.domain.Section;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class StudentService {

    private EnrollmentDAO enrollmentDAO;
    private GradeDAO gradeDAO; // <--- Add GradeDAO
    private AccessControlService accessControl;
    private SettingsDAO settingsDAO;

    public StudentService() {
        this.enrollmentDAO = new EnrollmentDAOImpl();
        this.gradeDAO = new GradeDAOImpl(); // <--- Initialize it
        this.accessControl = new AccessControlService();
        this.settingsDAO = new SettingsDAOImpl();
    }

    /**
     * Registers a student for a section.
     */
    public void registerForSection(int studentId, int sectionId) throws SQLException, ServiceException {
        if (accessControl.isMaintenanceModeOn()) {
            throw new ServiceException("Maintenance Mode is ON. Registration is temporarily disabled.");
        }
        try {
            enrollmentDAO.addEnrollment(studentId, sectionId);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new ServiceException("You are already registered for this section.");
            }
            throw new ServiceException("Database error. Could not register.");
        }
    }

    /**
     * Drops a student from a section.
     */
    public void dropSection(int enrollmentId) throws SQLException, ServiceException {
        // 1. Check Maintenance Mode
        if (accessControl.isMaintenanceModeOn()) {
            throw new ServiceException("Maintenance Mode is ON. Dropping courses is temporarily disabled.");
        }

        // 2. Check Drop Deadline
        LocalDate deadline = settingsDAO.getDropDeadline();
        if (LocalDate.now().isAfter(deadline)) {
            throw new ServiceException("The drop deadline (" + deadline + ") has passed. You cannot drop this course.");
        }

        // 3. Proceed with Drop
        enrollmentDAO.deleteEnrollment(enrollmentId);
    }

    /**
     * Gets all enrollments for a specific student.
     */
    public List<Enrollment> getMyEnrollments(int studentId) throws SQLException {
        return enrollmentDAO.getEnrollmentsByStudentId(studentId);
    }

    /**
     * Gets a student's timetable.
     */
    public List<Section> getMyTimetable(int studentId) throws SQLException {
        return enrollmentDAO.getEnrolledSectionsByStudentId(studentId);
    }

    /**
     * Gets grades for a specific enrollment.
     * @param enrollmentId The ID of the enrollment.
     * @return List of Grade objects.
     */
    public List<Grade> getGrades(int enrollmentId) throws SQLException {
        return gradeDAO.getGradesForEnrollment(enrollmentId);
    }
}