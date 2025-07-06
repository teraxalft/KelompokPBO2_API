package com.mycompany.mavenproject3;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

public class CustomerForm extends JFrame {
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, phoneField, emailField, addressField;
    private JButton saveButton, editButton, deleteButton;

    public CustomerForm() {
        setTitle("WK. Cuan | Data Pelanggan");
        setSize(900, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // === FORM INPUT ===
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(220, 220, 220));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        String[] labels = {"Nama:", "No. HP:", "Email:", "Alamat:"};
        JTextField[] fields = new JTextField[labels.length];

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            formPanel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            fields[i] = new JTextField(25);
            formPanel.add(fields[i], gbc);
        }

        nameField = fields[0];
        phoneField = fields[1];
        emailField = fields[2];
        addressField = fields[3];

        JPanel buttonPanel = new JPanel();
        saveButton = new JButton("Tambah");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Hapus");
        buttonPanel.add(saveButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        gbc.gridx = 0; gbc.gridy = labels.length;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.NORTH);

        // === TABEL ===
        tableModel = new DefaultTableModel(new String[]{"ID", "Nama", "No. HP", "Email", "Alamat"}, 0);
        customerTable = new JTable(tableModel);
        customerTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane scrollPane = new JScrollPane(customerTable);
        add(scrollPane, BorderLayout.CENTER);

        setColumnWidths(5, 200);

        loadCustomerData();

        // === AKSI ===
        saveButton.addActionListener(e -> saveCustomer());
        editButton.addActionListener(e -> updateCustomer());
        deleteButton.addActionListener(e -> deleteCustomer());
        customerTable.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());
    }

    private void setColumnWidths(int columnCount, int width) {
        TableColumnModel columnModel = customerTable.getColumnModel();
        for (int i = 0; i < columnCount; i++) {
            columnModel.getColumn(i).setPreferredWidth(width);
        }
    }

    private void loadCustomerData() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM customer")) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("address")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveCustomer() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!");
            return;
        }

        if (!phone.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "No. HP harus angka!");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO customer(name, phone, email, address) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, name);
            stmt.setString(2, phone);
            stmt.setString(3, email);
            stmt.setString(4, address);
            stmt.executeUpdate();
            loadCustomerData();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateCustomer() {
        int row = customerTable.getSelectedRow();
        if (row != -1) {
            int id = (int) tableModel.getValueAt(row, 0);
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String address = addressField.getText().trim();

            if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi!");
                return;
            }

            if (!phone.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "No. HP harus angka!");
                return;
            }

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "UPDATE customer SET name=?, phone=?, email=?, address=? WHERE id=?")) {
                stmt.setString(1, name);
                stmt.setString(2, phone);
                stmt.setString(3, email);
                stmt.setString(4, address);
                stmt.setInt(5, id);
                stmt.executeUpdate();
                loadCustomerData();
                clearFields();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin diedit!");
        }
    }

    private void deleteCustomer() {
        int row = customerTable.getSelectedRow();
        if (row != -1) {
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int id = (int) tableModel.getValueAt(row, 0);
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement("DELETE FROM customer WHERE id=?")) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                    loadCustomerData();
                    clearFields();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus!");
        }
    }

    private void fillFormFromTable() {
        int row = customerTable.getSelectedRow();
        if (row != -1) {
            nameField.setText(tableModel.getValueAt(row, 1).toString());
            phoneField.setText(tableModel.getValueAt(row, 2).toString());
            emailField.setText(tableModel.getValueAt(row, 3).toString());
            addressField.setText(tableModel.getValueAt(row, 4).toString());
        }
    }

    private void clearFields() {
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressField.setText("");
        customerTable.clearSelection();
    }
}
