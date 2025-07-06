package com.mycompany.mavenproject3;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class ProductForm extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField codeField, nameField, priceField, stockField;
    private JComboBox<String> categoryCombo;
    private Mavenproject3 mainApp;

    public ProductForm(Mavenproject3 mainApp) {
        this.mainApp = mainApp;
        setTitle("WK. Cuan | Stok Barang");
        setSize(800, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"ID", "Kode", "Nama", "Kategori", "Harga", "Stok"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        codeField = new JTextField(8);
        nameField = new JTextField(10);
        categoryCombo = new JComboBox<>();
        priceField = new JTextField(7);
        stockField = new JTextField(5);

        formPanel.add(new JLabel("Kode:")); formPanel.add(codeField);
        formPanel.add(new JLabel("Nama:")); formPanel.add(nameField);
        formPanel.add(new JLabel("Kategori:")); formPanel.add(categoryCombo);
        formPanel.add(new JLabel("Harga:")); formPanel.add(priceField);
        formPanel.add(new JLabel("Stok:")); formPanel.add(stockField);
        add(formPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Tambah");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Hapus");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addProduct());
        editButton.addActionListener(e -> updateProduct());
        deleteButton.addActionListener(e -> deleteProduct());

        table.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());

        loadCategoryCombo();
        loadProductData();
    }

    public void updateCategoryCombo() {
        loadCategoryCombo();
    }

    private void loadCategoryCombo() {
        categoryCombo.removeAllItems();
        for (String cat : mainApp.getCategoryList()) {
            categoryCombo.addItem(cat);
        }
    }

    private void loadProductData() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM product")) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("code"));
                row.add(rs.getString("name"));
                row.add(rs.getString("category"));
                row.add(rs.getDouble("price"));
                row.add(rs.getInt("stock"));
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addProduct() {
        String code = codeField.getText().trim();
        String name = nameField.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();
        String priceStr = priceField.getText().trim();
        String stockStr = stockField.getText().trim();

        if (code.isEmpty() || name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field wajib diisi!");
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int stock = Integer.parseInt(stockStr);
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO product (code, name, category, price, stock) VALUES (?, ?, ?, ?, ?)");) {
                stmt.setString(1, code);
                stmt.setString(2, name);
                stmt.setString(3, category);
                stmt.setDouble(4, price);
                stmt.setInt(5, stock);
                stmt.executeUpdate();
                loadProductData();
                clearFields();
                mainApp.refreshBanner();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga dan stok harus berupa angka!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String code = codeField.getText().trim();
            String name = nameField.getText().trim();
            String category = (String) categoryCombo.getSelectedItem();
            String priceStr = priceField.getText().trim();
            String stockStr = stockField.getText().trim();

            try {
                double price = Double.parseDouble(priceStr);
                int stock = Integer.parseInt(stockStr);

                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement("UPDATE product SET code=?, name=?, category=?, price=?, stock=? WHERE id=?")) {
                    stmt.setString(1, code);
                    stmt.setString(2, name);
                    stmt.setString(3, category);
                    stmt.setDouble(4, price);
                    stmt.setInt(5, stock);
                    stmt.setInt(6, id);
                    stmt.executeUpdate();
                    loadProductData();
                    clearFields();
                    mainApp.refreshBanner();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Harga dan stok harus berupa angka!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih data terlebih dahulu");
        }
    }

    private void deleteProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement("DELETE FROM product WHERE id=?")) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                    loadProductData();
                    clearFields();
                    mainApp.refreshBanner();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row != -1) {
            codeField.setText(tableModel.getValueAt(row, 1).toString());
            nameField.setText(tableModel.getValueAt(row, 2).toString());
            categoryCombo.setSelectedItem(tableModel.getValueAt(row, 3).toString());
            priceField.setText(tableModel.getValueAt(row, 4).toString());
            stockField.setText(tableModel.getValueAt(row, 5).toString());
        }
    }

    private void clearFields() {
        codeField.setText("");
        nameField.setText("");
        priceField.setText("");
        stockField.setText("");
        categoryCombo.setSelectedIndex(0);
        table.clearSelection();
    }
}
