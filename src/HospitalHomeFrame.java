import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// Note: Assumes FindDonorFrame, RequestBloodFrame, and PortalSelectionFrame exist.

public class HospitalHomeFrame extends JFrame {

    private String currentUsername;
    private String currentHospitalName;
    private JPanel contentPanel;
    private Map<String, Color> bloodTypeColors;
    private JLabel clockLabel;
    private Timer clockTimer;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("EEE, dd MMM yyyy, HH:mm:ss");

    // Constants for styling (consistent with user HomeFrame)
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180); // Blue for Hospital Portal
    private static final Color SECONDARY_COLOR = new Color(240, 248, 255);
    private static final Color ACCENT_COLOR = new Color(220, 230, 240);
    private static final Color TEXT_DARK = new Color(50, 50, 50);
    private static final Color TEXT_LIGHT = Color.WHITE;
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    public HospitalHomeFrame(String username, String hospitalName) {
        this.currentUsername = username;
        this.currentHospitalName = hospitalName;

        initializeBloodTypeColors();
        setupFrame();
        setupComponents();
        setupClockTimer();
        
        setVisible(true);
    }

    // Adds a timer to update the clock label every second
    private void setupClockTimer() {
        clockTimer = new Timer(1000, (ActionEvent e) -> {
            if (clockLabel != null) {
                clockLabel.setText(timeFormat.format(new Date()));
            }
        });
        clockTimer.start();
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
        setTitle("Hospital Portal - " + currentHospitalName);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void setupComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(SECONDARY_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Header Panel
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Navigation Panel
        mainPanel.add(createNavigationPanel(), BorderLayout.WEST);

        // 3. Content Panel (Dashboard)
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        contentPanel.add(createDashboardPanel(), BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // 4. Status Bar
        mainPanel.add(createStatusPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("HOSPITAL PORTAL");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_LIGHT);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);

        JLabel userLabel = new JLabel("Logged in as: " + currentHospitalName);
        userLabel.setFont(REGULAR_FONT);
        userLabel.setForeground(TEXT_LIGHT);

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


        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(userPanel, BorderLayout.EAST);
        return panel;
    }
    
    private void handleLogout() {
        int option = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to logout?", 
                "Confirm Logout", 
                JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            dispose();
            // Go back to the portal selection screen
            new PortalSelectionFrame().setVisible(true); 
        }
    }


    private JPanel createNavigationPanel() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(ACCENT_COLOR);
        outerPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        outerPanel.setPreferredSize(new Dimension(220, 0));

        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBackground(new Color(200, 210, 220));
        profilePanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        JLabel avatarLabel = new JLabel("🏥");
        avatarLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel(currentHospitalName);
        nameLabel.setFont(SUBTITLE_FONT);
        nameLabel.setForeground(TEXT_DARK);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        profilePanel.add(avatarLabel);
        profilePanel.add(Box.createVerticalStrut(10));
        profilePanel.add(nameLabel);

        JPanel navButtonsPanel = new JPanel();
        navButtonsPanel.setLayout(new BoxLayout(navButtonsPanel, BoxLayout.Y_AXIS));
        navButtonsPanel.setBackground(ACCENT_COLOR);
        navButtonsPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        String[] buttons = {"Dashboard", "Find Donors", "Request Blood", "View Requests", "My Profile"};
        String[] icons = {"🏠", "🔍", "🩸", "📝", "👤"};

        for (int i = 0; i < buttons.length; i++) {
            JPanel buttonPanel = createNavButtonPanel(icons[i], buttons[i]);
            navButtonsPanel.add(buttonPanel);
            navButtonsPanel.add(Box.createVerticalStrut(8));
        }

        JScrollPane scrollPane = new JScrollPane(navButtonsPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        outerPanel.add(profilePanel, BorderLayout.NORTH);
        outerPanel.add(scrollPane, BorderLayout.CENTER);

        return outerPanel;
    }
    
    private JPanel createNavButtonPanel(String icon, String text) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(10, 15, 10, 15)
        ));
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel iconLabel = new JLabel(icon.equals("🏠") ? "H" : icon.equals("🔍") ? "F" : icon.equals("🩸") ? "R" : icon.equals("📝") ? "Q" : "P");
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

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(new Color(230, 240, 250));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(Color.WHITE);
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                handleNavButtonClick(text);
            }
        });

        return panel;
    }

    private JPanel createDashboardPanel() {
        // This is a simplified version of the user dashboard
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        
        JLabel title = new JLabel("Welcome, " + currentHospitalName + "!", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);

        // --- Dashboard Content (Mirroring User Dashboard Structure) ---
        
        // 1. Quick Stats Cards (Placeholder - you would port the real methods from HomeFrame)
        JPanel statsPanel = createHospitalStatsPanel();
        panel.add(statsPanel);
        panel.add(Box.createVerticalStrut(20));

        // 2. Blood Availability and Events (Placeholder - you would port the real methods)
        JPanel middlePanel = new JPanel(new GridLayout(1, 2, 15, 0));
        middlePanel.setOpaque(false);
        middlePanel.add(createBloodAvailabilityPanel());
        middlePanel.add(createUpcomingEventsPanel());
        panel.add(middlePanel);
        
        // --- End Dashboard Content ---

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);

        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setOpaque(false);
        containerPanel.add(scrollPane, BorderLayout.CENTER);

        return containerPanel;
    }
    
    // Placeholder methods mirroring HomeFrame for structural consistency
    private JPanel createHospitalStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);

        String[] stats = {"Urgent Requests", "Pending Requests", "Donors Found Today", "Stock Level"};
        String[] values = {"5", "12", "8", "LOW"};
        Color[] colors = {new Color(220, 53, 69), new Color(255, 193, 7), new Color(40, 167, 69), new Color(108, 117, 125)};

        for (int i = 0; i < stats.length; i++) {
            JPanel card = createSimpleStatCard(stats[i], values[i], colors[i]);
            statsPanel.add(card);
        }
        return statsPanel;
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

    private JPanel createBloodAvailabilityPanel() {
        // Placeholder for Blood Type Availability - Hospital view
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Central Blood Stock Levels"));
        panel.add(new JLabel("Shows stock levels (O+, A-, etc.) in the central database.", SwingConstants.CENTER), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createUpcomingEventsPanel() {
        // Placeholder for upcoming donation camps or hospital events
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Upcoming Donation Drives"));
        panel.add(new JLabel("Shows list of local donation drives.", SwingConstants.CENTER), BorderLayout.CENTER);
        return panel;
    }


    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ACCENT_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel statusLabel = new JLabel("Hospital System Status: Ready");
        statusLabel.setFont(REGULAR_FONT);

        clockLabel = new JLabel(timeFormat.format(new Date()));
        clockLabel.setFont(REGULAR_FONT);

        panel.add(statusLabel, BorderLayout.WEST);
        panel.add(clockLabel, BorderLayout.EAST);

        return panel;
    }
    
    private void handleNavButtonClick(String command) {
        // Launches separate frames for specific tasks
        switch (command) {
            case "Find Donors":
                // Assumes FindDonorFrame is useful for hospitals
                new FindDonorFrame().setVisible(true);
                break;
            case "Request Blood":
                // Assumes RequestBloodFrame is used by hospitals
                new RequestBloodFrame().setVisible(true);
                break;
            default:
                // For Dashboard, View Requests, My Profile
                contentPanel.removeAll();
                if (command.equals("Dashboard")) {
                     contentPanel.add(createDashboardPanel(), BorderLayout.CENTER);
                } else {
                     contentPanel.add(new JLabel("Viewing " + command, SwingConstants.CENTER), BorderLayout.CENTER);
                }
                contentPanel.revalidate();
                contentPanel.repaint();
                break;
        }
    }
}

