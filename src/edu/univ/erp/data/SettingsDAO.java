package edu.univ.erp.data;

import java.sql.SQLException;
import java.time.LocalDate;

public interface SettingsDAO {

    /**
     * Checks the database for the current maintenance mode status.
     * @return true if maintenance mode is ON, false otherwise.
     * @throws SQLException
     */
    boolean isMaintenanceModeOn() throws SQLException;

    /**
     * Updates the maintenance mode status in the database.
     * @param isOon true to turn maintenance ON, false to turn it OFF.
     * @throws SQLException
     */
    void setMaintenanceMode(boolean isOon) throws SQLException;

    /**
     * Retrieves the drop deadline from the settings table.
     * @return The deadline as a LocalDate.
     * @throws SQLException
     */
    LocalDate getDropDeadline() throws SQLException;
}