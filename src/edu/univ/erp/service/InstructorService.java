package edu.univ.erp.service;

import edu.univ.erp.access.AccessControlService; // <-- ADD THIS IMPORT
import edu.univ.erp.data.*;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.Student;
import edu.univ.erp.service.ServiceException;

import java.sql.SQLException;
import java.util.List;

public class InstructorService {

    private CourseDAO courseDAO;
    private EnrollmentDAO enrollmentDAO;
    private GradeDAO gradeDAO;
    private AccessControlService accessControl; // <-- ADD THIS

    public InstructorService() {
        this.courseDAO = new CourseDAOImpl();
        this.enrollmentDAO = new EnrollmentDAOImpl();
        this.gradeDAO = new GradeDAOImpl();
        this.accessControl = new AccessControlService(); // <-- ADD THIS
    }

    /**
     * Gets all sections assigned to a specific instructor. (READ-ONLY)
     */
    public List<Section> getMySections(int instructorId) throws SQLException {
        return courseDAO.getSectionsByInstructorId(instructorId);
    }

    /**
     * Gets all students enrolled in a specific section. (READ-ONLY)
     */
    public List<Student> getStudentsBySection(int sectionId) throws SQLException {
        return enrollmentDAO.getStudentsBySectionId(sectionId);
    }

    /**
     * Saves or updates a grade for a student.
     */
    public void submitGrade(int enrollmentId, String component, double score) throws SQLException, ServiceException {        // --- MAINTENANCE MODE CHECK ---
        if (accessControl.isMaintenanceModeOn()) {
            throw new ServiceException("Maintenance Mode is ON. Submitting grades is temporarily disabled.");
        }

        // --- (Original logic) ---
        gradeDAO.saveOrUpdateGrade(enrollmentId, component, score);
    }

    /**
     * Gets all grade components for a specific enrollment. (READ-ONLY)
     */
    public List<Grade> getGradesForEnrollment(int enrollmentId) throws SQLException {
        return gradeDAO.getGradesForEnrollment(enrollmentId);
    }
}