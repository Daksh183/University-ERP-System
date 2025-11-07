package edu.univ.erp.access;

import edu.univ.erp.data.SettingsDAO;
import edu.univ.erp.data.SettingsDAOImpl;

import java.sql.SQLException;

/**
 * A central service to check permissions and system-wide flags
 * like Maintenance Mode.
 */
public class AccessControlService {

    private SettingsDAO settingsDAO;

    public AccessControlService() {
        this.settingsDAO = new SettingsDAOImpl();
    }

    /**
     * Checks if the system is in read-only maintenance mode.
     * @return true if maintenance is ON, false otherwise.
     * @throws SQLException
     */
    public boolean isMaintenanceModeOn() throws SQLException {
        return settingsDAO.isMaintenanceModeOn();
    }
}