package edu.univ.erp.auth;

import edu.univ.erp.domain.User;
import java.sql.SQLException;

// This is the INTERFACE (the contract)
public interface AuthDAO {

    /**
     * Attempts to log in a user.
     *
     * @param username The username entered by the user.
     * @param password The plaintext password entered by the user.
     * @return A User object if login is successful.
     * @throws SQLException if a database error occurs.
     * @throws AuthException if login fails (wrong username/password).
     */
    User login(String username, String password) throws SQLException, AuthException;
    // ... inside AuthDAO.java ...

    /**
     * Creates a new user in the Auth DB.
     * @return The auto-generated user_id, or -1 on failure.
     */
    int createUser(String username, String role, String passwordHash) throws SQLException;
}

