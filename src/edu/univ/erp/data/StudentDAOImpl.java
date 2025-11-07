package edu.univ.erp.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// This is the IMPLEMENTATION (the real work)
public class StudentDAOImpl implements StudentDAO {

    @Override
    public void createStudentProfile(int userId, String rollNo) throws SQLException {
        // Assumes default year and program, admin can edit later
        String sql = "INSERT INTO students (user_id, roll_no, program, year) VALUES (?, ?, 'N/A', 1)";

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, rollNo);
            stmt.executeUpdate();
        }
    }

    // We will add more methods here later
}