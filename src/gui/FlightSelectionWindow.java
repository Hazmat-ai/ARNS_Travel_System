package gui;

import javax.swing.*;

public class FlightSelectionWindow extends JFrame {
    private static final long serialVersionUID = 1L;

    /**
     * This class creates a simple notification window using a JFrame.
     * The window displays a centered label and closes only itself when the user exits.
     * It is designed to be a lightweight popup screen within the larger application.
     */
    
    // Constructor: This sets up the Notifications window
    public FlightSelectionWindow() {
    	// Basic window setup (title, size, close behavior)
        setTitle("Notifications");
        setSize(400, 300);
        // Add a simple label to visually indicate this is the Notifications page
        JLabel label = new JLabel("Notifications Screen", SwingConstants.CENTER);
        add(label);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //display
        setVisible(true);
    }
}
