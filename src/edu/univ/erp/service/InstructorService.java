package edu.univ.erp.service;

import edu.univ.erp.data.*;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.Student;

import java.sql.SQLException;
import java.util.List;

public class InstructorService {

    private CourseDAO courseDAO;
    private EnrollmentDAO enrollmentDAO;
    private GradeDAO gradeDAO;

    public InstructorService() {
        this.courseDAO = new CourseDAOImpl();
        this.enrollmentDAO = new EnrollmentDAOImpl();
        this.gradeDAO = new GradeDAOImpl();
    }

    /**
     * Gets all sections assigned to a specific instructor.
     * @param instructorId The instructor's user ID.
     */
    public List<Section> getMySections(int instructorId) throws SQLException {
        return courseDAO.getSectionsByInstructorId(instructorId);
    }

    /**
     * Gets all students enrolled in a specific section.
     * @param sectionId The section's ID.
     */
    public List<Student> getStudentsBySection(int sectionId) throws SQLException {
        return enrollmentDAO.getStudentsBySectionId(sectionId);
    }

    /**
     * Saves or updates a grade for a student.
     * @param enrollmentId The enrollment ID.
     * @param component e.g., "Midterm", "Final"
     * @param score The numerical score.
     */
    public void submitGrade(int enrollmentId, String component, double score) throws SQLException {
        // This is a simple version. A real one would be more complex.
        gradeDAO.saveOrUpdateGrade(enrollmentId, component, score);
    }

    /**
     * Gets all grade components for a specific enrollment.
     * @param enrollmentId The enrollment ID.
     */
    public List<Grade> getGradesForEnrollment(int enrollmentId) throws SQLException {
        return gradeDAO.getGradesForEnrollment(enrollmentId);
    }
}