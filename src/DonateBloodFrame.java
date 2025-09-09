import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class DonateBloodFrame extends JFrame {

    private String username;
    private int age;
    private String fullName ;

    public DonateBloodFrame(String username, String fullName, int age) {
        this.username = username;
        this.fullName = fullName;
        this.age = age;
        setTitle("Blood Donation - Donate Blood");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(139, 0, 0));
        JLabel headerLabel = new JLabel("BLOOD DONATION FORM");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(139, 0, 0), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Form Fields
        JLabel bloodGroupLabel = new JLabel("Blood Group:");
        JComboBox<String> bloodGroupCombo = new JComboBox<>(new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});

        JLabel weightLabel = new JLabel("Weight (kg):");
        JTextField weightField = new JTextField(10);

        JLabel heightLabel = new JLabel("Height (cm):");
        JTextField heightField = new JTextField(10);

        JLabel hemoglobinLabel = new JLabel("Hemoglobin Level (g/dL):");
        JTextField hemoglobinField = new JTextField(10);

        JLabel bpLabel = new JLabel("Blood Pressure:");
        JTextField bpField = new JTextField(10);

        JLabel pulseLabel = new JLabel("Pulse Rate:");
        JTextField pulseField = new JTextField(10);

        JLabel bmiLabel = new JLabel("BMI:");
        JLabel bmiValueLabel = new JLabel("--");

        JLabel eligibilityLabel = new JLabel("Eligibility Status:");
        JLabel eligibilityValueLabel = new JLabel("");
        eligibilityValueLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Add components to form
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(bloodGroupLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(bloodGroupCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(weightLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(weightField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(heightLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(heightField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(hemoglobinLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(hemoglobinField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(bpLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(bpField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(pulseLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(pulseField, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(bmiLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(bmiValueLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(eligibilityLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(eligibilityValueLabel, gbc);

        // Calculate Button
        JButton calculateBtn = new JButton("Calculate Eligibility");
        calculateBtn.setBackground(new Color(70, 130, 180));
        calculateBtn.setForeground(Color.WHITE);
        calculateBtn.addActionListener(e -> {
            try {
                double weight = Double.parseDouble(weightField.getText());
                double height = Double.parseDouble(heightField.getText());
                double hemoglobin = Double.parseDouble(hemoglobinField.getText());

                // Calculate BMI
                double bmi = calculateBMI(weight, height);
                bmiValueLabel.setText(String.format("%.2f", bmi));

                // Check eligibility
                boolean isEligible = checkEligibility(bmi, hemoglobin);
                if (isEligible) {
                    eligibilityValueLabel.setText("ELIGIBLE - You can donate blood!");
                    eligibilityValueLabel.setForeground(new Color(0, 128, 0));
                } else {
                    eligibilityValueLabel.setText("NOT ELIGIBLE - Please check requirements");
                    eligibilityValueLabel.setForeground(Color.RED);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Submit Button
        JButton submitBtn = new JButton("Submit Donation");
        submitBtn.setBackground(new Color(139, 0, 0));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.addActionListener(e -> submitDonation(
                (String)bloodGroupCombo.getSelectedItem(),
                weightField.getText(),
                heightField.getText(),
                hemoglobinField.getText(),
                bpField.getText(),
                pulseField.getText(),
                bmiValueLabel.getText(),
                eligibilityValueLabel.getText().startsWith("ELIGIBLE")
        ));

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(calculateBtn);
        buttonPanel.add(submitBtn);

        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private double calculateBMI(double weightKg, double heightCm) {
        double heightM = heightCm / 100;
        return weightKg / (heightM * heightM);
    }

    private boolean checkEligibility(double bmi, double hemoglobin) {
        // Basic eligibility criteria
        boolean bmiOk = bmi >= 18.5 && bmi <= 30;
        boolean hemoglobinOk = hemoglobin >= 12.5; // 12.5g/dL for women, 13g/dL for men (simplified)
        return bmiOk && hemoglobinOk;
    }

    private void submitDonation(String bloodGroup, String weight, String height,
                                String hemoglobin, String bp, String pulse,
                                String bmi, boolean isEligible) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO donors (username, full_name, age, blood_group, " +
                    "weight_kg, height_cm, hemoglobin_level, blood_pressure, " +
                    "pulse_rate, bmi, is_eligible) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, fullName);
            stmt.setInt(3, age);
            stmt.setString(4, bloodGroup);
            stmt.setDouble(5, Double.parseDouble(weight));
            stmt.setDouble(6, Double.parseDouble(height));
            stmt.setDouble(7, Double.parseDouble(hemoglobin));
            stmt.setString(8, bp);
            stmt.setInt(9, Integer.parseInt(pulse));
            stmt.setDouble(10, Double.parseDouble(bmi));
            stmt.setBoolean(11, isEligible);

            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Donation information saved successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            dispose(); // Close the form after submission

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error saving donation: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}