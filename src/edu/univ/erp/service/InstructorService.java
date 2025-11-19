package edu.univ.erp.service;

import edu.univ.erp.access.AccessControlService;
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
    private AccessControlService accessControl;

    public InstructorService() {
        this.courseDAO = new CourseDAOImpl();
        this.enrollmentDAO = new EnrollmentDAOImpl();
        this.gradeDAO = new GradeDAOImpl();
        this.accessControl = new AccessControlService();
    }

    public List<Section> getMySections(int instructorId) throws SQLException {
        return courseDAO.getSectionsByInstructorId(instructorId);
    }

    public List<Student> getStudentsBySection(int sectionId) throws SQLException {
        return enrollmentDAO.getStudentsBySectionId(sectionId);
    }

    public void submitGrade(int enrollmentId, String component, double score, double maxMarks, double weightage) throws SQLException, ServiceException {
        if (accessControl.isMaintenanceModeOn()) {
            throw new ServiceException("Maintenance Mode is ON. Submitting grades is temporarily disabled.");
        }
        gradeDAO.saveOrUpdateGrade(enrollmentId, component, score, maxMarks, weightage);
    }

    public List<Grade> getGradesForEnrollment(int enrollmentId) throws SQLException {
        return gradeDAO.getGradesForEnrollment(enrollmentId);
    }

    /**
     * Calculates the weighted score and publishes "Pass" or "Fail".
     * @param passingThreshold The percentage required to pass (e.g., 40.0).
     */
    public String computeAndPublishFinalGrade(int enrollmentId, double passingThreshold) throws SQLException, ServiceException {
        // Check Maintenance Mode
        if (accessControl.isMaintenanceModeOn()) {
            throw new ServiceException("Maintenance Mode is ON. Publishing grades is disabled.");
        }

        List<Grade> grades = gradeDAO.getGradesForEnrollment(enrollmentId);
        double totalWeightedScore = 0.0;

        for (Grade g : grades) {
            if (g.getMaxMarks() > 0) {
                // Formula: (Score / Max) * Weightage
                double percentage = g.getScore() / g.getMaxMarks();
                totalWeightedScore += (percentage * g.getWeightage());
            }
        }

        // --- NEW LOGIC: Pass vs Fail ---
        String finalStatus;
        if (totalWeightedScore >= passingThreshold) {
            finalStatus = "Pass";
        } else {
            finalStatus = "Fail";
        }

        // Save "Pass" or "Fail" to DB
        gradeDAO.updateFinalGrade(enrollmentId, finalStatus);

        return finalStatus;
    }
}