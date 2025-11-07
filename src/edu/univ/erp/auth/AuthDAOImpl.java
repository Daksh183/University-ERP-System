package edu.univ.erp.auth;

import edu.univ.erp.data.DatabaseConnector;
import edu.univ.erp.domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // <-- This import was added

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

    /**
     * Creates a new user in the Auth DB.
     * @return The auto-generated user_id, or -1 on failure.
     */
    @Override
    public int createUser(String username, String role, String passwordHash) throws SQLException {
        String sql = "INSERT INTO users_auth (username, role, password_hash) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnector.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, username);
            stmt.setString(2, role);
            stmt.setString(3, passwordHash);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                return -1; // Insert failed
            }

            // Get the generated user_id
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return the new user_id
                } else {
                    return -1; // Failed to get ID
                }
            }
        }
    }
}