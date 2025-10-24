import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.Vector;

public class FindDonorFrame extends JFrame {

    private JComboBox<String> bloodGroupBox;
    private JTable donorTable;

    // Constants for styling (match core project colors)
    private static final Color PRIMARY_COLOR = new Color(139, 0, 0); // Red for Donor Actions

    public FindDonorFrame() {
        setTitle("Find Donors");
        setSize(1000, 600); // Increased size for better table viewing
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(240, 248, 255)); // Light background

        // --- Top Search Panel ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 0, 20));
        topPanel.setBackground(new Color(240, 248, 255));

        JLabel label = new JLabel("Select Blood Group to Search:");
        label.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(label);

        bloodGroupBox = new JComboBox<>(new String[] {
                "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
        });
        bloodGroupBox.setFont(new Font("Arial", Font.PLAIN, 16));
        topPanel.add(bloodGroupBox);

        JButton searchBtn = new JButton("Search Donors");
        searchBtn.setFont(new Font("Arial", Font.BOLD, 16));
        searchBtn.setBackground(PRIMARY_COLOR);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        searchBtn.addActionListener(this::searchDonors);
        topPanel.add(searchBtn);

        // --- Table Panel ---
        donorTable = new JTable();
        donorTable.setFont(new Font("Arial", Font.PLAIN, 14));
        donorTable.setRowHeight(25);
        donorTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        JScrollPane scrollPane = new JScrollPane(donorTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void searchDonors(ActionEvent e) {
        String selectedBloodGroup = (String) bloodGroupBox.getSelectedItem();

        // Use ConnectionProvider.getCon() to retrieve the connection
        try (Connection con = ConnectionProvider.getCon()) {
            
            // Querying the 'users' table (where registration data is stored) 
            // to find all users matching the requested blood group.
            String query = "SELECT full_name AS 'Name', age AS 'Age', mobile AS 'Mobile', blood_group AS 'Blood Group' FROM users WHERE blood_group = ? AND age >= 18";

            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, selectedBloodGroup);

            ResultSet rs = ps.executeQuery();

            // Setup table model using ResultSet metadata
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            DefaultTableModel tableModel = new DefaultTableModel();

            // Add column names
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(metaData.getColumnLabel(i));
            }

            // Add rows
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    // Use rs.getObject(i) to fetch values based on position
                    row.add(rs.getObject(i));
                }
                tableModel.addRow(row);
            }

            donorTable.setModel(tableModel);
            
            if (tableModel.getRowCount() == 0) {
                 JOptionPane.showMessageDialog(this, 
                     "No donors found for blood group " + selectedBloodGroup + ".", 
                     "Search Complete", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            // Inform the user about the failure
            JOptionPane.showMessageDialog(this, 
                "Database connection or query error: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    } 

    public static void main(String[] args) {
        // Use system look and feel for a cleaner look
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new FindDonorFrame().setVisible(true);
        });
    }
}