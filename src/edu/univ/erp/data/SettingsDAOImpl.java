package edu.univ.erp.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SettingsDAOImpl implements SettingsDAO {

    private static final String MAINTENANCE_KEY = "maintenance_on";

    @Override
    public boolean isMaintenanceModeOn() throws SQLException {
        String sql = "SELECT setting_value FROM settings WHERE setting_key = ?";

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, MAINTENANCE_KEY);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Return true if the value in the DB is "true"
                    return "true".equalsIgnoreCase(rs.getString("setting_value"));
                }
            }
        }
        // Default to 'false' (safe) if key is missing
        return false;
    }

    @Override
    public void setMaintenanceMode(boolean isOon) throws SQLException {
        // "UPDATE ... SET" is safer than INSERT ON DUPLICATE KEY here
        String sql = "UPDATE settings SET setting_value = ? WHERE setting_key = ?";
        String value = isOon ? "true" : "false";

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, value);
            stmt.setString(2, MAINTENANCE_KEY);
            stmt.executeUpdate();
        }
    }
}