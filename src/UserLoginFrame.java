import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserLoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public UserLoginFrame() {
        setTitle("Blood Donation - User Login");
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

        Font labelFont = new Font("Arial", Font.BOLD, 18);
        Font fieldFont = new Font("Arial", Font.PLAIN, 16);
        Dimension fieldSize = new Dimension(300, 35);

        JLabel titleLabel = new JLabel("USER / DONOR LOGIN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(139, 0, 0));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        usernameField = createStyledTextField(fieldSize, fieldFont);
        passwordField = createStyledPasswordField(fieldSize, fieldFont);

        JButton loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Arial", Font.BOLD, 18));
        loginButton.setPreferredSize(new Dimension(200, 45));
        loginButton.setBackground(new Color(139, 0, 0));
        loginButton.setForeground(Color.WHITE);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(this::handleUserLogin);

        JButton registerButton = new JButton("NEW USER? REGISTER HERE");
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setForeground(new Color(70, 130, 180));
        registerButton.setContentAreaFilled(false);
        registerButton.setBorderPainted(false);
        registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Registration feature not implemented yet.", "Info", JOptionPane.INFORMATION_MESSAGE));


        // --- ADDED: NEW HOSPITAL LOGIN BUTTON ---
        JButton hospitalLoginButton = new JButton("HOSPITAL LOGIN");
        hospitalLoginButton.setFont(new Font("Arial", Font.BOLD, 14));
        hospitalLoginButton.setForeground(new Color(70, 70, 70));
        hospitalLoginButton.setContentAreaFilled(false);
        hospitalLoginButton.setBorderPainted(false);
        hospitalLoginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Add hover effects
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(178, 34, 34));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(139, 0, 0));
            }
        });
        
        registerButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                registerButton.setForeground(new Color(30, 144, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                registerButton.setForeground(new Color(70, 130, 180));
            }
        });
        
        hospitalLoginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                hospitalLoginButton.setForeground(new Color(0, 0, 0)); // Black on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                hospitalLoginButton.setForeground(new Color(70, 70, 70));
            }
        });

        // Layout components
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; formPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0; 
        JLabel userLabel = new JLabel("Username:"); userLabel.setFont(labelFont); formPanel.add(userLabel, gbc);
        gbc.gridx = 1; formPanel.add(usernameField, gbc);
        gbc.gridy = 2; gbc.gridx = 0;
        JLabel passLabel = new JLabel("Password:"); passLabel.setFont(labelFont); formPanel.add(passLabel, gbc);
        gbc.gridx = 1; formPanel.add(passwordField, gbc);
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2; formPanel.add(loginButton, gbc);
        gbc.gridy = 4; formPanel.add(registerButton, gbc);

        // --- ADDED: Layout for Hospital Button ---
        gbc.gridy = 5;
        formPanel.add(hospitalLoginButton, gbc); 
        // ------------------------------------------

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);
        
        // --- ADDED: HOSPITAL LOGIN ACTION ---
        hospitalLoginButton.addActionListener((ActionEvent e) -> {
            dispose();
            // Launches the HospitalLoginFrame
            new HospitalLoginFrame().setVisible(true); 
        });
        // ------------------------------------
    }

    private void handleUserLogin(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = ConnectionProvider.getCon()) {
            String sql = "SELECT full_name, age FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String fullName = rs.getString("full_name");
                int age = rs.getInt("age");
                JOptionPane.showMessageDialog(this, "User Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // --- FIX: Redirect to HomeFrame ---
                dispose();
                new HomeFrame(username, fullName, age).setVisible(true);
                // ----------------------------------
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
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
}
