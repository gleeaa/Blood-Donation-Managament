import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfileFrame extends JFrame {

    private String currentUser;
    private JPanel profilePanel; // To hold the scroll pane content

    public ProfileFrame(String username) {
        this.currentUser = username;
        setTitle("My Profile");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(139, 0, 0));
        JLabel headerLabel = new JLabel("MY PROFILE");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        // Profile Content Panel (Initialized, populated in loadProfile)
        profilePanel = new JPanel(new GridBagLayout());
        profilePanel.setBackground(Color.WHITE);
        profilePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(139, 0, 0), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Load data and build the UI
        loadProfileData();
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(profilePanel), BorderLayout.CENTER);

        add(mainPanel);
    }

    private void loadProfileData() {
        profilePanel.removeAll(); // Clear previous content
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        try (Connection conn = ConnectionProvider.getCon()) {
            String sql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, currentUser);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // --- Personal Information Section (NO HEADING) ---
                createPersonalInfoSection(rs, profilePanel, gbc); 
                
                // --- Medical Information Section ---
                gbc.gridy++;
                addSectionLabel("Medical Information", profilePanel, gbc);

                addProfileField("Blood Group:", rs.getString("blood_group"), profilePanel, gbc);
                addProfileField("Height (cm):", String.valueOf(rs.getDouble("height_cm")), profilePanel, gbc);
                addProfileField("Weight (kg):", String.valueOf(rs.getDouble("weight_kg")), profilePanel, gbc);
                
                // --- Donation History Section (Read-only view) ---
                gbc.gridy++;
                addSectionLabel("Donation History", profilePanel, gbc);
                
                // Get donation history data
                String donationSql = "SELECT donation_date, blood_group FROM donors WHERE username = ? ORDER BY donation_date DESC";
                PreparedStatement donationStmt = conn.prepareStatement(donationSql);
                donationStmt.setString(1, currentUser);
                ResultSet donationRs = donationStmt.executeQuery();

                int donationCount = 0;
                while (donationRs.next()) {
                    donationCount++;
                    String donationText = donationCount + ". " + donationRs.getDate("donation_date") +
                            " (" + donationRs.getString("blood_group") + ")";
                    addProfileField("", donationText, profilePanel, gbc);
                }

                if (donationCount == 0) {
                    addProfileField("", "No donation history found", profilePanel, gbc);
                }
                
            } else {
                 JOptionPane.showMessageDialog(this, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading profile data: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        profilePanel.revalidate();
        profilePanel.repaint();
    }

    /**
     * Creates and adds the Personal Information section fields to the panel, without a heading.
     */
    private void createPersonalInfoSection(ResultSet rs, JPanel panel, GridBagConstraints gbc) throws SQLException {
        // REMOVED: addSectionLabel("Personal Information", panel, gbc); 
        
        // Ensure gbc starts at the correct position (0, 0 if loadProfileData calls this first)
        // If this is the first section, no extra top padding is needed.
        
        addProfileField("Full Name:", rs.getString("full_name"), panel, gbc);
        addProfileField("Username:", rs.getString("username"), panel, gbc); 
        addProfileField("Email:", rs.getString("email"), panel, gbc);
        addProfileField("Mobile:", rs.getString("mobile"), panel, gbc);
        addProfileField("Age:", String.valueOf(rs.getInt("age")), panel, gbc);
        addProfileField("Gender:", rs.getString("gender"), panel, gbc);
    }
    
    // --- Helper Methods ---

    /**
     * Adds a section heading with extra vertical spacing to prevent collision.
     * NOTE: This is only used for "Medical Information" and "Donation History" now.
     */
    private void addSectionLabel(String text, JPanel panel, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        
        // FIX: Add extra top padding (20) to separate from the previous section
        gbc.insets = new Insets(20, 10, 5, 10); 
        
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(new Color(139, 0, 0));
        panel.add(label, gbc);
        
        gbc.gridy++;
        gbc.gridwidth = 1;
        
        // RESTORE default padding for the next profile field
        gbc.insets = new Insets(10, 10, 10, 10);
    }

    /**
     * Adds a label-value pair as read-only JTextField to the panel.
     */
    private void addProfileField(String labelText, String value, JPanel panel, GridBagConstraints gbc) {
        gbc.gridx = 0;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(label, gbc);

        gbc.gridx = 1;
        JTextField field = new JTextField(value);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createEmptyBorder()); 
        field.setEditable(false);
        field.setBackground(panel.getBackground());
        field.setPreferredSize(new Dimension(250, 25)); 
        panel.add(field, gbc);

        gbc.gridy++;
    }
}