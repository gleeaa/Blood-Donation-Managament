import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class FindDonorFrame extends JFrame {

    private JComboBox<String> bloodGroupBox;
    private JTable donorTable;

    public FindDonorFrame() {
        setTitle("Find Donors");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));

        JLabel label = new JLabel("Select Blood Group:");
        label.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(label);

        bloodGroupBox = new JComboBox<>(new String[] {
                "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
        });
        bloodGroupBox.setFont(new Font("Arial", Font.PLAIN, 14));
        topPanel.add(bloodGroupBox);

        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Arial", Font.BOLD, 14));
        searchBtn.setBackground(new Color(139, 0, 0));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.addActionListener(this::searchDonors);
        topPanel.add(searchBtn);

        donorTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(donorTable);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void searchDonors(ActionEvent e) {
        String selectedBloodGroup = (String) bloodGroupBox.getSelectedItem();

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/blood_donation", "root", "parth");
            String query = "SELECT full_name AS 'Name',  age AS 'Age', donation_date AS 'Donation Date' FROM donors WHERE blood_group = ?";

            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, selectedBloodGroup);

            ResultSet rs = ps.executeQuery();

            // Get column count
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Prepare data for JTable
            DefaultTableModel tableModel = new DefaultTableModel();

            // Add column names to table model
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(metaData.getColumnLabel(i));
            }

            // Add rows to table model
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                tableModel.addRow(row);
            }

            donorTable.setModel(tableModel);
            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FindDonorFrame().setVisible(true);
        });
    }
}
