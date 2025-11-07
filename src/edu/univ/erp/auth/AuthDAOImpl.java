package edu.univ.erp.auth;

import edu.univ.erp.data.DatabaseConnector;
import edu.univ.erp.domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// This is the IMPLEMENTATION (the real work)
public class AuthDAOImpl implements AuthDAO {

    @Override
    public User login(String username, String password) throws SQLException, AuthException {
        // SQL to find the user and their hash in the Auth DB
        String sql = "SELECT user_id, role, password_hash FROM users_auth WHERE username = ?";

        // Step 1: Connect to the Auth DB
        try (Connection conn = DatabaseConnector.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            // Step 2: Run the query
            try (ResultSet rs = stmt.executeQuery()) {

                // Step 3: Check if we found a user
                if (rs.next()) {
                    // A user with this username exists.
                    // Now, get the stored hash from the database.
                    String storedHash = rs.getString("password_hash");

                    // Step 4: Check the password with jBCrypt
                    if (PasswordHasher.checkPassword(password, storedHash)) {
                        // Password is correct!
                        // Get the user's details
                        int userId = rs.getInt("user_id");
                        String role = rs.getString("role");

                        // Create and return a User domain object
                        return new User(userId, username, role);
                    } else {
                        // Password was wrong
                        throw new AuthException("Incorrect username or password.");
                    }
                } else {
                    // No user with that username was found
                    throw new AuthException("Incorrect username or password.");
                }
            }
        }
    }
}