package gui;

import model.Reservation;
import service.ReservationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * BookingGUI: Modern booking dialog with automatic price extraction and auto-fill
 */
public class BookingGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    
    // Static variables to remember the last entered user information
    private static String lastUserName = "";
    private static String lastUserEmail = "";
    
    public BookingGUI(String module, String itemText) {
        setTitle("Book " + module);
        setSize(480, 400);
        setLayout(new BorderLayout(12, 12));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Extract price from itemText automatically
        double extractedPrice = extractPrice(itemText);
        
        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        header.setBorder(new EmptyBorder(15, 20, 10, 20));
        header.setBackground(new Color(0xf5f5f5));
        
        JLabel icon = new JLabel(getModuleIcon(module));
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        
        JLabel title = new JLabel("Booking: " + module);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        header.add(icon);
        header.add(title);
        add(header, BorderLayout.NORTH);
        
        // Form fields
        JPanel center = new JPanel(new GridLayout(3, 2, 12, 12));
        center.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        // Name field (auto-filled with last used name)
        center.add(createLabel("Name:"));
        JTextField nameField = new JTextField(lastUserName);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        nameField.setToolTipText("Your name will be remembered for future bookings");
        center.add(nameField);
        
        // Email field (auto-filled with last used email)
        center.add(createLabel("Email:"));
        JTextField emailField = new JTextField(lastUserEmail);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        emailField.setToolTipText("Your email will be remembered for future bookings");
        center.add(emailField);
        
        // Item field (read-only)
        center.add(createLabel("Item:"));
        JTextField itemField = new JTextField(itemText);
        itemField.setEditable(false);
        itemField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        itemField.setBackground(new Color(0xf0f0f0));
        center.add(itemField);
        
        SwingUtilities.invokeLater(() -> {
            itemField.setCaretPosition(0); 	// Sets the cursor position to 0
        });
        
        add(center, BorderLayout.CENTER);
        
        // Price display panel (info only, not editable)
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        pricePanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xe0e0e0)));
        
        JLabel priceLabel = new JLabel("Total Price: $" + String.format("%.2f", extractedPrice));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        priceLabel.setForeground(new Color(0x2563eb));
        
        pricePanel.add(priceLabel);
        
        // Info label for auto-fill
        if (!lastUserName.isEmpty() || !lastUserEmail.isEmpty()) {
            JLabel autoFillInfo = new JLabel("Fields pre-filled from your last booking - edit as needed");
            autoFillInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            autoFillInfo.setForeground(new Color(0x666666));
            autoFillInfo.setBorder(new EmptyBorder(0, 20, 5, 0));
            pricePanel.add(autoFillInfo);
        }
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBorder(new EmptyBorder(0, 20, 15, 20));
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cancelBtn.setBackground(new Color(0xBA2D2D));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorderPainted(false);
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        cancelBtn.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(cancelBtn);
            if (window != null) {
                window.dispose();
            }
        });
        
        JButton confirmBtn = new JButton("Add to Cart");
        confirmBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        confirmBtn.setBackground(new Color(0x4caf50));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFocusPainted(false);
        confirmBtn.setBorderPainted(false);
        confirmBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        confirmBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String item = itemField.getText();
            
            // Validation
            if (name.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter your name and email.", 
                    "Missing Information", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (!isValidEmail(email)) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a valid email address.", 
                    "Invalid Email", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Save the current information for auto-fill next time
            lastUserName = name;
            lastUserEmail = email;
            
            // Create reservation with extracted price
            Reservation r = new Reservation(name, email, module, item, extractedPrice);
            ReservationService.addReservation(r);
            
            // Success message
            JOptionPane.showMessageDialog(this, 
                "Booking confirmed!\n\n" + 
                "Customer: " + name + "\n" +
                "Item: " + item + "\n" +
                "Total: $" + String.format("%.2f", extractedPrice),
                "Booking Confirmed", 
                JOptionPane.INFORMATION_MESSAGE);
            
            dispose();
        });
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(confirmBtn);
        
        // Bottom section with price and buttons
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(pricePanel, BorderLayout.NORTH);
        bottom.add(buttonPanel, BorderLayout.SOUTH);
        
        add(bottom, BorderLayout.SOUTH);
        
        setLocationRelativeTo(null);
        setVisible(true);
        
        // Focus on name field (select all text so user can easily type over it)
        nameField.requestFocusInWindow();
        nameField.selectAll();
    }
    
    /**
     * Extract price from item text using regex patterns
     * Looks for patterns like: $123, $123.45, £50, etc.
     */
    private double extractPrice(String text) {
        if (text == null || text.isEmpty()) {
            return 0.0;
        }
        
        // Pattern to match currency symbols followed by numbers
        // Matches: $100, $100.50, £200, €150.99, etc.
        Pattern pattern = Pattern.compile("[$£€¥]\\s*(\\d+(?:\\.\\d{2})?)");
        Matcher matcher = pattern.matcher(text);
        
        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1));
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        
        return 0.0;
    }
    
    /**
     * Simple email validation
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        // Basic email pattern
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
    
    /**
     * Get icon for module type
     */
    private String getModuleIcon(String module) {
        switch (module) {
            case "Flights": return "✈️";
            case "Hotels": return "🏨";
            case "Cars": return "🚗";
            case "Attractions": return "🎭";
            case "Events": return "🎫";
            case "Passes": return "🎟️";
            case "Restaurants": return "🍽️";
            case "Tours": return "🗺️";
            default: return "📋";
        }
    }
    
    /**
     * Create styled label
     */
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return label;
    }
    
    /**
     * Static method to manually set auto-fill data (useful for testing or integration)
     */
    public static void setAutoFillData(String name, String email) {
        lastUserName = name != null ? name : "";
        lastUserEmail = email != null ? email : "";
    }
    
    /**
     * Static method to clear auto-fill data
     */
    public static void clearAutoFillData() {
        lastUserName = "";
        lastUserEmail = "";
    }
}