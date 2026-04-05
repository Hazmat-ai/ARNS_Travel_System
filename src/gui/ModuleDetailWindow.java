package gui;

import javax.swing.*;
import java.awt.*;

/**
 * ModuleDetailWindow
 * Shows a simple details window when a module item is opened.
 */
public class ModuleDetailWindow extends JFrame {
    private static final long serialVersionUID = 1L;

    // Constructor: Builds the full detail window UI

    public ModuleDetailWindow(String module, String detailText, String city) {


        // Window settings

        setTitle(module + " — Details");
        setSize(480, 360);
        setLayout(new BorderLayout(8,8));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        // Header label

        
        JLabel heading = new JLabel(module + " details", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(heading, BorderLayout.NORTH);
        

        // Main text area showing module information + city


        JTextArea area = new JTextArea(detailText + "\n\nCity: " + (city == null ? "—" : city));
        area.setEditable(false);
        area.setWrapStyleWord(true);
        area.setLineWrap(true);
        area.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        add(new JScrollPane(area), BorderLayout.CENTER);

   
        // Close button at bottom

        
        JButton close = new JButton("Close");
        close.addActionListener(e -> dispose());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(close);
        add(south, BorderLayout.SOUTH);


        // Center the window and show it
 
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

