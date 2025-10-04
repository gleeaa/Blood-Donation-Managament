import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.Date;
import java.util.Vector;

public class AppointmentsFrame extends JFrame {
    private String username;
    private String fullName;
    private JTable appointmentTable;
    private DefaultTableModel tableModel;

    public AppointmentsFrame(String username, String fullName) {
        this.username = username;
        this.fullName = fullName;

        setTitle("Appointments Management");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Book Appointment", createBookingPanel());
        tabbedPane.addTab("View Appointments", createViewPanel());

        add(tabbedPane);
    }

    private JPanel createBookingPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Date Picker
        JLabel dateLabel = new JLabel("Appointment Date:");
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(new Date()); // Default to today

        // Time Selection
        JLabel timeLabel = new JLabel("Appointment Time:");
        String[] times = {"09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00"};
        JComboBox<String> timeBox = new JComboBox<>(times);

        // Location
        JLabel locationLabel = new JLabel("Location:");
        JComboBox<String> locationBox = new JComboBox<>(new String[]{
                "Aster Mims Kasaragod", "CM Hospital", "Muliyar CHC", "Government College Kasaragod"
        });

        // Book Button
        JButton bookBtn = new JButton("Book Appointment");
        bookBtn.addActionListener(e -> bookAppointment(
                (Date) dateSpinner.getValue(),
                (String) timeBox.getSelectedItem(),
                (String) locationBox.getSelectedItem()
                // Reason is now removed from the arguments
        ));

        // Layout
        gbc.gridx = 0; gbc.gridy = 0; panel.add(dateLabel, gbc);
        gbc.gridx = 1; panel.add(dateSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(timeLabel, gbc);
        gbc.gridx = 1; panel.add(timeBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(locationLabel, gbc);
        gbc.gridx = 1; panel.add(locationBox, gbc);

        // NOTE: Previous lines for reasonLabel and reasonArea are removed.
        
        gbc.gridx = 1; gbc.gridy = 3; // Shifted up from gbc.gridy = 4
        panel.add(bookBtn, gbc);

        return panel;
    }

    // Updated method signature: removed String reason
    private void bookAppointment(Date date, String time, String location) {
        // Reason validation check is now removed.

        try (Connection con = ConnectionProvider.getCon()) {
            // Updated query: removed 'reason' column and its placeholder
            String query = "INSERT INTO appointments (username, full_name, appointment_date, " +
                    "appointment_time, location, status) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, username);
                ps.setString(2, fullName);
                ps.setDate(3, new java.sql.Date(date.getTime()));
                ps.setString(4, time);
                ps.setString(5, location);
                // NOTE: ps.setString(6, reason) is removed.
                ps.setString(6, "Scheduled"); // Status is now parameter 6

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Appointment booked successfully!");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error booking appointment: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private JPanel createViewPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table setup: Removed "Reason" column from table headers
        tableModel = new DefaultTableModel(
                new String[]{"ID", "Date", "Time", "Location", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        appointmentTable = new JTable(tableModel);
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(appointmentTable);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadAppointments());

        JButton cancelBtn = new JButton("Cancel Appointment");
        cancelBtn.addActionListener(this::cancelAppointment);

        buttonPanel.add(refreshBtn);
        buttonPanel.add(cancelBtn);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        loadAppointments();
        return panel;
    }

    private void loadAppointments() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                try (Connection con = ConnectionProvider.getCon()) {
                    // Updated query: removed 'reason' column from SELECT list
                    String query = "SELECT id, appointment_date, appointment_time, " +
                            "location, status FROM appointments " +
                            "WHERE username = ? ORDER BY appointment_date DESC";

                    try (PreparedStatement ps = con.prepareStatement(query)) {
                        ps.setString(1, username);
                        ResultSet rs = ps.executeQuery();

                        tableModel.setRowCount(0); // Clear existing data

                        while (rs.next()) {
                            Vector<Object> row = new Vector<>();
                            row.add(rs.getInt("id"));
                            row.add(rs.getDate("appointment_date"));
                            row.add(rs.getString("appointment_time"));
                            row.add(rs.getString("location"));
                            row.add(rs.getString("status"));
                            tableModel.addRow(row);
                        }
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException("Database error: " + ex.getMessage(), ex);
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); // Check for exceptions
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(AppointmentsFrame.this,
                            "Error loading appointments: " + ex.getCause().getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    // ... (class and other methods)

    private void cancelAppointment(ActionEvent e) {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select an appointment to cancel",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this appointment?",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // --- CRITICAL FIX HERE: Safely retrieve the Integer ID ---
        Object idObject = tableModel.getValueAt(selectedRow, 0);
        int appointmentId;

        try {
            // Ensure the object is converted to Integer first, then unboxed to int
            appointmentId = (Integer) idObject; 
        } catch (ClassCastException ex) {
            // Fallback for extreme cases where it might be a Long or String
            try {
                appointmentId = Integer.parseInt(idObject.toString());
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Could not retrieve appointment ID. Data format error.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        // --- END CRITICAL FIX ---


        try (Connection con = ConnectionProvider.getCon()) {
            // Verify query syntax and column name 'id'
            String query = "UPDATE appointments SET status = 'Cancelled' WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setInt(1, appointmentId);
                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Appointment cancelled successfully");
                    loadAppointments(); // Refresh the view
                } else {
                    // This else block is vital for debugging!
                    JOptionPane.showMessageDialog(this, 
                        "Cancellation failed: No rows were updated. Check if the ID exists.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error cancelling appointment: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // For testing purposes
            new AppointmentsFrame("testUser", "John Doe").setVisible(true);
        });
    }
}