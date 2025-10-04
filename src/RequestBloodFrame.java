import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;

public class RequestBloodFrame extends JFrame {

    private JTextField fullNameField, emailField, mobileField, unitsField, hospitalField, addressField;
    private JComboBox<String> bloodGroupBox;
    private JButton submitButton, clearButton;
    private JLabel headerLabel;
    private JPanel formPanel;

    public RequestBloodFrame() {
        setTitle("Request Blood");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 248, 255)); // AliceBlue background

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(139, 0, 0)); // Dark red
        headerPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        headerLabel = new JLabel("BLOOD REQUEST FORM", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Main Form Panel
        formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(139, 0, 0), 2, true),
                new EmptyBorder(30, 50, 30, 50)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form Fields
        String[] labels = {
                "Full Name:", "Email:", "Mobile:", "Blood Group:",
                "Units Required:", "Hospital Name:", "Address:"
        };

        fullNameField = createStyledTextField();
        emailField = createStyledTextField();
        mobileField = createStyledTextField();
        bloodGroupBox = createStyledComboBox(new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
        unitsField = createStyledTextField();
        hospitalField = createStyledTextField();
        addressField = createStyledTextField();
        addressField.setPreferredSize(new Dimension(250, 60)); // Taller for address

        Component[] fields = {
                fullNameField, emailField, mobileField, bloodGroupBox,
                unitsField, hospitalField, addressField
        };

        // Add labels and fields to form
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.gridwidth = 1;
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Arial", Font.BOLD, 14));
            formPanel.add(label, gbc);

            gbc.gridx = 1;
            if (fields[i] instanceof JComboBox) {
                formPanel.add(fields[i], gbc);
            } else {
                JScrollPane scrollPane = new JScrollPane(fields[i]);
                scrollPane.setBorder(null);
                formPanel.add(scrollPane, gbc);
            }
        }

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 10, 0));

        submitButton = createStyledButton("Submit Request", new Color(139, 0, 0));
        submitButton.addActionListener(this::submitForm);
        submitButton.addMouseListener(new ButtonHoverEffect(submitButton, new Color(178, 34, 34)));

        clearButton = createStyledButton("Clear Form", new Color(70, 130, 180));
        clearButton.addActionListener(e -> clearForm());
        clearButton.addMouseListener(new ButtonHoverEffect(clearButton, new Color(100, 149, 237)));

        buttonPanel.add(clearButton);
        buttonPanel.add(submitButton);

        // Add components to frame
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(240, 248, 255));
        centerPanel.add(formPanel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // Add real-time validation
        addInputValidation();
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(5, 5, 5, 5)
        ));
        return comboBox;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.WHITE, 2),
                new EmptyBorder(10, 25, 10, 25)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void addInputValidation() {
        // Mobile number validation
        mobileField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                    e.consume();
                    JOptionPane.showMessageDialog(RequestBloodFrame.this,
                            "Only numbers allowed in mobile field",
                            "Invalid Input", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Units validation
        unitsField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                    e.consume();
                }
            }
        });

        // Email validation on focus loss
        emailField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                String email = emailField.getText();
                if (!email.isEmpty() && !email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                    JOptionPane.showMessageDialog(RequestBloodFrame.this,
                            "Please enter a valid email address",
                            "Invalid Email", JOptionPane.WARNING_MESSAGE);
                    emailField.requestFocus();
                }
            }
        });
    }

    private void clearForm() {
        fullNameField.setText("");
        emailField.setText("");
        mobileField.setText("");
        bloodGroupBox.setSelectedIndex(0);
        unitsField.setText("");
        hospitalField.setText("");
        addressField.setText("");
    }

    private void submitForm(ActionEvent e) {
        // Validate all fields
        if (fullNameField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() ||
                mobileField.getText().trim().isEmpty() ||
                unitsField.getText().trim().isEmpty() ||
                hospitalField.getText().trim().isEmpty() ||
                addressField.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Please fill in all required fields",
                    "Incomplete Form", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Parse units
        int units;
        try {
            units = Integer.parseInt(unitsField.getText());
            if (units <= 0 || units > 10) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid number of units (1-10)",
                        "Invalid Units", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid number for units",
                    "Invalid Units", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirm submission
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to submit this blood request?",
                "Confirm Submission",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Process submission
        try (Connection con = DriverManager.getConnection(
                "jdbc:mariadb://127.0.0.1:3307/blood_donation_db", "root", "Alpha@123")) {

            String query = "INSERT INTO blood_requests (full_name, email, mobile, blood_group, " +
                    "units_required, hospital_name, address, request_date, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, fullNameField.getText().trim());
                ps.setString(2, emailField.getText().trim());
                ps.setString(3, mobileField.getText().trim());
                ps.setString(4, (String) bloodGroupBox.getSelectedItem());
                ps.setInt(5, units);
                ps.setString(6, hospitalField.getText().trim());
                ps.setString(7, addressField.getText().trim());
                ps.setDate(8, Date.valueOf(LocalDate.now()));
                ps.setString(9, "Pending"); // Default status

                int result = ps.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Request submitted successfully!\nWe'll contact you soon.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Show confirmation dialog with reference number
                    showConfirmationDialog(con);
                    this.dispose();
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error submitting request: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void showConfirmationDialog(Connection con) throws SQLException {
        String query = "SELECT request_id FROM blood_requests ORDER BY request_id DESC LIMIT 1";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                int requestId = rs.getInt("request_id");
                JOptionPane.showMessageDialog(this,
                        "<html><div style='text-align: center;'>" +
                                "<h2>Request Submitted Successfully!</h2>" +
                                "<p>Your reference number is: <b>BR" + requestId + "</b></p>" +
                                "<p>We'll contact you shortly to confirm your request.</p>" +
                                "</div></html>",
                        "Request Confirmation",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    // Button hover effect class
    private class ButtonHoverEffect extends MouseAdapter {
        private JButton button;
        private Color hoverColor;

        public ButtonHoverEffect(JButton button, Color hoverColor) {
            this.button = button;
            this.hoverColor = hoverColor;
        }

        public void mouseEntered(MouseEvent e) {
            button.setBackground(hoverColor);
        }

        public void mouseExited(MouseEvent e) {
            if (button == submitButton) {
                button.setBackground(new Color(139, 0, 0));
            } else {
                button.setBackground(new Color(70, 130, 180));
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            RequestBloodFrame frame = new RequestBloodFrame();
            frame.setVisible(true);
        });
    }
}