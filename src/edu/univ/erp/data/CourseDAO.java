package edu.univ.erp.data;

import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Section; // <-- Add this import

import java.sql.SQLException;
import java.util.List;

public interface CourseDAO {

    // ... (keep any existing methods) ...

    /**
     * Retrieves all sections from the database.
     * @return A list of all Section objects.
     * @throws SQLException if a database error occurs.
     */
    List<Section> getAllSections() throws SQLException;
}