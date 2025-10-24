import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.Vector;

public class ViewRequestsPanel extends JPanel {

    private JTable requestsTable;
    private DefaultTableModel tableModel;
    
    // Constants for styling (match HospitalHomeFrame)
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180); 
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public ViewRequestsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("PENDING BLOOD REQUESTS", SwingConstants.CENTER);
        titleLabel.setFont(SUBTITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        
        // Table setup
        String[] columns = {"ID", "Date", "Blood Group", "Units", "Hospital", "Requester Name", "Mobile"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        requestsTable = new JTable(tableModel);
        requestsTable.setFont(REGULAR_FONT);
        JScrollPane scrollPane = new JScrollPane(requestsTable);
        
        JButton refreshButton = new JButton("Refresh List");
        refreshButton.setBackground(PRIMARY_COLOR);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(this::refreshData);

        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(refreshButton, BorderLayout.SOUTH);
        
        // Initial load
        loadRequestsData();
    }

    private void refreshData(ActionEvent e) {
        loadRequestsData();
    }

    private void loadRequestsData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        try (Connection con = ConnectionProvider.getCon()) {
            // Select all requests that are currently 'Pending'
            String query = "SELECT request_id, request_date, blood_group, units_required, " +
                           "hospital_name, full_name, mobile FROM blood_requests WHERE status = 'Pending' " +
                           "ORDER BY request_date ASC";

            try (PreparedStatement ps = con.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("request_id"));
                    row.add(rs.getDate("request_date"));
                    row.add(rs.getString("blood_group"));
                    row.add(rs.getInt("units_required"));
                    row.add(rs.getString("hospital_name"));
                    row.add(rs.getString("full_name"));
                    row.add(rs.getString("mobile"));
                    
                    tableModel.addRow(row);
                }

                if (tableModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "No pending blood requests at this time.", 
                        "Info", JOptionPane.INFORMATION_MESSAGE);
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading requests: " + ex.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
