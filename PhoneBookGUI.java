import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PhoneBookGUI {
    private static JTable table;
    private static DefaultTableModel model;
    private static JTextField nameField, phoneField, emailField, dobField;
    private static Connection conn;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Phone Book");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            String[] columnNames = {"Contact Name", "Phone Number", "Email", "Date of Birth"};
            model = new DefaultTableModel(columnNames, 0);
            table = new JTable(model);

            JPanel inputPanel = new JPanel(new GridLayout(6, 2));
            nameField = new JTextField();
            phoneField = new JTextField();
            emailField = new JTextField();
            dobField = new JTextField();
            JButton addButton = new JButton("Add Contact");
            JButton updateButton = new JButton("Update Contact");
            JButton deleteButton = new JButton("Delete Contact");
            JButton viewButton = new JButton("View Contacts");

            inputPanel.add(new JLabel("Contact Name:"));
            inputPanel.add(nameField);
            inputPanel.add(new JLabel("Phone Number:"));
            inputPanel.add(phoneField);
            inputPanel.add(new JLabel("Email:"));
            inputPanel.add(emailField);
            inputPanel.add(new JLabel("Date of Birth:"));
            inputPanel.add(dobField);
            inputPanel.add(addButton);
            inputPanel.add(updateButton);
            inputPanel.add(deleteButton);
            inputPanel.add(viewButton);

            addButton.addActionListener(e -> addContact());
            updateButton.addActionListener(e -> updateContact());
            deleteButton.addActionListener(e -> deleteContact());
            viewButton.addActionListener(e -> viewContacts());

            frame.getContentPane().add(inputPanel, BorderLayout.NORTH);

            JScrollPane scrollPane = new JScrollPane(table);
            table.setPreferredScrollableViewportSize(new Dimension(500, 70));
            table.setFillsViewportHeight(true);

            frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            try {
                conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbcontacts", "root", "123456");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    
    private static void updateContact() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String name = nameField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();
            String dob = dobField.getText();
            try {
                PreparedStatement stmt = conn.prepareStatement("UPDATE contacts1 SET phone = ?, email = ?, dob = ? WHERE name = ?");
                stmt.setString(1, phone);
                stmt.setString(2, email);
                stmt.setString(3, dob);
                stmt.setString(4, name);
                stmt.executeUpdate();
                model.setValueAt(name, selectedRow, 0);
                model.setValueAt(phone, selectedRow, 1);
                model.setValueAt(email, selectedRow, 2);
                model.setValueAt(dob, selectedRow, 3);
                clearFields();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void deleteContact() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String name = model.getValueAt(selectedRow, 0).toString();
            try {
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM contacts1 WHERE name = ?");
                stmt.setString(1, name);
                stmt.executeUpdate();
                model.removeRow(selectedRow);
                clearFields();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void viewContacts() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM contacts1 ORDER BY name ASC");
            model.setRowCount(0);
            while (rs.next()) {
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                String dob = rs.getString("dob");
                model.addRow(new Object[]{name, phone, email, dob});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void clearFields() {
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        dobField.setText("");
    }

    private static void addContact() {
        String name = nameField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String dob = dobField.getText();
    
        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || dob.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill in all fields.");
        } else {
            try {
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO contacts1 (name, phone, email, dob) VALUES (?, ?, ?, ?)");
                stmt.setString(1, name);
                stmt.setString(2, phone);
                stmt.setString(3, email);
                stmt.setString(4, dob);
                stmt.executeUpdate();
                model.addRow(new Object[]{name, phone, email, dob});
                clearFields();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
}
