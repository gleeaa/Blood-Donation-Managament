import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PortalSelectionFrame extends JFrame {

    public PortalSelectionFrame() {
        setTitle("Blood Donation - Portal Selection");
        setSize(450, 350); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JLabel titleLabel = new JLabel("SELECT YOUR PORTAL");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(new Color(139, 0, 0));
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // --- HOSPITAL BUTTON ---
        JButton hospitalButton = createStyledButton("HOSPITAL LOGIN", new Color(70, 130, 180));
        hospitalButton.addActionListener(e -> {
            dispose();
            new HospitalLoginFrame().setVisible(true);
        });
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        mainPanel.add(hospitalButton, gbc);

        // --- USER BUTTON ---
        JButton userButton = createStyledButton("USER / DONOR LOGIN", new Color(139, 0, 0));
        userButton.addActionListener(e -> {
            dispose();
            new UserLoginFrame().setVisible(true);
        });
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        mainPanel.add(userButton, gbc);

        add(mainPanel);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(300, 50));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PortalSelectionFrame().setVisible(true));
    }
}
