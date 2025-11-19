package edu.univ.erp.data;

import edu.univ.erp.domain.Section;
import java.sql.SQLException;
import java.util.List;

public interface CourseDAO {

    List<Section> getAllSections() throws SQLException;

    List<Section> getSectionsByInstructorId(int instructorId) throws SQLException;

    void createCourse(String code, String title, int credits) throws SQLException;

    void updateCourse(String code, String title, int credits) throws SQLException;

    void createSection(int courseId, int instructorId, String dayTime, String room, int capacity, String semester, int year) throws SQLException;

    // --- UPDATED METHOD: Now includes Semester and Year ---
    void updateSection(int sectionId, int courseId, int instructorId, String dayTime, String room, int capacity, String semester, int year) throws SQLException;
}