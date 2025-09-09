import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProfileFrame extends JFrame {

    private String currentUser;

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

        // Profile Content Panel
        JPanel profilePanel = new JPanel(new GridBagLayout());
        profilePanel.setBackground(Color.WHITE);
        profilePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(139, 0, 0), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, currentUser);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Personal Information Section
                addSectionLabel("Personal Information", profilePanel, gbc);

                addProfileField("Full Name:", rs.getString("full_name"), profilePanel, gbc);
                addProfileField("Username:", rs.getString("username"), profilePanel, gbc);
                addProfileField("Email:", rs.getString("email"), profilePanel, gbc);
                addProfileField("Mobile:", rs.getString("mobile"), profilePanel, gbc);
                addProfileField("Age:", String.valueOf(rs.getInt("age")), profilePanel, gbc);
                addProfileField("Gender:", rs.getString("gender"), profilePanel, gbc);

                // Medical Information Section
                gbc.gridy++;
                addSectionLabel("Medical Information", profilePanel, gbc);

                addProfileField("Blood Group:", rs.getString("blood_group"), profilePanel, gbc);
                addProfileField("Height (cm):", String.valueOf(rs.getDouble("height_cm")), profilePanel, gbc);
                addProfileField("Weight (kg):", String.valueOf(rs.getDouble("weight_kg")), profilePanel, gbc);

                // Donation History Section
                gbc.gridy++;
                addSectionLabel("Donation History", profilePanel, gbc);

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
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading profile: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Edit Profile Button
        JButton editButton = new JButton("Edit Profile");
        editButton.setBackground(new Color(70, 130, 180));
        editButton.setForeground(Color.WHITE);
        editButton.addActionListener(e -> {
            // Will implement edit functionality later
            JOptionPane.showMessageDialog(this, "Edit profile feature coming soon!");
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(editButton);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(profilePanel), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addSectionLabel(String text, JPanel panel, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(new Color(139, 0, 0));
        panel.add(label, gbc);
        gbc.gridy++;
        gbc.gridwidth = 1;
    }

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
        panel.add(field, gbc);

        gbc.gridy++;
    }
}