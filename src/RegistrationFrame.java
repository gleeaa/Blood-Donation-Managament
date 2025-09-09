import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class RegistrationFrame extends JFrame {

    public RegistrationFrame() {
        setTitle("Blood Donation - Registration");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main panel with background
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Solid color background (light blue)
                g.setColor(new Color(240, 248, 255));
                g.fillRect(0, 0, getWidth(), getHeight());

                // Optional: If you want to add a subtle pattern instead of image
                g.setColor(new Color(220, 230, 240));
                for (int i = 0; i < getWidth(); i += 40) {
                    for (int j = 0; j < getHeight(); j += 40) {
                        g.fillOval(i, j, 3, 3);
                    }
                }
            }
        };
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Form panel with rounded border
        JPanel formPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(new Color(139, 0, 0));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 30, 30);
                g2.dispose();
            }
        };
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(40, 60, 40, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create larger form components
        Font labelFont = new Font("Arial", Font.BOLD, 18);
        Font fieldFont = new Font("Arial", Font.PLAIN, 16);
        Dimension fieldSize = new Dimension(300, 35);

        JTextField fullNameField = createStyledTextField(fieldSize, fieldFont);
        JTextField emailField = createStyledTextField(fieldSize, fieldFont);
        JTextField mobileField = createStyledTextField(fieldSize, fieldFont);
        JTextField ageField = createStyledTextField(new Dimension(80, 35), fieldFont);
        JTextField heightField = createStyledTextField(new Dimension(80, 35), fieldFont);
        JTextField weightField = createStyledTextField(new Dimension(80, 35), fieldFont);
        JTextField bloodGroupField = createStyledTextField(new Dimension(80, 35), fieldFont);
        JTextField genderField = createStyledTextField(new Dimension(120, 35), fieldFont);
        JTextField usernameField = createStyledTextField(fieldSize, fieldFont);
        JPasswordField passwordField = createStyledPasswordField(fieldSize, fieldFont);

        String[] labels = {
                "Full Name", "Email", "Mobile", "Age",
                "Height (cm)", "Weight (kg)", "Blood Group",
                "Gender", "Username", "Password"
        };

        Component[] fields = {
                fullNameField, emailField, mobileField, ageField,
                heightField, weightField, bloodGroupField,
                genderField, usernameField, passwordField
        };

        // Add form title
        JLabel titleLabel = new JLabel("BLOOD DONOR REGISTRATION");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(139, 0, 0));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(titleLabel, gbc);

        // Add form fields
        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i] + ":");
            label.setFont(labelFont);
            label.setForeground(new Color(70, 70, 70));

            gbc.gridx = 0;
            gbc.gridy = i + 1;
            gbc.gridwidth = 1;
            formPanel.add(label, gbc);

            gbc.gridx = 1;
            formPanel.add((Component) fields[i], gbc);
        }

        // Create register button with styling
        JButton registerButton = new JButton("REGISTER");
        registerButton.setFont(new Font("Arial", Font.BOLD, 18));
        registerButton.setPreferredSize(new Dimension(200, 45));
        registerButton.setBackground(new Color(139, 0, 0));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(139, 0, 0), 2),
                BorderFactory.createEmptyBorder(5, 25, 5, 25)
        ));
        registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Add hover effect
        registerButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                registerButton.setBackground(new Color(178, 34, 34));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                registerButton.setBackground(new Color(139, 0, 0));
            }
        });

        gbc.gridx = 0;
        gbc.gridy = labels.length + 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(registerButton, gbc);

        // Button Action - Insert into database
        registerButton.addActionListener((ActionEvent e) -> {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "INSERT INTO users (full_name, email, mobile, age, height_cm, weight_kg, blood_group, gender, username, password) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, fullNameField.getText());
                stmt.setString(2, emailField.getText());
                stmt.setString(3, mobileField.getText());
                stmt.setInt(4, Integer.parseInt(ageField.getText()));
                stmt.setDouble(5, Double.parseDouble(heightField.getText()));
                stmt.setDouble(6, Double.parseDouble(weightField.getText()));
                stmt.setString(7, bloodGroupField.getText());
                stmt.setString(8, genderField.getText());
                stmt.setString(9, usernameField.getText());
                stmt.setString(10, new String(passwordField.getPassword()));

                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Registration successful!");
                clearFields(fields);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JTextField createStyledTextField(Dimension size, Font font) {
        JTextField field = new JTextField();
        field.setPreferredSize(size);
        field.setFont(font);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(139, 0, 0), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setBackground(Color.WHITE);
        return field;
    }

    private JPasswordField createStyledPasswordField(Dimension size, Font font) {
        JPasswordField field = new JPasswordField();
        field.setPreferredSize(size);
        field.setFont(font);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(139, 0, 0), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setBackground(Color.WHITE);
        return field;
    }

    private void clearFields(Component[] fields) {
        for (Component c : fields) {
            if (c instanceof JTextField) ((JTextField) c).setText("");
            if (c instanceof JPasswordField) ((JPasswordField) c).setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RegistrationFrame().setVisible(true);
        });
    }
}