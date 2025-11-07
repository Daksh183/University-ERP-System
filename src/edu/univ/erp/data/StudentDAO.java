package edu.univ.erp.data;

import java.sql.SQLException;

// This is the INTERFACE (the contract)
public interface StudentDAO {

    /**
     * Creates a new student profile in the ERP DB.
     * @param userId The user_id from the Auth DB
     * @param rollNo The student's roll number
     * @throws SQLException
     */
    void createStudentProfile(int userId, String rollNo) throws SQLException;

    // We will add more methods here later, like getStudentById, etc.
}