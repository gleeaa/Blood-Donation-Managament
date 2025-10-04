import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeFrame extends JFrame {

    private String currentUser;
    private String currentFullName;
    private int currentAge;
    private JPanel contentPanel;
    private JPanel dashboardPanel;
    private Map<String, Color> bloodTypeColors;
    private JLabel clockLabel;
    private Timer clockTimer;
    private JProgressBar donationProgressBar;
    private SimpleDateFormat timeFormat;
    private JPanel mainPanel;

    // Constants for styling
    private static final Color PRIMARY_COLOR = new Color(139, 0, 0);
    private static final Color SECONDARY_COLOR = new Color(240, 248, 255);
    private static final Color ACCENT_COLOR = new Color(220, 230, 240);
    private static final Color TEXT_DARK = new Color(50, 50, 50);
    private static final Color TEXT_LIGHT = Color.WHITE;
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    public HomeFrame(String username, String fullName, int age) {
        this.currentUser = username;
        this.currentFullName = fullName;
        this.currentAge = age;

        initializeBloodTypeColors();
        setupFrame();
        setupComponents();
        setupClockTimer();

        setVisible(true);
    }

    private void initializeBloodTypeColors() {
        bloodTypeColors = new HashMap<>();
        bloodTypeColors.put("A+", new Color(220, 20, 60));
        bloodTypeColors.put("A-", new Color(178, 34, 34));
        bloodTypeColors.put("B+", new Color(220, 60, 20));
        bloodTypeColors.put("B-", new Color(178, 60, 34));
        bloodTypeColors.put("AB+", new Color(165, 42, 42));
        bloodTypeColors.put("AB-", new Color(128, 0, 0));
        bloodTypeColors.put("O+", new Color(220, 120, 20));
        bloodTypeColors.put("O-", new Color(178, 95, 34));
    }

    private void setupFrame() {
        setTitle("Blood Donation Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set custom icon if available
        try {
            // Replace with actual icon path if available
            // setIconImage(new ImageIcon("path/to/icon.png").getImage());
        } catch (Exception e) {
            System.err.println("Could not load application icon: " + e.getMessage());
        }
    }

    private void setupComponents() {
        // Main panel with BorderLayout
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(SECONDARY_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Header Panel
        JPanel headerPanel = createHeaderPanel();

        // 2. Navigation Panel
        JPanel navPanel = createNavigationPanel();

        // 3. Content Panel (will hold different views)
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // 4. Status Bar
        JPanel statusPanel = createStatusPanel();

        // Set default view to dashboard
        dashboardPanel = createDashboardPanel();
        contentPanel.add(dashboardPanel, BorderLayout.CENTER);

        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(navPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Logo and Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);

        JLabel logoLabel = new JLabel("🩸");
        logoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        logoLabel.setForeground(TEXT_LIGHT);

        JLabel titleLabel = new JLabel("BLOOD DONATION MANAGEMENT SYSTEM");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_LIGHT);

        titlePanel.add(logoLabel);
        titlePanel.add(titleLabel);

        // User info panel
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);

        // Welcome message
        JLabel userLabel = new JLabel("Welcome, " + currentFullName);
        userLabel.setFont(REGULAR_FONT);
        userLabel.setForeground(TEXT_LIGHT);

        // Create logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(SMALL_FONT);
        logoutButton.setForeground(PRIMARY_COLOR);
        logoutButton.setBackground(TEXT_LIGHT);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        logoutButton.addActionListener(e -> handleLogout());

        userPanel.add(userLabel);
        userPanel.add(Box.createHorizontalStrut(15));
        userPanel.add(logoutButton);

        panel.add(titlePanel, BorderLayout.WEST);
        panel.add(userPanel, BorderLayout.EAST);

        return panel;
    }

    private void handleLogout() {
        int option = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (option == JOptionPane.YES_OPTION) {
            dispose();
            // Here you would normally open login frame
            JOptionPane.showMessageDialog(null, "You have been logged out successfully.",
                    "Logged Out", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);  // For demonstration purposes
        }
    }

    private JPanel createNavigationPanel() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(ACCENT_COLOR);
        outerPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        outerPanel.setPreferredSize(new Dimension(220, 0));

        // User profile summary at top
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBackground(new Color(200, 210, 220));
        profilePanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        // User avatar (placeholder)
        JLabel avatarLabel = new JLabel("👤");
        avatarLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // User name
        JLabel nameLabel = new JLabel(currentFullName);
        nameLabel.setFont(SUBTITLE_FONT);
        nameLabel.setForeground(TEXT_DARK);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        profilePanel.add(avatarLabel);
        profilePanel.add(Box.createVerticalStrut(10));
        profilePanel.add(nameLabel);

        // Navigation buttons
        JPanel navButtonsPanel = new JPanel();
        navButtonsPanel.setLayout(new BoxLayout(navButtonsPanel, BoxLayout.Y_AXIS));
        navButtonsPanel.setBackground(ACCENT_COLOR);
        navButtonsPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        String[] buttons = {
                "Dashboard", "Donate Blood", "Request Blood",
                "Find Donors", "Appointments", "My Donations",
                "My Requests", "My Profile"
        };

        String[] icons = {
                "🏠", "❤️", "🩸", "🔍", "📅", "📋", "📝", "👤"
        };

        for (int i = 0; i < buttons.length; i++) {
            JPanel buttonPanel = createNavButtonPanel(icons[i], buttons[i]);
            navButtonsPanel.add(buttonPanel);
            navButtonsPanel.add(Box.createVerticalStrut(8));
        }

        // Add scroll capability in case window is resized
        JScrollPane scrollPane = new JScrollPane(navButtonsPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        outerPanel.add(profilePanel, BorderLayout.NORTH);
        outerPanel.add(scrollPane, BorderLayout.CENTER);

        return outerPanel;
    }

    private JPanel createNavButtonPanel(String icon, String text) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // Create an icon label that will reliably display across systems
        JLabel iconLabel = new JLabel();

        // Map common emoji to text/symbols that render better across systems
        switch(icon) {
            case "🏠":
                iconLabel.setText("H");
                break;
            case "❤️":
                iconLabel.setText("D");
                break;
            case "🩸":
                iconLabel.setText("R");
                break;
            case "🔍":
                iconLabel.setText("F");
                break;
            case "📅":
                iconLabel.setText("A");
                break;
            case "📋":
                iconLabel.setText("M");
                break;
            case "📝":
                iconLabel.setText("Q");
                break;
            case "👤":
                iconLabel.setText("P");
                break;
            default:
                iconLabel.setText("•");
        }

        // Style the icon label
        iconLabel.setFont(new Font("Arial", Font.BOLD, 18));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setPreferredSize(new Dimension(30, 30));
        iconLabel.setOpaque(true);
        iconLabel.setBackground(PRIMARY_COLOR);
        iconLabel.setForeground(Color.WHITE);

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(REGULAR_FONT);
        textLabel.setForeground(TEXT_DARK);
        textLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(textLabel, BorderLayout.CENTER);

        // Add hover effect and click listener
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(new Color(230, 240, 250));
                textLabel.setForeground(TEXT_DARK);  // Keep text dark on hover
                iconLabel.setBackground(new Color(120, 0, 0));  // Darker shade of PRIMARY_COLOR
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(Color.WHITE);
                textLabel.setForeground(TEXT_DARK);  // Keep text dark when not hovering
                iconLabel.setBackground(PRIMARY_COLOR);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                handleNavButtonClick(text);
            }
        });

        return panel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        // Dashboard header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel dashboardTitle = new JLabel("Dashboard Overview");
        dashboardTitle.setFont(SUBTITLE_FONT);
        dashboardTitle.setIcon(new ImageIcon()); // Add icon if available

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(SMALL_FONT);
        refreshButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> showDashboard());

        headerPanel.add(dashboardTitle, BorderLayout.WEST);
        headerPanel.add(refreshButton, BorderLayout.EAST);

        // Quick Stats Cards
        JPanel statsPanel = createStatsPanel();

        // Blood Availability Panel
        JPanel bloodAvailPanel = createBloodAvailabilityPanel();

        // Recent Activity Table with filter
        JPanel activityPanel = createActivityPanel();

        // Upcoming Events Panel
        JPanel eventsPanel = createUpcomingEventsPanel();

        // Add everything to main panel with some space between
        panel.add(headerPanel);
        panel.add(statsPanel);
        panel.add(Box.createVerticalStrut(20));

        // Create a split panel for blood availability and upcoming events
        JPanel middlePanel = new JPanel(new GridLayout(1, 2, 15, 0));
        middlePanel.setOpaque(false);
        middlePanel.add(bloodAvailPanel);
        middlePanel.add(eventsPanel);
        panel.add(middlePanel);

        panel.add(Box.createVerticalStrut(20));
        panel.add(activityPanel);

        // Create a scrollable panel that takes all available space
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setOpaque(false);
        containerPanel.add(scrollPane, BorderLayout.CENTER);

        return containerPanel;
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        String[] stats = {"Total Donations", "Blood Requests", "Available Donors", "Upcoming Camps"};
        String[] values = {"64", "2", "4", "3"};
        String[] icons = {"📊", "🩸", "👥", "🏕️"};
        String[] trends = {"+12%", "+5%", "-3%", "0%"};

        for (int i = 0; i < stats.length; i++) {
            JPanel statCard = createEnhancedStatCard(stats[i], values[i], icons[i], trends[i]);
            statsPanel.add(statCard);
        }

        return statsPanel;
    }

    private JPanel createEnhancedStatCard(String title, String value, String icon, String trend) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Top section with icon and title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(SMALL_FONT);
        titleLabel.setForeground(new Color(100, 100, 100));

        topPanel.add(iconLabel, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // Middle section with value
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(PRIMARY_COLOR);

        // Bottom section with trend
        JPanel trendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        trendPanel.setOpaque(false);

        JLabel trendLabel = new JLabel(trend);
        trendLabel.setFont(SMALL_FONT);

        // Set color based on trend direction
        if (trend.startsWith("+")) {
            trendLabel.setForeground(new Color(40, 167, 69));
            trendLabel.setText("↑ " + trend);
        } else if (trend.startsWith("-")) {
            trendLabel.setForeground(new Color(220, 53, 69));
            trendLabel.setText("↓ " + trend);
        } else {
            trendLabel.setForeground(new Color(108, 117, 125));
        }

        trendPanel.add(trendLabel);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        panel.add(trendPanel, BorderLayout.SOUTH);

        // Add hover effect
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(new Color(250, 250, 250));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(Color.WHITE);
            }
        });

        return panel;
    }

    private JPanel createBloodAvailabilityPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("Blood Type Availability");
        titleLabel.setFont(SUBTITLE_FONT);

        // Create grid for blood types
        JPanel bloodGrid = new JPanel(new GridLayout(2, 4, 10, 10));
        bloodGrid.setOpaque(false);

        String[] bloodTypes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        int[] availability = {75, 30, 60, 25, 40, 15, 85, 20};

        for (int i = 0; i < bloodTypes.length; i++) {
            bloodGrid.add(createBloodTypeCard(bloodTypes[i], availability[i]));
        }

        // Add overall donation progress
        JPanel progressPanel = new JPanel(new BorderLayout(5, 5));
        progressPanel.setOpaque(false);
        progressPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel progressLabel = new JLabel("Overall Donation Target:");
        progressLabel.setFont(REGULAR_FONT);

        donationProgressBar = new JProgressBar(0, 100);
        donationProgressBar.setValue(65);
        donationProgressBar.setStringPainted(true);
        donationProgressBar.setForeground(PRIMARY_COLOR);
        donationProgressBar.setString("65% of Monthly Goal");

        progressPanel.add(progressLabel, BorderLayout.NORTH);
        progressPanel.add(donationProgressBar, BorderLayout.CENTER);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(bloodGrid, BorderLayout.CENTER);
        panel.add(progressPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBloodTypeCard(String bloodType, int percentage) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        Color bloodColor = bloodTypeColors.getOrDefault(bloodType, PRIMARY_COLOR);
        panel.setBackground(new Color(250, 250, 250));
        panel.setBorder(new LineBorder(bloodColor, 2));

        // Blood type label
        JLabel typeLabel = new JLabel(bloodType);
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        typeLabel.setForeground(bloodColor);
        typeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Availability indicator
        JProgressBar levelBar = new JProgressBar(0, 100);
        levelBar.setValue(percentage);
        levelBar.setStringPainted(true);
        levelBar.setString(percentage + "%");
        levelBar.setForeground(bloodColor);

        // Status indicator
        JLabel statusLabel = new JLabel();
        statusLabel.setFont(SMALL_FONT);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        if (percentage < 30) {
            statusLabel.setText("URGENT NEED");
            statusLabel.setForeground(new Color(220, 53, 69));
        } else if (percentage < 60) {
            statusLabel.setText("MODERATE");
            statusLabel.setForeground(new Color(255, 193, 7));
        } else {
            statusLabel.setText("SUFFICIENT");
            statusLabel.setForeground(new Color(40, 167, 69));
        }

        panel.add(typeLabel, BorderLayout.NORTH);
        panel.add(levelBar, BorderLayout.CENTER);
        panel.add(statusLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createActivityPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Header with title and filter
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Recent Activities");
        titleLabel.setFont(SUBTITLE_FONT);

        JComboBox<String> filterCombo = new JComboBox<>(new String[] {
                "All Activities", "Donations", "Requests", "Appointments"
        });
        filterCombo.setFont(SMALL_FONT);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(filterCombo, BorderLayout.EAST);

        // Table data
        String[] columns = {"Date", "Activity", "Status", "Details"};
        Object[][] data = {
                {"2023-05-15", "Blood Donation", "Completed", "A+ Blood Type"},
                {"2023-04-28", "Blood Request", "Fulfilled", "AB- Blood Type"},
                {"2023-04-10", "Donation Camp", "Registered", "City Hospital"},
                {"2023-03-22", "Blood Donation", "Completed", "O+ Blood Type"}
        };

        // Create a custom table model
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable activityTable = new JTable(model);
        activityTable.setFont(REGULAR_FONT);
        activityTable.setRowHeight(30);
        activityTable.setShowVerticalLines(false);
        activityTable.setGridColor(new Color(230, 230, 230));

        // Set custom renderer for status column
        activityTable.getColumnModel().getColumn(2).setCellRenderer(new StatusRenderer());

        // Add "View" button in last column
        activityTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        activityTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane tableScroll = new JScrollPane(activityTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(tableScroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createUpcomingEventsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("Upcoming Events");
        titleLabel.setFont(SUBTITLE_FONT);

        // Create a panel for event cards
        JPanel eventsContainer = new JPanel();
        eventsContainer.setLayout(new BoxLayout(eventsContainer, BoxLayout.Y_AXIS));
        eventsContainer.setOpaque(false);

        // Add some sample events
        eventsContainer.add(createEventCard(
                "Blood Donation Drive",
                "10-10-2025",
                "09:00 AM - 12:00 PM",
                "LBS College of Engineering"
        ));
        eventsContainer.add(Box.createVerticalStrut(10));

        eventsContainer.add(createEventCard(
                "Community Awareness Program",
                "13-10-2025",
                "10:00 AM - 01:00 PM",
                "Aster Mims Kasaragod"
        ));
        eventsContainer.add(Box.createVerticalStrut(10));

        eventsContainer.add(createEventCard(
                "Blood Donation Training",
                "25-10-2025",
                "02:00 PM - 04:00 PM",
                "Muliyar Primary Health Center"
        ));

        // Add a "View All Events" button
        JButton viewAllButton = new JButton("View All Events");
        viewAllButton.setFont(REGULAR_FONT);
        viewAllButton.setFocusPainted(false);
        viewAllButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewAllButton.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "All events view coming soon!", "Information", JOptionPane.INFORMATION_MESSAGE));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(viewAllButton);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(eventsContainer), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createEventCard(String title, String date, String time, String location) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(10, 10, 10, 10)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // Event title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Event details panel
        JPanel detailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        detailsPanel.setOpaque(false);
        detailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dateLabel = new JLabel("📅 " + date);
        dateLabel.setFont(SMALL_FONT);

        JLabel timeLabel = new JLabel("🕒 " + time);
        timeLabel.setFont(SMALL_FONT);

        JLabel locationLabel = new JLabel("📍 " + location);
        locationLabel.setFont(SMALL_FONT);

        detailsPanel.add(dateLabel);
        detailsPanel.add(timeLabel);
        detailsPanel.add(locationLabel);

        // Action button panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);
        actionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton registerButton = new JButton("Register");
        registerButton.setFont(SMALL_FONT);
        registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Registration for " + title + " coming soon!",
                "Information", JOptionPane.INFORMATION_MESSAGE));

        actionPanel.add(registerButton);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(detailsPanel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(actionPanel);

        // Add hover effect
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(new Color(248, 249, 250));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(Color.WHITE);
            }
        });

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ACCENT_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Status message on the left
        JLabel statusLabel = new JLabel("System Status: Online");
        statusLabel.setFont(SMALL_FONT);
        statusLabel.setIcon(new ImageIcon()); // Add status icon if available

        // Current time on the right
        timeFormat = new SimpleDateFormat("EEE, dd MMM yyyy, HH:mm:ss");
        clockLabel = new JLabel(timeFormat.format(new Date()));
        clockLabel.setFont(SMALL_FONT);

        panel.add(statusLabel, BorderLayout.WEST);
        panel.add(clockLabel, BorderLayout.EAST);

        return panel;
    }

    private void setupClockTimer() {
        // Update the clock every second
        clockTimer = new Timer(1000, e -> {
            if (clockLabel != null) {
                clockLabel.setText(timeFormat.format(new Date()));
            }
        });
        clockTimer.start();
    }

    private void handleNavButtonClick(String command) {
        // Special handling for options that open new frames - don't clear the content panel
        switch(command) {
            case "Donate Blood":
                DonateBloodFrame donateFrame = new DonateBloodFrame(currentUser, currentFullName, currentAge);
                styleFrame(donateFrame);
                donateFrame.setVisible(true);
                return; // Return early without clearing content

            case "Request Blood":
                RequestBloodFrame requestFrame = new RequestBloodFrame();
                styleFrame(requestFrame);
                requestFrame.setVisible(true);
                return; // Return early without clearing content

            case "Find Donors":
                FindDonorFrame donorFrame = new FindDonorFrame();
                styleFrame(donorFrame);
                donorFrame.setVisible(true);
                return; // Return early without clearing content

            case "Appointments":
                AppointmentsFrame appointmentsFrame = new AppointmentsFrame(currentUser, currentFullName);
                styleFrame(appointmentsFrame);
                appointmentsFrame.setVisible(true);
                return; // Return early without clearing content

            case "My Profile":
                ProfileFrame profileFrame = new ProfileFrame(currentUser);
                styleFrame(profileFrame);
                profileFrame.setVisible(true);
                return; // Return early without clearing content
        }

        // For other tabs that need to update the content panel
        contentPanel.removeAll();

        // For Dashboard, just reload immediately without loading screen
        if (command.equals("Dashboard")) {
            dashboardPanel = createDashboardPanel();
            contentPanel.add(dashboardPanel, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
            return;
        }

        // For the rest, use a loading indicator but keep it quick
        showLoadingEffect();

        SwingWorker<JPanel, Void> worker = new SwingWorker<JPanel, Void>() {
            @Override
            protected JPanel doInBackground() {
                JPanel newPanel = null;

                switch(command) {
                    case "My Donations":
                        newPanel = createMyDonationsPanel();
                        break;

                    case "My Requests":
                        newPanel = createMyRequestsPanel();
                        break;

                    default:
                        newPanel = createPlaceholderPanel("Feature Coming Soon",
                                "This feature is under development.");
                }

                return newPanel;
            }

            @Override
            protected void done() {
                try {
                    JPanel newPanel = get();
                    contentPanel.removeAll();
                    contentPanel.add(newPanel, BorderLayout.CENTER);
                    contentPanel.revalidate();
                    contentPanel.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                    contentPanel.removeAll();
                    contentPanel.add(createDashboardPanel(), BorderLayout.CENTER);
                    contentPanel.revalidate();
                    contentPanel.repaint();
                }
            }
        };

        worker.execute();
    }

    // Helper method to style frames consistently
    private void styleFrame(JFrame frame) {
        // Set main frame colors
        frame.getContentPane().setBackground(Color.WHITE);
        frame.getContentPane().setForeground(TEXT_DARK);

        // Apply to all components recursively
        applyStylesToComponents(frame.getContentPane());

        // Force refresh
        frame.revalidate();
        frame.repaint();
    }

    // Recursive method to style all components
    private void applyStylesToComponents(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JComponent) {
                JComponent jcomp = (JComponent) comp;

                // Set basic colors
                jcomp.setBackground(Color.WHITE);
                jcomp.setForeground(TEXT_DARK);

                // Special handling for specific components
                if (comp instanceof JButton) {
                    JButton button = (JButton) comp;
                    button.setContentAreaFilled(false);
                    button.setOpaque(true);
                    button.setBackground(PRIMARY_COLOR);
                    button.setForeground(Color.WHITE);
                    button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                } else if (comp instanceof JLabel) {
                    jcomp.setFont(REGULAR_FONT);
                } else if (comp instanceof JTable) {
                    JTable table = (JTable) comp;
                    table.setBackground(Color.WHITE);
                    table.setForeground(TEXT_DARK);
                    table.setGridColor(new Color(220, 220, 220));
                    table.setFont(REGULAR_FONT);
                } else if (comp instanceof JTextField || comp instanceof JTextArea || comp instanceof JComboBox) {
                    jcomp.setBackground(Color.WHITE);
                    jcomp.setForeground(TEXT_DARK);
                    jcomp.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                            BorderFactory.createEmptyBorder(5, 5, 5, 5)
                    ));
                }

                // Recursively apply to child components
                if (comp instanceof Container) {
                    applyStylesToComponents((Container) comp);
                }
            }
        }
    }

    private void showLoadingEffect() {
        JPanel loadingPanel = new JPanel(new BorderLayout());
        loadingPanel.setBackground(Color.WHITE);

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setOpaque(false);

        JLabel loadingLabel = new JLabel("Loading...");
        loadingLabel.setFont(REGULAR_FONT);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(200, 20));
        progressBar.setForeground(PRIMARY_COLOR);

        centerPanel.add(loadingLabel);
        centerPanel.add(progressBar);

        loadingPanel.add(centerPanel, BorderLayout.CENTER);
        contentPanel.add(loadingPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createPlaceholderPanel(String title, String message) {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(SUBTITLE_FONT);

        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(REGULAR_FONT);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(messageLabel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createMyDonationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel titleLabel = new JLabel("My Donation History");
        titleLabel.setFont(SUBTITLE_FONT);

        // Statistics panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setOpaque(false);

        JPanel totalDonations = createSimpleStatCard("Total Donations", "5", PRIMARY_COLOR);
        JPanel totalVolume = createSimpleStatCard("Total Volume", "2250 ml", new Color(40, 167, 69));
        JPanel lastDonation = createSimpleStatCard("Last Donation", "45 days ago", new Color(255, 193, 7));

        statsPanel.add(totalDonations);
        statsPanel.add(totalVolume);
        statsPanel.add(lastDonation);

        // Donation history table
        String[] columns = {"Date", "Location", "Blood Type", "Volume (ml)", "Certificate"};
        Object[][] data = {
                {"2023-03-05", "City Hospital", "A+", "450 ml", "View"},
                {"2022-11-18", "Red Cross Center", "A+", "450 ml", "View"},
                {"2022-07-22", "Community Drive", "A+", "450 ml", "View"},
                {"2022-03-15", "Medical College", "A+", "450 ml", "View"},
                {"2021-11-30", "City Hospital", "A+", "450 ml", "View"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only certificate column is editable
            }
        };

        JTable donationTable = new JTable(model);
        donationTable.setFont(REGULAR_FONT);
        donationTable.setRowHeight(30);
        donationTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        donationTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane tablePane = new JScrollPane(donationTable);
        tablePane.setBorder(BorderFactory.createEmptyBorder());

        // Schedule next donation
        JPanel schedulePanel = new JPanel(new BorderLayout(10, 0));
        schedulePanel.setOpaque(false);
        schedulePanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JLabel eligibilityLabel = new JLabel("You are eligible for your next donation in 45 days");
        eligibilityLabel.setFont(REGULAR_FONT);

        JButton scheduleButton = new JButton("Schedule Next Donation");
        scheduleButton.setFont(REGULAR_FONT);
        scheduleButton.setBackground(PRIMARY_COLOR);
        scheduleButton.setForeground(Color.WHITE);
        scheduleButton.setFocusPainted(false);
        scheduleButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        scheduleButton.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Scheduling feature coming soon!",
                "Information", JOptionPane.INFORMATION_MESSAGE));

        schedulePanel.add(eligibilityLabel, BorderLayout.WEST);
        schedulePanel.add(scheduleButton, BorderLayout.EAST);

        // Add components to main panel
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);
        centerPanel.add(statsPanel, BorderLayout.NORTH);
        centerPanel.add(tablePane, BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(schedulePanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMyRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel titleLabel = new JLabel("My Blood Requests");
        titleLabel.setFont(SUBTITLE_FONT);

        // Active requests section
        JPanel activeRequestsPanel = new JPanel(new BorderLayout(0, 10));
        activeRequestsPanel.setOpaque(false);

        JLabel activeLabel = new JLabel("Active Requests");
        activeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        // Active requests table
        String[] activeColumns = {"Request ID", "Blood Type", "Units", "Priority", "Created On", "Status", "Actions"};
        Object[][] activeData = {
                {"REQ-2023-052", "AB-", "2", "High", "2023-05-10", "Searching Donors", "Cancel"}
        };

        DefaultTableModel activeModel = new DefaultTableModel(activeData, activeColumns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only actions column is editable
            }
        };

        JTable activeTable = new JTable(activeModel);
        activeTable.setFont(REGULAR_FONT);
        activeTable.setRowHeight(30);
        activeTable.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());
        activeTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        activeTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane activePane = new JScrollPane(activeTable);
        activePane.setBorder(BorderFactory.createEmptyBorder());

        activeRequestsPanel.add(activeLabel, BorderLayout.NORTH);
        activeRequestsPanel.add(activePane, BorderLayout.CENTER);

        // Past requests section
        JPanel pastRequestsPanel = new JPanel(new BorderLayout(0, 10));
        pastRequestsPanel.setOpaque(false);

        JLabel pastLabel = new JLabel("Past Requests");
        pastLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        // Past requests table
        String[] pastColumns = {"Request ID", "Blood Type", "Units", "Priority", "Created On", "Status", "Details"};
        Object[][] pastData = {
                {"REQ-2023-041", "A+", "1", "Medium", "2023-04-05", "Fulfilled", "View"},
                {"REQ-2023-032", "O-", "3", "High", "2023-03-28", "Cancelled", "View"},
                {"REQ-2022-128", "A+", "2", "Medium", "2022-12-15", "Fulfilled", "View"}
        };

        DefaultTableModel pastModel = new DefaultTableModel(pastData, pastColumns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only details column is editable
            }
        };

        JTable pastTable = new JTable(pastModel);
        pastTable.setFont(REGULAR_FONT);
        pastTable.setRowHeight(30);
        pastTable.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());
        pastTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        pastTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane pastPane = new JScrollPane(pastTable);
        pastPane.setBorder(BorderFactory.createEmptyBorder());

        pastRequestsPanel.add(pastLabel, BorderLayout.NORTH);
        pastRequestsPanel.add(pastPane, BorderLayout.CENTER);

        // New request button
        JButton newRequestButton = new JButton("New Blood Request");
        newRequestButton.setFont(REGULAR_FONT);
        newRequestButton.setBackground(PRIMARY_COLOR);
        newRequestButton.setForeground(Color.WHITE);
        newRequestButton.setFocusPainted(false);
        newRequestButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        newRequestButton.addActionListener(e -> {
            new RequestBloodFrame().setVisible(true);
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(newRequestButton);

        // Add components to main panel
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.add(activeRequestsPanel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(pastRequestsPanel);

        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSimpleStatCard(String title, String value, Color color) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(SMALL_FONT);
        titleLabel.setForeground(new Color(100, 100, 100));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);

        return panel;
    }

    private void openDonateBloodFrame() {
        // Use the data we already have from login
        if (currentUser != null && currentFullName != null) {
            new DonateBloodFrame(currentUser, currentFullName, currentAge).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                    "User data not available",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showDashboard() {
        // Clear existing content
        contentPanel.removeAll();

        // Immediately show the dashboard panel (no loading delay)
        dashboardPanel = createDashboardPanel();
        contentPanel.add(dashboardPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();

        // Use SwingWorker for background data refresh if needed
        SwingWorker<Void, Void> refreshWorker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Actual data loading/refresh operations would go here
                // For example:
                // refreshDashboardData();
                return null;
            }

            @Override
            protected void done() {
                // Optional: Update UI after refresh completes
                try {
                    // Recreate dashboard with fresh data if needed
                    dashboardPanel = createDashboardPanel();
                    contentPanel.removeAll();
                    contentPanel.add(dashboardPanel, BorderLayout.CENTER);
                    contentPanel.revalidate();
                    contentPanel.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        refreshWorker.execute();
    }

    // Custom renderers and editors for table buttons and status cells

    // Status renderer for coloring status cells
    private class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            String status = value.toString();

            // Set text and background color based on status
            if (status.equals("Completed") || status.equals("Fulfilled") || status.equals("Sufficient")) {
                c.setForeground(new Color(40, 167, 69));
                setIcon(new ImageIcon()); // Add green check icon if available
            } else if (status.equals("Pending") || status.equals("Registered") || status.equals("Moderate")) {
                c.setForeground(new Color(255, 193, 7));
                setIcon(new ImageIcon()); // Add pending icon if available
            } else if (status.equals("Cancelled") || status.equals("URGENT NEED")) {
                c.setForeground(new Color(220, 53, 69));
                setIcon(new ImageIcon()); // Add warning icon if available
            } else {
                c.setForeground(table.getForeground());
                setIcon(null);
            }

            // Apply styles
            setHorizontalAlignment(SwingConstants.CENTER);

            return c;
        }
    }

    // Button renderer for showing buttons in tables
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setFocusPainted(false);
            setBorderPainted(true);
            setForeground(PRIMARY_COLOR); // Default color
            setBackground(Color.WHITE);  // Default background
            setFont(SMALL_FONT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());

            // Adjust colors based on selection
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(Color.WHITE);
                setForeground(PRIMARY_COLOR);
            }

            return this;
        }
    }

    // Button editor for handling clicks on table buttons
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setFocusPainted(false);
            button.setForeground(PRIMARY_COLOR);
            button.setBackground(Color.WHITE);
            button.setFont(SMALL_FONT);

            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            button.setForeground(isSelected ? table.getSelectionForeground() : PRIMARY_COLOR);
            button.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Handle button click based on the label
                switch (label) {
                    case "View":
                        JOptionPane.showMessageDialog(button,
                                "Details view coming soon!",
                                "Information",
                                JOptionPane.INFORMATION_MESSAGE);
                        break;
                    case "Cancel":
                        int option = JOptionPane.showConfirmDialog(button,
                                "Are you sure you want to cancel this request?",
                                "Confirm Cancellation",
                                JOptionPane.YES_NO_OPTION);
                        if (option == JOptionPane.YES_OPTION) {
                            JOptionPane.showMessageDialog(button,
                                    "Request cancelled successfully!",
                                    "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                        break;
                }
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Apply custom UI tweaks
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("ProgressBar.arc", 8);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.gridColor", new Color(230, 230, 230));
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            // Provide dummy/test values for all required parameters
            new HomeFrame("AdminUser", "Administrator", 30);
        });
    }
}