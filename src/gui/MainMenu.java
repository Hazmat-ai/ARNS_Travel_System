package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

//MainMenu: Central dashboard window for the ARNS Travel System.
//Builds a modern Swing UI with theme switching, module cards, animations,
//and structured header/content/footer layout.

public class MainMenu extends JFrame {
    private static final long serialVersionUID = 1L;

    // Theme colors
    private Color bg, fg, cardBg, cardHoverBg, accent, accentHover, border;
    private boolean darkMode = false;

    // Module metadata with icons and descriptions
    private static final String[][] MODULE_DATA = {
        {"Flights", "✈️", "Search and book flights worldwide"},
        {"Hotels", "🏨", "Find comfortable accommodations"},
        {"Cars", "🚗", "Rent vehicles for your trip"},
        {"Attractions", "🎭", "Discover local attractions"},
        {"Events", "🎫", "Browse upcoming events"},
        {"Passes", "🎟️", "Get city passes and deals"},
        {"Restaurants", "🍽️", "Find great dining options"},
        {"Tours", "🗺️", "Book guided tours"}
    };
    // Constructor:
    // Initializes the main window, applies system look-and-feel,
    // loads theme colors, builds UI sections, and displays the menu.
    public MainMenu() {
        try { 
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        } catch (Exception ignored) {}

        setTitle("ARNS Travel System");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        updateThemeColors();
        buildUI();
        
        setLocationRelativeTo(null);
        setVisible(true);
    }
    // Builds the full UI layout by assembling:
    // - Header bar
    // - Grid of module cards
    // - Footer section
    // Also refreshes layout if the theme changes.
    private void buildUI() {
        getContentPane().removeAll();
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(bg);

        // Header with title and controls
        JPanel header = createHeader();
        add(header, BorderLayout.NORTH);

        // Main content area with modules
        JPanel content = createContent();
        add(content, BorderLayout.CENTER);

        // Footer
        JPanel footer = createFooter();
        add(footer, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }
    // Header section:
    // Contains title, subtitle, globe icon, and the light/dark mode toggle.
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setBackground(bg);
        header.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Left: Title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        
        JLabel icon = new JLabel("🌍");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        
        JLabel title = new JLabel(" ARS Travel");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(fg);
        
        JLabel subtitle = new JLabel("Select a module to get started");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 150));
        
        JPanel titleStack = new JPanel();
        titleStack.setLayout(new BoxLayout(titleStack, BoxLayout.Y_AXIS));
        titleStack.setOpaque(false);
        
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleRow.setOpaque(false);
        titleRow.add(icon);
        titleRow.add(title);
        
        titleStack.add(titleRow);
        titleStack.add(subtitle);
        leftPanel.add(titleStack);

        // Right: Controls
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setOpaque(false);
        
        JToggleButton darkToggle = createStyledToggle();
        rightPanel.add(darkToggle);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }
    // Creates the Light/Dark mode toggle button.
    // When pressed, theme colors are recalculated and UI refreshes.
    private JToggleButton createStyledToggle() {
        JToggleButton toggle = new JToggleButton(darkMode ? "Light" : "Dark");
        toggle.setSelected(darkMode);
        toggle.setFocusable(false);
        toggle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        toggle.setBorder(new EmptyBorder(8, 16, 8, 16));
        toggle.setBackground(cardBg);
        toggle.setForeground(fg);
        toggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        toggle.addActionListener(e -> {
            darkMode = toggle.isSelected();
            updateThemeColors();
            buildUI();
        });

        return toggle;
    }
    // Builds the main content area containing the 2x4 grid of module cards.

    private JPanel createContent() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 30, 30, 30));

        JPanel grid = new JPanel(new GridLayout(2, 4, 20, 20));
        grid.setOpaque(false);

        // Add all module cards
        for (String[] moduleData : MODULE_DATA) {
            grid.add(createModuleCard(moduleData[0], moduleData[1], moduleData[2]));
        }

        wrapper.add(grid, BorderLayout.CENTER);
        return wrapper;
    }
    // Creates a single module card with:
    // - Icon + Title
    // - Small description
    // - "Open Module" button
    // Includes hover animations and click-to-open behavior.
    private JPanel createModuleCard(String name, String icon, String description) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(0, 12));
        card.setBackground(cardBg);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(border, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Icon and title
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        header.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nameLabel.setForeground(fg);
        
        header.add(iconLabel);
        header.add(nameLabel);

        // Description
        JTextArea desc = new JTextArea(description);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setEditable(false);
        desc.setOpaque(false);
        desc.setForeground(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 180));
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        desc.setBorder(null);

        // Open button
        JButton openBtn = new JButton("Open Module →");
        openBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        openBtn.setBackground(accent);
        openBtn.setForeground(Color.WHITE);
        openBtn.setFocusPainted(false);
        openBtn.setBorderPainted(false);
        openBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        openBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
        openBtn.addActionListener(e -> openModuleAnimated(name));

        // Hover effects
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(cardHoverBg);
                openBtn.setBackground(accentHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(cardBg);
                openBtn.setBackground(accent);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                openModuleAnimated(name);
            }
        });

        card.add(header, BorderLayout.NORTH);
        card.add(desc, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(openBtn);
        card.add(btnPanel, BorderLayout.SOUTH);

        return card;
    }
    // Footer section:
    // Displays platform name and version number at bottom of window.
    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(bg);
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, border),
            new EmptyBorder(15, 30, 15, 30)
        ));

        JLabel footerText = new JLabel("ARNS — Airline Reservation, Notification & Smart City Platform");
        footerText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerText.setForeground(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 150));

        JLabel version = new JLabel("v3.5");
        version.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        version.setForeground(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 100));

        footer.add(footerText, BorderLayout.WEST);
        footer.add(version, BorderLayout.EAST);

        return footer;
    }
    
    // Updates theme colors (Light vs Dark mode).
    // Called whenever the user toggles dark mode.

    private void updateThemeColors() {
        if (darkMode) {
            bg = new Color(0x1a1a1a);
            fg = new Color(0xe8e8e8);
            cardBg = new Color(0x2a2a2a);
            cardHoverBg = new Color(0x333333);
            accent = new Color(0x3b82f6);
            accentHover = new Color(0x2563eb);
            border = new Color(0x404040);
        } else {
            bg = new Color(0xf5f5f5);
            fg = new Color(0x1f1f1f);
            cardBg = Color.WHITE;
            cardHoverBg = new Color(0xfafafa);
            accent = new Color(0x2563eb);
            accentHover = new Color(0x1d4ed8);
            border = new Color(0xe0e0e0);
        }
        
        if (getContentPane() != null) {
            getContentPane().setBackground(bg);
        }
    }
    // Opens a module window with a smooth slide-up animation.
    // Uses a Timer for ease-out motion from bottom of screen to center.
    private void openModuleAnimated(String moduleName) {
        ModuleWindow w = new ModuleWindow(moduleName, null, darkMode, bg, fg, cardBg, accent);
        
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int targetX = (screen.width - w.getWidth()) / 2;
        int targetY = (screen.height - w.getHeight()) / 2;
        int startY = screen.height;
        
        w.setLocation(targetX, startY);
        w.setVisible(true);

        final int durationMs = 250;
        final int steps = 25;
        final int delay = Math.max(1, durationMs / steps);
        final int dy = startY - targetY;
        
        Timer timer = new Timer(delay, null);
        final long start = System.currentTimeMillis();
        
        timer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                float progress = (float)(System.currentTimeMillis() - start) / durationMs;
                if (progress >= 1f) {
                    w.setLocation(targetX, targetY);
                    timer.stop();
                    return;
                }
                // Ease-out animation
                float eased = 1f - (float)Math.pow(1 - progress, 3);
                int y = startY - Math.round(dy * eased);
                w.setLocation(targetX, y);
            }
        });
        timer.start();
    }
    // Entry point: launches the menu on the Swing event thread.
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainMenu::new);
    }
}