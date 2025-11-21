package edu.univ.erp.service;

import edu.univ.erp.access.AccessControlService;
import edu.univ.erp.data.*;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.Student;
import edu.univ.erp.service.ServiceException;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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

    // --- NEW METHOD: Batch Publish for Whole Section ---
    public void publishAllGrades(int sectionId, Map<String, Double> cutoffs) throws SQLException, ServiceException {
        // 1. Check Maintenance Mode
        if (accessControl.isMaintenanceModeOn()) {
            throw new ServiceException("Maintenance Mode is ON. Publishing grades is disabled.");
        }

        // 2. Get all students in the section
        List<Student> students = enrollmentDAO.getStudentsBySectionId(sectionId);

        for (Student s : students) {
            // 3. Find Enrollment ID for this student in this section
            List<Enrollment> enrs = enrollmentDAO.getEnrollmentsByStudentId(s.getUserId());
            int enrollmentId = -1;
            for (Enrollment e : enrs) {
                if (e.getSectionId() == sectionId) {
                    enrollmentId = e.getEnrollmentId();
                    break;
                }
            }

            if (enrollmentId != -1) {
                // 4. Calculate Weighted Total
                List<Grade> grades = gradeDAO.getGradesForEnrollment(enrollmentId);
                double total = 0.0;
                for (Grade g : grades) {
                    if (g.getMaxMarks() > 0) {
                        double percentage = g.getScore() / g.getMaxMarks();
                        total += (percentage * g.getWeightage());
                    }
                }

                // 5. Determine Letter Grade based on Cutoffs
                String letter = "F";
                if (total >= cutoffs.get("A")) letter = "A";
                else if (total >= cutoffs.get("A-")) letter = "A-";
                else if (total >= cutoffs.get("B")) letter = "B";
                else if (total >= cutoffs.get("B-")) letter = "B-";
                else if (total >= cutoffs.get("C")) letter = "C";
                else if (total >= cutoffs.get("C-")) letter = "C-";
                else if (total >= cutoffs.get("D")) letter = "D";

                // 6. Save to DB
                gradeDAO.updateFinalGrade(enrollmentId, letter);
            }
        }
    }
}