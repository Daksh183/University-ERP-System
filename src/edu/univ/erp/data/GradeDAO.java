package edu.univ.erp.data;

import edu.univ.erp.domain.Grade;
import java.sql.SQLException;
import java.util.List;

public interface GradeDAO {

    /**
     * Gets all grade components for a single enrollment.
     * @param enrollmentId The enrollment ID.
     * @return A list of Grade objects.
     * @throws SQLException
     */
    List<Grade> getGradesForEnrollment(int enrollmentId) throws SQLException;

    /**
     * Saves or updates a grade component.
     * Uses "INSERT ... ON DUPLICATE KEY UPDATE" logic.
     * @param enrollmentId The enrollment ID.
     * @param component The name of the grade (e.g., "Midterm").
     * @param score The numerical score.
     * @throws SQLException
     */
    void saveOrUpdateGrade(int enrollmentId, String component, double score) throws SQLException;
}