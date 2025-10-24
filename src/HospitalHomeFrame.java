import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// IMPORTANT: Ensure HospitalHomeFrame is available in your project's classpath
// If HospitalHomeFrame is in a different package, you MUST uncomment and correct the import below:
// import your.package.HospitalHomeFrame; 

public class HospitalLoginFrame extends JFrame {
    
    private JTextField usernameField;
    private JPasswordField passwordField;

    public HospitalLoginFrame() {
        setTitle("Blood Donation - Hospital Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(240, 248, 255));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(new Color(220, 230, 240));
                for (int i = 0; i < getWidth(); i += 40) {
                    for (int j = 0; j < getHeight(); j += 40) {
                        g.fillOval(i, j, 3, 3);
                    }
                }
            }
        };
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JPanel formPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(new Color(70, 130, 180)); // Use a different color for distinction
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 30, 30);
                g2.dispose();
            }
        };
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(40, 60, 40, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Arial", Font.BOLD, 18);
        Font fieldFont = new Font("Arial", Font.PLAIN, 16);
        Dimension fieldSize = new Dimension(300, 35);

        JLabel titleLabel = new JLabel("HOSPITAL PORTAL LOGIN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(70, 130, 180));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        usernameField = createStyledTextField(fieldSize, fieldFont);
        passwordField = createStyledPasswordField(fieldSize, fieldFont);

        JButton loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Arial", Font.BOLD, 18));
        loginButton.setPreferredSize(new Dimension(200, 45));
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(this::handleHospitalLogin);

        // Layout
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; formPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0; 
        JLabel userLabel = new JLabel("Username:"); userLabel.setFont(labelFont); formPanel.add(userLabel, gbc);
        gbc.gridx = 1; formPanel.add(usernameField, gbc);
        gbc.gridy = 2; gbc.gridx = 0;
        JLabel passLabel = new JLabel("Password:"); passLabel.setFont(labelFont); formPanel.add(passLabel, gbc);
        gbc.gridx = 1; formPanel.add(passwordField, gbc);
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2; formPanel.add(loginButton, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void handleHospitalLogin(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = ConnectionProvider.getCon()) {
            // Check against the new hospitals table
            String sql = "SELECT hospital_name FROM hospitals WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hospitalName = rs.getString("hospital_name");
                JOptionPane.showMessageDialog(this, "Hospital Login successful! Welcome, " + hospitalName);
                dispose();
                
                // --- FIX: Ensure the constructor call is correct ---
                new HospitalHomeFrame(username, hospitalName).setVisible(true); 
                // ---------------------------------------------------
            } else {
                JOptionPane.showMessageDialog(this, "Invalid hospital username or password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            // This error often points to issues in ConnectionProvider or database configuration
            JOptionPane.showMessageDialog(this, "Database Error during login: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JTextField createStyledTextField(Dimension size, Font font) {
        JTextField field = new JTextField();
        field.setPreferredSize(size);
        field.setFont(font);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
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
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setBackground(Color.WHITE);
        return field;
    }
}
