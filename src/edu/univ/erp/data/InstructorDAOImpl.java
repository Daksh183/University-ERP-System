package edu.univ.erp.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InstructorDAOImpl implements InstructorDAO {

    @Override
    public void createInstructorProfile(int userId, String department) throws SQLException {
        String sql = "INSERT INTO instructors (user_id, department) VALUES (?, ?)";

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, department);
            stmt.executeUpdate();
        }
    }
}