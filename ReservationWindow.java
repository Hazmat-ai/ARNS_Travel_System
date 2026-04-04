package gui;

import service.ReservationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Enhanced ReservationWindow with modern UI and self-managed cart.
 * 
 * This class acts as a standalone shopping cart screen that displays all
 * current reservations, allows removing/editing items, calculates totals,
 * and handles checkout flow. It pulls reservation data from
 * ReservationService and provides a full GUI for interacting with it.
 */
public class ReservationWindow extends JFrame {
    private static final long serialVersionUID = 1L;

    private final Color bgColor = new Color(245, 245, 245);
    private final Color cardColor = Color.WHITE;
    private final Color accentColor = new Color(41, 128, 185);
    private final Color dangerColor = new Color(231, 76, 60);
    private final Color textColor = new Color(51, 51, 51);
    private final Color secondaryText = new Color(102, 102, 102);

    private JPanel reservationsPanel;
    private JLabel totalLabel;
    private double totalAmount = 0.0;
    private List<Object> currentReservations;


    /** 
     * Constructor: initializes the window, builds the UI layout, 
     * loads reservations from ReservationService, and displays the window.
     */
    public ReservationWindow() {
        initializeWindow();
        setupUI();
        loadReservations();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    /**
     * Sets up the core JFrame properties such as title, size, 
     * default close behavior, background color, and layout.
     */
    private void initializeWindow() {
        setTitle("Shopping Cart");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(bgColor);
    }
    /**
     * Builds the overall UI structure: header, content panel, and footer.
     * This method only handles layout assembly, not data population.
     */
    private void setupUI() {
        // Header
        add(createHeader(), BorderLayout.NORTH);

        // Main content
        add(createContentPanel(), BorderLayout.CENTER);

        // Footer
        add(createFooter(), BorderLayout.SOUTH);
    }
    /**
     * Creates the top banner showing the cart title, subtitle, and icon.
     * Provides a visually polished header for the cart screen.
     */
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(accentColor);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("Your Shopping Cart");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Manage your travel reservations");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(255, 255, 255, 200));

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(title, BorderLayout.NORTH);
        textPanel.add(subtitle, BorderLayout.SOUTH);

        // Cart icon
        JLabel cartIcon = new JLabel("🛒");
        cartIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        cartIcon.setBorder(new EmptyBorder(0, 0, 0, 10));

        header.add(cartIcon, BorderLayout.WEST);
        header.add(textPanel, BorderLayout.CENTER);

        return header;
    }
    /**
     * Creates the main scrollable content area where reservation cards appear.
     */
    private JComponent createContentPanel() {
        reservationsPanel = new JPanel();
        reservationsPanel.setLayout(new BoxLayout(reservationsPanel, BoxLayout.Y_AXIS));
        reservationsPanel.setBackground(bgColor);
        reservationsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(reservationsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return scrollPane;
    }
    /**
     * Creates the footer containing the total price display and functional
     * buttons: Clear Cart, Checkout, and Close.
     */
    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            new EmptyBorder(15, 20, 15, 20)
        ));

        // Total amount
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setForeground(accentColor);

        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton clearButton = createStyledButton("Clear Cart", dangerColor);
        clearButton.addActionListener(e -> clearCart());

        JButton checkoutButton = createStyledButton("Proceed to Checkout", new Color(46, 204, 113));
        checkoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        checkoutButton.addActionListener(e -> proceedToCheckout());

        JButton closeButton = createStyledButton("Close", new Color(149, 165, 166));
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(clearButton);
        buttonPanel.add(checkoutButton);
        buttonPanel.add(closeButton);

        footer.add(totalLabel, BorderLayout.WEST);
        footer.add(buttonPanel, BorderLayout.EAST);

        return footer;
    }

    /**
     * Utility method for generating consistently styled buttons with
     * hover effects used throughout the UI.
     */
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(8, 16, 8, 16));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    /**
     * Loads reservations from ReservationService, generates visual cards,
     * extracts pricing, updates totals, and handles empty-cart display.
     */
    private void loadReservations() {
        reservationsPanel.removeAll();
        totalAmount = 0.0;

        // Get current reservations from service
        currentReservations = new ArrayList<>(ReservationService.getAllReservations());
        
        if (currentReservations.isEmpty()) {
            showEmptyState();
        } else {
            for (int i = 0; i < currentReservations.size(); i++) {
                Object reservation = currentReservations.get(i);
                reservationsPanel.add(createReservationCard(reservation, i));
                reservationsPanel.add(Box.createVerticalStrut(10));
                
                // Extract price from reservation
                extractPriceFromReservation(reservation);
            }
        }

        updateTotal();
        reservationsPanel.revalidate();
        reservationsPanel.repaint();
    }
    /**
     * Parses reservation text to extract a price value for total calculation.
     */
    private void extractPriceFromReservation(Object reservation) {
        try {
            String reservationStr = reservation.toString();
            if (reservationStr.contains("$")) {
                // Try to extract price after $ symbol
                String pricePart = reservationStr.split("\\$")[1];
                // Take only numbers and decimal point
                StringBuilder priceBuilder = new StringBuilder();
                for (char c : pricePart.toCharArray()) {
                    if (Character.isDigit(c) || c == '.') {
                        priceBuilder.append(c);
                    } else if (priceBuilder.length() > 0) {
                        // Stop at first non-digit after we started building price
                        break;
                    }
                }
                if (priceBuilder.length() > 0) {
                    totalAmount += Double.parseDouble(priceBuilder.toString());
                }
            }
        } catch (Exception e) {
            // If price extraction fails, continue without adding to total
            System.out.println("Could not extract price from: " + reservation);
        }
    }
    /**
     * Creates a reservation card UI component containing details text and
     * Edit/Remove controls.
     */

    private JPanel createReservationCard(Object reservation, int index) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Reservation details
        JTextArea detailsArea = new JTextArea(formatReservationText(reservation));
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setBackground(cardColor);
        detailsArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsArea.setForeground(textColor);
        detailsArea.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        actionPanel.setOpaque(false);

        JButton editButton = createSmallButton("Edit");
        editButton.addActionListener(e -> editReservation(reservation));

        JButton removeButton = createSmallButton("Remove");
        removeButton.setBackground(dangerColor);
        removeButton.addActionListener(e -> removeReservation(index));

        actionPanel.add(editButton);
        actionPanel.add(removeButton);

        card.add(detailsArea, BorderLayout.CENTER);
        card.add(actionPanel, BorderLayout.SOUTH);

        return card;
    }

    /**
     * Formats reservation text into a cleaner multi-line layout suitable
     * for card display.
     */

    private String formatReservationText(Object reservation) {
        String text = reservation.toString();
        // Format the text to be more readable - replace commas and semicolons with new lines
        return text.replace(", ", "\n").replace("; ", "\n").replace(" - ", "\n");
    }

    /**
     * Creates a compact version of the styled button for use within cards.
     */
    private JButton createSmallButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        button.setBackground(new Color(200, 200, 200));
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(4, 8, 4, 8));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(180, 180, 180));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(200, 200, 200));
            }
        });
        
        return button;
    }
    /**
     * Displays the empty-cart screen with icon, text, and a button to return
     * to browsing.
     */
    private void showEmptyState() {
        JPanel emptyState = new JPanel();
        emptyState.setLayout(new BoxLayout(emptyState, BoxLayout.Y_AXIS));
        emptyState.setBackground(bgColor);
        emptyState.setBorder(new EmptyBorder(80, 20, 80, 20));
        emptyState.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emptyIcon = new JLabel("🛒");
        emptyIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emptyText = new JLabel("Your cart is empty");
        emptyText.setFont(new Font("Segoe UI", Font.BOLD, 18));
        emptyText.setForeground(secondaryText);
        emptyText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emptySubtext = new JLabel("Add some travel items to get started!");
        emptySubtext.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        emptySubtext.setForeground(secondaryText);
        emptySubtext.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton browseButton = createStyledButton("Browse Flights", accentColor);
        browseButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        browseButton.addActionListener(e -> {
            dispose();
        });

        emptyState.add(emptyIcon);
        emptyState.add(Box.createVerticalStrut(20));
        emptyState.add(emptyText);
        emptyState.add(Box.createVerticalStrut(10));
        emptyState.add(emptySubtext);
        emptyState.add(Box.createVerticalStrut(20));
        emptyState.add(browseButton);

        reservationsPanel.add(emptyState);
    }
    /**
     * Updates the displayed total price in the footer.
     */
    private void updateTotal() {
        totalLabel.setText(String.format("Total: $%.2f", totalAmount));
    }

    /**
     * Clears all reservations after user confirmation and refreshes the UI.
     */
    private void clearCart() {
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to clear your entire cart?",
            "Clear Cart",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            // Use reflection to clear reservations if possible, otherwise just clear local state
            clearReservationsUsingReflection();
            loadReservations();
            showToast("Cart cleared successfully");
        }
    }
    /**
     * Attempts to clear ReservationService's internal reservation list using
     * reflection. Falls back to local state if reflection fails.
     */
    private void clearReservationsUsingReflection() {
        try {
            // Try to clear using reflection
            java.lang.reflect.Field field = ReservationService.class.getDeclaredField("reservations");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.List<Object> reservations = (java.util.List<Object>) field.get(null);
            reservations.clear();
        } catch (Exception e) {
            // If reflection fails, just work with local state
            System.out.println("Could not clear reservations via reflection, using local state only");
        }
    }
    /**
     * Removes a single reservation at a specific index using reflection.
     */
    private void removeReservationUsingReflection(int index) {
        try {
            // Try to remove using reflection
            java.lang.reflect.Field field = ReservationService.class.getDeclaredField("reservations");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.List<Object> reservations = (java.util.List<Object>) field.get(null);
            if (index >= 0 && index < reservations.size()) {
                reservations.remove(index);
            }
        } catch (Exception e) {
            // If reflection fails, just work with local state
            System.out.println("Could not remove reservation via reflection, using local state only");
        }
    }
    /**
     * Opens a checkout dialog showing summary and confirm/cancel options.
     */
    private void proceedToCheckout() {
        if (currentReservations.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Your cart is empty. Add some items before checkout.",
                "Empty Cart",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Show checkout dialog
        JDialog checkoutDialog = new JDialog(this, "Checkout", true);
        checkoutDialog.setSize(400, 300);
        checkoutDialog.setLayout(new BorderLayout());
        checkoutDialog.setLocationRelativeTo(this);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Checkout Summary");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JTextArea summary = new JTextArea();
        summary.setEditable(false);
        summary.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        summary.setBackground(bgColor);
        summary.setText(String.format("Total Amount: $%.2f\n\nItems in cart: %d\n\nThank you for your purchase!", 
            totalAmount, currentReservations.size()));

        JButton confirmButton = createStyledButton("Confirm Purchase", new Color(46, 204, 113));
        confirmButton.addActionListener(e -> {
            checkoutDialog.dispose();
            completePurchase();
        });

        JButton cancelButton = createStyledButton("Cancel", new Color(149, 165, 166));
        cancelButton.addActionListener(e -> checkoutDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);

        content.add(title, BorderLayout.NORTH);
        content.add(new JScrollPane(summary), BorderLayout.CENTER);
        content.add(buttonPanel, BorderLayout.SOUTH);

        checkoutDialog.add(content);
        checkoutDialog.setVisible(true);
    }
    /**
     * Finalizes the purchase by clearing reservations and reloading the cart.
     */
    private void completePurchase() {
        // Clear reservations using reflection
        clearReservationsUsingReflection();
        loadReservations();
        showToast("Purchase completed successfully! Thank you!");
    }

    /**
     * Placeholder method for editing a reservation. Displays a toast.
     */
    private void editReservation(Object reservation) {
        // Placeholder for edit functionality
        showToast("Edit feature coming soon!");
    }

    /**
     * Removes an item from the cart after confirmation.
     */
    private void removeReservation(int index) {
        int result = JOptionPane.showConfirmDialog(this,
            "Remove this item from your cart?",
            "Remove Item",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            // Remove using reflection
            removeReservationUsingReflection(index);
            loadReservations();
            showToast("Item removed from cart");
        }
    }

    /**
     * Displays a small popup notification for cart actions.
     */
    private void showToast(String message) {
        JOptionPane.showMessageDialog(this, message, "Cart", JOptionPane.INFORMATION_MESSAGE);
    }
}