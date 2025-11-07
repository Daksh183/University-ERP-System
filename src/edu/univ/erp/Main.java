package edu.univ.erp;

import edu.univ.erp.ui.auth.LoginWindow;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        // Use SwingUtilities.invokeLater to ensure the UI
        // is created on the correct "Event Dispatch Thread".
        // This is the standard, safe way to start a Swing app.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // 1. Create an instance of our window
                LoginWindow loginWindow = new LoginWindow();

                // 2. Make it visible
                loginWindow.setVisible(true);
            }
        });
    }
}