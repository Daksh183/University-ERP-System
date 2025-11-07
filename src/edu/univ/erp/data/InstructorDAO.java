package edu.univ.erp.data;

import java.sql.SQLException;

public interface InstructorDAO {
    /**
     * Creates a new instructor profile in the ERP DB.
     * @param userId The user_id from the Auth DB
     * @param department The instructor's department
     * @throws SQLException
     */
    void createInstructorProfile(int userId, String department) throws SQLException;
}