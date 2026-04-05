package gui;

import model.Flight;
import service.FlightService;
import service.SmartCityService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Window that displays detailed information about a module item.
 * This class is used for showing additional text details related to the
 * selected item (flights, hotels, attractions, etc.).
 * It provides a simple scrollable text section and a close button.
 */
public class ModuleWindow extends JFrame {
	
    // Window used to display detailed information about a selected module
    private static final long serialVersionUID = 1L;

    private final String module;
    private Flight selectedFlight;
    private String selectedDestination;

    private final boolean darkMode;
    private final Color bg, fg, cardBg, accent;
    private Color border, selectedBg, hoverBg;

    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> itemList = new JList<>(listModel);
    private final JTextField searchField = new JTextField(20);
    private final JComboBox<String> sortBox = new JComboBox<>(new String[]{"A → Z", "Z → A", "Price (low to high)", "Price (high to low)"});
    private final JTextArea detailArea = new JTextArea();
    private JLabel statusLabel;
    private JPanel flightStatusPanel;
    private JLabel flightStatusLabel1, flightStatusLabel2; // Added for flight status updates
    /**
     * Constructor for the main module window.
     * 
     * This sets up the initial theme colors, loads any preselected flight
     * information, and builds the full UI. It is responsible only for 
     * initialization and does not perform any business logic.
     */
    public ModuleWindow(String module, Flight preselectedFlight, boolean darkMode, 
                        Color bg, Color fg, Color cardBg, Color accent) {
        // Basic window setup
        this.module = module;
        this.selectedFlight = preselectedFlight;
        this.selectedDestination = preselectedFlight == null ? null : preselectedFlight.getDestination();
        this.darkMode = darkMode;
        this.bg = bg;
        this.fg = fg;
        this.cardBg = cardBg;
        this.accent = accent;
        
        initThemeColors();
        setupUI();
        populateInitial();
    }
    /**
     * Displays a  meme popup used as a playful "sponsorship" interaction.
     * 
     * The method loads an image from the resources folder and shows it inside 
     * a JOptionPane. It is primarily for user engagement and does not affect 
     * any application functionality.
     */
    private void showMemePopup() {
        java.net.URL memes = getClass().getResource("/gui/meme.jpeg");
        System.out.println("Loaded image from: " + memes); // debug

        ImageIcon icon = (memes == null) ? null : new ImageIcon(memes);

        JOptionPane.showMessageDialog(
                this,
                "PLZ SPONSER PLSSSS 💀",
                "Redeem Ad Sponsership ;)",
                JOptionPane.PLAIN_MESSAGE,
                icon
        );
    }



//sets the colours for the different modes
    private void initThemeColors() {
        if (darkMode) {
            border = new Color(0x404040);
            selectedBg = new Color(0x3a3a3a);
            hoverBg = new Color(0x333333);
        } else {
            border = new Color(0xe0e0e0);
            selectedBg = new Color(0xe3f2fd);
            hoverBg = new Color(0xf5f5f5);
        }
    }
//Layout of the UI
    private void setupUI() {
        setTitle(module + " - ARNS");
        setSize(1000, 700);
        setLayout(new BorderLayout(0, 0));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(bg);

        // Header
        add(createHeader(), BorderLayout.NORTH);

        // Main content (split: list + details)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createListPanel());
        splitPane.setRightComponent(createDetailPanel());
        splitPane.setDividerLocation(600);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);
        
        add(splitPane, BorderLayout.CENTER);

        // Bottom controls
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(15, 0));
        header.setBackground(bg);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, border),
            new EmptyBorder(15, 20, 15, 20)
        ));

        // Left: Module title with icon
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);
        
        JLabel icon = new JLabel(getModuleIcon(module));
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        
        JLabel title = new JLabel(module);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(fg);
        
        leftPanel.add(icon);
        leftPanel.add(title);

        // Right: Module selector
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        JLabel switchLabel = new JLabel("Switch to:");
        switchLabel.setForeground(fg);
        switchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        String[] modules = {"Flights", "Hotels", "Cars", "Attractions", "Events", "Passes", "Restaurants", "Tours"};
        JComboBox<String> moduleSelector = new JComboBox<>(modules);
        moduleSelector.setSelectedItem(module);
        moduleSelector.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        moduleSelector.addActionListener(e -> {
            String sel = (String) moduleSelector.getSelectedItem();
            if (!sel.equals(module)) {
                switchToModule(sel);
            }
        });
        
        rightPanel.add(switchLabel);
        rightPanel.add(moduleSelector);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }
    /**
     * Creates the main panel containing a searchable and sortable list of items.
     * 
     * This panel consists of two main sections:
     * 1. Controls at the top for flight status, search, sort, and action buttons.
     * 2. A scrollable list in the center displaying the items.
     * 
     * @return JPanel fully configured with search/sort controls and the item list.
     */
    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 20, 15, 10));

        // Controls section
        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        controls.setOpaque(false);

        // Flight status (if applicable)
        flightStatusPanel = createFlightStatusPanel();
        controls.add(flightStatusPanel);
        controls.add(Box.createVerticalStrut(10));

        // Search and sort
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setOpaque(false);
        
        JLabel searchLabel = new JLabel("🔍");
        searchLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        
        searchField.setPreferredSize(new Dimension(250, 32));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBackground(cardBg);
        searchField.setForeground(fg);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(border),
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        sortBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sortBox.setPreferredSize(new Dimension(120, 32));
        
        JButton applyBtn = createStyledButton("Apply", accent);
        applyBtn.addActionListener(e -> refreshList());
        
        JButton clearBtn = createStyledButton("Clear", new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 60));
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            sortBox.setSelectedIndex(0);
            refreshList();
        });
        
        searchPanel.add(searchLabel);
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Sort:"));
        searchPanel.add(sortBox);
        searchPanel.add(applyBtn);
        searchPanel.add(clearBtn);
        
        controls.add(searchPanel);

        // Status label
        statusLabel = new JLabel("Loading...");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 150));
        statusLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        controls.add(statusLabel);

        panel.add(controls, BorderLayout.NORTH);

        // List
        itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemList.setBackground(cardBg);
        itemList.setForeground(fg);
        itemList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        itemList.setFixedCellHeight(40);
        itemList.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        itemList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateDetailArea();
            }
        });
        
        JScrollPane scroll = new JScrollPane(itemList);
        scroll.setBorder(BorderFactory.createLineBorder(border));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFlightStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(true);
        panel.setBackground(darkMode ? new Color(0x2a4a2a) : new Color(0xe8f5e9));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(darkMode ? new Color(0x4caf50) : new Color(0x66bb6a)),
            new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel icon = new JLabel("✈️");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        flightStatusLabel1 = new JLabel(selectedFlight != null ? "Selected Flight" : "No Flight Selected");
        flightStatusLabel1.setFont(new Font("Segoe UI", Font.BOLD, 13));
        flightStatusLabel1.setForeground(fg);

        flightStatusLabel2 = new JLabel(selectedFlight != null 
            ? selectedFlight.getOrigin() + " → " + selectedFlight.getDestination()
            : "Please select a flight to view location-based options");
        flightStatusLabel2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        flightStatusLabel2.setForeground(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 180));

        textPanel.add(flightStatusLabel1);
        textPanel.add(flightStatusLabel2);

        panel.add(icon, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);

        panel.setVisible(!module.equals("Flights"));
        return panel;
    }

    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 10, 15, 20));

        JLabel detailTitle = new JLabel("Details");
        detailTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        detailTitle.setForeground(fg);
        detailTitle.setBorder(new EmptyBorder(0, 0, 10, 0));

        detailArea.setEditable(false);
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);
        detailArea.setBackground(cardBg);
        detailArea.setForeground(fg);
        detailArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detailArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        detailArea.setText("Select an item to view details");

        JScrollPane scrollDetail = new JScrollPane(detailArea);
        scrollDetail.setBorder(BorderFactory.createLineBorder(border));

        // Action buttons
        JPanel actions = new JPanel();
        actions.setLayout(new GridLayout(0, 1, 0, 10));
        actions.setOpaque(false);

        JButton bookBtn = createStyledButton("Add to Cart", new Color(0x4caf50));
        bookBtn.addActionListener(e -> bookItem());
        
        actions.add(bookBtn);

        panel.add(detailTitle, BorderLayout.NORTH);
        panel.add(scrollDetail, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bg);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, border),
            new EmptyBorder(12, 20, 12, 20)
        ));

        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftButtons.setOpaque(false);

        JButton selectFlightBtn = createStyledButton("Select Flight", accent);
        selectFlightBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        selectFlightBtn.setForeground(Color.WHITE);
        selectFlightBtn.setFocusPainted(false);
        selectFlightBtn.setBorderPainted(false);
        selectFlightBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        selectFlightBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
        selectFlightBtn.addActionListener(e -> selectFlight());

        JButton viewReservationsBtn = createStyledButton("Cart", new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 120));
        viewReservationsBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        viewReservationsBtn.setBackground(new Color(0x4caf50));
        viewReservationsBtn.setForeground(Color.WHITE);
        viewReservationsBtn.setFocusPainted(false);
        viewReservationsBtn.setBorderPainted(false);
        viewReservationsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewReservationsBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        viewReservationsBtn.addActionListener(e -> new ReservationWindow());

        leftButtons.add(selectFlightBtn);
        leftButtons.add(viewReservationsBtn);

        panel.add(leftButtons, BorderLayout.WEST);

        return panel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(color);
        btn.setForeground(darkMode || color.equals(accent) || color.getGreen() > 150 ? Color.WHITE : fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        return btn;
    }

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
            default: return "📍";
        }
    }

    private void switchToModule(String newModule) {
    	showMemePopup();
        ModuleWindow w = new ModuleWindow(newModule, selectedFlight, darkMode, bg, fg, cardBg, accent);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        w.setLocation((screen.width - w.getWidth()) / 2, (screen.height - w.getHeight()) / 2);
        w.setVisible(true);
        dispose();
    }

    private void populateInitial() {
        refreshList();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void selectFlight() {
        Flight picked = showFlightSelectionDialog();
        if (picked != null) {
            selectedFlight = picked;
            selectedDestination = picked.getDestination();
            
            // Update the flight status labels directly
            if (flightStatusLabel1 != null && flightStatusLabel2 != null) {
                flightStatusLabel1.setText("Selected Flight");
                flightStatusLabel2.setText(selectedFlight.getOrigin() + " → " + selectedFlight.getDestination());
                
                // Make sure the panel is visible for non-flight modules
                flightStatusPanel.setVisible(!module.equals("Flights"));
                
                // Force UI refresh
                flightStatusPanel.revalidate();
                flightStatusPanel.repaint();
            }
            
            showToast("Flight selected: " + selectedDestination);
            refreshList();
        }
    }

    private void bookItem() {
        String sel = itemList.getSelectedValue();
        if (sel == null) {
            showToast("Please select an item first");
            return;
        }
        if (!module.equals("Flights") && selectedDestination == null) {
            JOptionPane.showMessageDialog(this, 
                "Please select a flight first to access " + module.toLowerCase() + " at your destination.",
                "Flight Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        new BookingGUI(module, sel);
    }

    private void updateDetailArea() {
        String sel = itemList.getSelectedValue();
        if (sel == null) {
            detailArea.setText("Select an item to view details");
            return;
        }
        
        StringBuilder details = new StringBuilder();
        details.append("SELECTED ITEM\n");
        details.append("=".repeat(36)).append("\n\n");
        details.append(sel).append("\n\n");
        
        if (selectedDestination != null) {
            details.append("Destination: ").append(selectedDestination).append("\n");
        }
        
        // Add detailed information from SmartCityService
        details.append("\n").append("=".repeat(36)).append("\n");
        details.append("DETAILED INFORMATION:\n\n");
        
        try {
            // Try to get detailed information from SmartCityService
            String city = selectedDestination == null ? "" : selectedDestination;
            List<?> detailedItems = getDetailedItemsForModule(module, city);
            
            // Find the matching item based on the display text
            for (Object item : detailedItems) {
                if (matchesDisplayText(item, sel)) {
                    details.append(getDetailedDescription(item));
                    break;
                }
            }
        } catch (Exception e) {
            details.append("Additional details not available.\n");
            details.append("This is a sample item - real data would show full description here.");
        }
        
        detailArea.setText(details.toString());
        detailArea.setCaretPosition(0);
    }

    private List<?> getDetailedItemsForModule(String module, String city) {
        switch (module) {
            case "Hotels":
                return SmartCityService.getHotelsForCity(city);
            case "Restaurants":
                return SmartCityService.getRestaurantsForCity(city);
            case "Cars":
                return SmartCityService.getCarsForCity(city);
            case "Attractions":
                return SmartCityService.getAttractionsForCity(city);
            case "Events":
                return SmartCityService.getEventsForCity(city);
            case "Passes":
                return SmartCityService.getPassesForCity(city);
            case "Tours":
                return SmartCityService.getToursForCity(city);
            default:
                return new ArrayList<>();
        }
    }

    private boolean matchesDisplayText(Object item, String displayText) {
        // Check if this item matches the display text from the list
        if (item instanceof SmartCityService.Hotel) {
            SmartCityService.Hotel hotel = (SmartCityService.Hotel) item;
            return displayText.contains(hotel.name);
        } else if (item instanceof SmartCityService.Restaurant) {
            SmartCityService.Restaurant restaurant = (SmartCityService.Restaurant) item;
            return displayText.contains(restaurant.name);
        } else if (item instanceof SmartCityService.Car) {
            SmartCityService.Car car = (SmartCityService.Car) item;
            return displayText.contains(car.company + " " + car.model);
        } else if (item instanceof SmartCityService.Attraction) {
            SmartCityService.Attraction attraction = (SmartCityService.Attraction) item;
            return displayText.contains(attraction.name);
        } else if (item instanceof SmartCityService.Event) {
            SmartCityService.Event event = (SmartCityService.Event) item;
            return displayText.contains(event.name);
        } else if (item instanceof SmartCityService.Pass) {
            SmartCityService.Pass pass = (SmartCityService.Pass) item;
            return displayText.contains(pass.name);
        } else if (item instanceof SmartCityService.Tour) {
            SmartCityService.Tour tour = (SmartCityService.Tour) item;
            return displayText.contains(tour.name);
        }
        return false;
    }

    private String getDetailedDescription(Object item) {
        StringBuilder desc = new StringBuilder();
        
        if (item instanceof SmartCityService.Hotel) {
            SmartCityService.Hotel hotel = (SmartCityService.Hotel) item;
            desc.append("Hotel: ").append(hotel.name).append("\n");
            desc.append("Location: ").append(hotel.city).append("\n");
            desc.append("Rating: ").append(hotel.stars).append(" stars\n");
            desc.append("Price: $").append(String.format("%.2f", hotel.pricePerNight)).append(" per night\n");
            desc.append("Description: ").append(hotel.description).append("\n");
            
        } else if (item instanceof SmartCityService.Restaurant) {
            SmartCityService.Restaurant restaurant = (SmartCityService.Restaurant) item;
            desc.append("Restaurant: ").append(restaurant.name).append("\n");
            desc.append("Location: ").append(restaurant.city).append("\n");
            desc.append("Cuisine: ").append(restaurant.cuisine).append("\n");
            desc.append("Average Price: $").append(String.format("%.2f", restaurant.avgPrice)).append("\n");
            
        } else if (item instanceof SmartCityService.Car) {
            SmartCityService.Car car = (SmartCityService.Car) item;
            desc.append("Car: ").append(car.company).append(" ").append(car.model).append("\n");
            desc.append("Location: ").append(car.city).append("\n");
            desc.append("Type: ").append(car.type).append("\n");
            desc.append("Price: $").append(String.format("%.2f", car.pricePerDay)).append(" per day\n");
            
        } else if (item instanceof SmartCityService.Attraction) {
            SmartCityService.Attraction attraction = (SmartCityService.Attraction) item;
            desc.append("Attraction: ").append(attraction.name).append("\n");
            desc.append("Location: ").append(attraction.city).append("\n");
            desc.append("Type: ").append(attraction.type).append("\n");
            desc.append("Price: $").append(String.format("%.2f", attraction.price)).append("\n");
            
        } else if (item instanceof SmartCityService.Event) {
            SmartCityService.Event event = (SmartCityService.Event) item;
            desc.append("Event: ").append(event.name).append("\n");
            desc.append("Location: ").append(event.city).append("\n");
            desc.append("Date: ").append(event.date).append("\n");
            desc.append("Price: $").append(String.format("%.2f", event.price)).append("\n");
            
        } else if (item instanceof SmartCityService.Pass) {
            SmartCityService.Pass pass = (SmartCityService.Pass) item;
            desc.append("Pass: ").append(pass.name).append("\n");
            desc.append("Location: ").append(pass.city).append("\n");
            desc.append("Duration: ").append(pass.durationDays).append(" days\n");
            desc.append("Price: $").append(String.format("%.2f", pass.price)).append("\n");
            
        } else if (item instanceof SmartCityService.Tour) {
            SmartCityService.Tour tour = (SmartCityService.Tour) item;
            desc.append("Tour: ").append(tour.name).append("\n");
            desc.append("Location: ").append(tour.city).append("\n");
            desc.append("Duration: ").append(tour.duration).append("\n");
            desc.append("Price: $").append(String.format("%.2f", tour.price)).append("\n");
        }
        
        return desc.toString();
    }

    private void showToast(String message) {
        statusLabel.setText("ℹ️ " + message);
        Timer timer = new Timer(3000, e -> statusLabel.setText(""));
        timer.setRepeats(false);
        timer.start();
    }

    private Flight showFlightSelectionDialog() {
        JDialog d = new JDialog(this, "Select Flight", true);
        d.setSize(750, 450);
        d.setLayout(new BorderLayout(10, 10));
        d.getContentPane().setBackground(bg);

        JLabel header = new JLabel("Choose Your Flight");
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setForeground(fg);
        header.setBorder(new EmptyBorder(10, 10, 10, 10));
        d.add(header, BorderLayout.NORTH);

        DefaultListModel<String> lm = new DefaultListModel<>();
        List<Flight> flights = FlightService.getAllFlights();
        for (Flight f : flights) lm.addElement(f.toString());
        
        JList<String> jlist = new JList<>(lm);
        jlist.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        jlist.setBackground(cardBg);
        jlist.setForeground(fg);
        jlist.setFixedCellHeight(35);
        
        JScrollPane scroll = new JScrollPane(jlist);
        scroll.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(0, 10, 10, 10),
            BorderFactory.createLineBorder(border)
        ));
        d.add(scroll, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttons.setOpaque(false);
        
        JButton cancel = createStyledButton("Cancel", accent);
        cancel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cancel.setBackground(new Color(0xBA2D2D));
        cancel.setForeground(Color.WHITE);
        cancel.setFocusPainted(false);
        cancel.setBorderPainted(false);
        cancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancel.setBorder(new EmptyBorder(10, 20, 10, 20));
        cancel.addActionListener(e -> d.dispose());
        
        JButton select = createStyledButton("Select Flight", accent);
        select.setFont(new Font("Segoe UI", Font.BOLD, 13));
        select.setForeground(Color.WHITE);
        select.setFocusPainted(false);
        select.setBorderPainted(false);
        select.setCursor(new Cursor(Cursor.HAND_CURSOR));
        select.setBorder(new EmptyBorder(10, 20, 10, 20));
        select.addActionListener(e -> d.dispose());
        
        buttons.add(cancel);
        buttons.add(select);
        d.add(buttons, BorderLayout.SOUTH);

        d.setLocationRelativeTo(this);
        d.setVisible(true);

        int idx = jlist.getSelectedIndex();
        return idx >= 0 ? flights.get(idx) : null;
    }

    // Helper class for sortable items
    private class SortableItem {
        String displayText;
        double price;
        String name;
        
        SortableItem(String displayText, double price, String name) {
            this.displayText = displayText;
            this.price = price;
            this.name = name;
        }
    }

    // Helper method to sort items based on the selected sort option
    private void sortItems(List<SortableItem> items, String sort) {
        if ("Price (low to high)".equals(sort)) {
            items.sort((a, b) -> Double.compare(a.price, b.price));
        } else if ("Price (high to low)".equals(sort)) {
            items.sort((a, b) -> Double.compare(b.price, a.price));
        } else if ("Z → A".equals(sort)) {
            items.sort((a, b) -> b.name.compareToIgnoreCase(a.name));
        } else { // "A → Z" - default
            items.sort((a, b) -> a.name.compareToIgnoreCase(b.name));
        }
    }

    private void refreshList() {
        listModel.clear();
        statusLabel.setText("Loading items...");
        
        String q = searchField.getText().trim().toLowerCase();
        String sort = (String) sortBox.getSelectedItem();

        if ("Flights".equals(module)) {
            List<Flight> flights = FlightService.getAllFlights();
            List<Flight> filtered = new ArrayList<>();
            for (Flight f : flights) {
                if (q.isEmpty() || f.toString().toLowerCase().contains(q)) {
                    filtered.add(f);
                }
            }
            
            // Sort flights
            if ("Price (low to high)".equals(sort)) {
                filtered.sort((a, b) -> Double.compare(a.getPrice(), b.getPrice()));
            } else if ("Price (high to low)".equals(sort)) {
                filtered.sort((a, b) -> Double.compare(b.getPrice(), a.getPrice()));
            } else if ("Z → A".equals(sort)) {
                filtered.sort((a, b) -> b.toString().compareToIgnoreCase(a.toString()));
            } else { // "A → Z" - default
                filtered.sort((a, b) -> a.toString().compareToIgnoreCase(b.toString()));
            }
            
            for (Flight f : filtered) listModel.addElement(f.toString());
            statusLabel.setText(filtered.size() + " flight(s) found");
            return;
        }

        // For other modules
        boolean hasSmart = true;
        try {
            Class.forName("service.SmartCityService");
        } catch (Throwable ex) {
            hasSmart = false;
        }

        if (!hasSmart) {
            // Create sample items with sortable data
            List<SortableItem> sampleItems = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                double price = 50 + (i * 10) + (Math.random() * 100); // Varying prices for sorting
                String itemText = module + " Item " + i + " (Destination: " + 
                    (selectedDestination == null ? "—" : selectedDestination) + ") — $" + String.format("%.2f", price);
                sampleItems.add(new SortableItem(itemText, price, "Item " + i));
            }
            
            // Sort sample items
            sortItems(sampleItems, sort);
            
            for (SortableItem item : sampleItems) {
                listModel.addElement(item.displayText);
            }
            statusLabel.setText("10 items (sample data)");
            return;
        }

        // SmartCityService integration with proper sorting
        try {
            service.SmartCityService sc = new service.SmartCityService();
            List<SortableItem> items = new ArrayList<>();
            
            String city = selectedDestination == null ? "" : selectedDestination;
            
            switch (module) {
                case "Hotels":
                    for (service.SmartCityService.Hotel h : SmartCityService.getHotelsForCity(city)) {
                        if (q.isEmpty() || (h.name + " " + h.description).toLowerCase().contains(q))
                            items.add(new SortableItem(
                                h.name + " — " + h.stars + "★ — $" + h.pricePerNight + " per night",
                                h.pricePerNight,
                                h.name
                            ));
                    }
                    break;
                case "Restaurants":
                    for (service.SmartCityService.Restaurant r : SmartCityService.getRestaurantsForCity(city)) {
                        if (q.isEmpty() || r.name.toLowerCase().contains(q))
                            items.add(new SortableItem(
                                r.name + " — " + r.cuisine + " — avg $" + r.avgPrice,
                                r.avgPrice,
                                r.name
                            ));
                    }
                    break;
                case "Cars":
                    for (service.SmartCityService.Car c : SmartCityService.getCarsForCity(city)) {
                        if (q.isEmpty() || (c.company + " " + c.model).toLowerCase().contains(q))
                            items.add(new SortableItem(
                                c.company + " " + c.model + " — $" + c.pricePerDay + "/day",
                                c.pricePerDay,
                                c.company + " " + c.model
                            ));
                    }
                    break;
                case "Attractions":
                    for (service.SmartCityService.Attraction a : SmartCityService.getAttractionsForCity(city)) {
                        if (q.isEmpty() || a.name.toLowerCase().contains(q))
                            items.add(new SortableItem(
                                a.name + " — " + a.type + " — $" + a.price,
                                a.price,
                                a.name
                            ));
                    }
                    break;
                case "Events":
                    for (service.SmartCityService.Event ev : SmartCityService.getEventsForCity(city)) {
                        if (q.isEmpty() || ev.name.toLowerCase().contains(q))
                            items.add(new SortableItem(
                                ev.name + " — " + ev.date + " — $" + ev.price,
                                ev.price,
                                ev.name
                            ));
                    }
                    break;
                case "Passes":
                    for (service.SmartCityService.Pass p : SmartCityService.getPassesForCity(city)) {
                        if (q.isEmpty() || p.name.toLowerCase().contains(q))
                            items.add(new SortableItem(
                                p.name + " — " + p.durationDays + " days — $" + p.price,
                                p.price,
                                p.name
                            ));
                    }
                    break;
                case "Tours":
                    for (service.SmartCityService.Tour t : SmartCityService.getToursForCity(city)) {
                        if (q.isEmpty() || t.name.toLowerCase().contains(q))
                            items.add(new SortableItem(
                                t.name + " — " + t.duration + " — $" + t.price,
                                t.price,
                                t.name
                            ));
                    }
                    break;
                default:
                    // No data
            }
            
            // Sort the items
            sortItems(items, sort);
            
            for (SortableItem item : items) {
                listModel.addElement(item.displayText);
            }
            statusLabel.setText(items.size() + " item(s) found");
        } catch (Throwable ex) {
            // Fallback with sortable sample data
            List<SortableItem> fallbackItems = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                double price = 50 + (i * 15) + (Math.random() * 150);
                String itemText = module + " Item " + i + " (fallback) — $" + String.format("%.2f", price);
                fallbackItems.add(new SortableItem(itemText, price, "Item " + i));
            }
            
            sortItems(fallbackItems, sort);
            
            for (SortableItem item : fallbackItems) {
                listModel.addElement(item.displayText);
            }
            statusLabel.setText("10 items (fallback data)");
        }
    }
}