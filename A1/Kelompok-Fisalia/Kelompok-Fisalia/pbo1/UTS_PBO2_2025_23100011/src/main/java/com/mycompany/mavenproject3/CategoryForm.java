
package com.mycompany.mavenproject3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class CategoryForm extends JFrame {
    private JTextField idField, nameField, descField;
    private JTable table;
    private DefaultTableModel model;
    private Mavenproject3 mainApp;

    public CategoryForm(Mavenproject3 mainApp) {
        this.mainApp = mainApp;

        setTitle("Kelola Kategori");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.LIGHT_GRAY);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("ID"), gbc);
        gbc.gridx = 1;
        idField = new JTextField(20);
        panel.add(idField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Kategori"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Deskripsi"), gbc);
        gbc.gridx = 1;
        descField = new JTextField(20);
        panel.add(descField, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        gbc.gridwidth = 3;
        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("Tambah");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Hapus");
        btnPanel.add(addBtn);
        btnPanel.add(editBtn); // opsional: belum diimplementasikan
        btnPanel.add(deleteBtn);
        panel.add(btnPanel, gbc);

        model = new DefaultTableModel(new Object[]{"ID", "Kategori", "Deskripsi"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        loadCategoryData();

        // Tombol Tambah
        addBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String desc = descField.getText().trim();

            if (!name.isEmpty() && !isCategoryExist(name)) {
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement("INSERT INTO category (id, name, description) VALUES (?, ?, ?)")) {
                    stmt.setString(1, id);
                    stmt.setString(2, name);
                    stmt.setString(3, desc);
                    stmt.executeUpdate();
                    loadCategoryData();
                    if (mainApp != null) {
                        mainApp.updateAllProductFormCategoryCombo();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Tombol Hapus
        deleteBtn.addActionListener(e -> {
            int selected = table.getSelectedRow();
            if (selected != -1) {
                String id = model.getValueAt(selected, 0).toString();
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement("DELETE FROM category WHERE id = ?")) {
                    stmt.setString(1, id);
                    stmt.executeUpdate();
                    loadCategoryData();
                    if (mainApp != null) {
                        mainApp.updateAllProductFormCategoryCombo();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Klik pada tabel → tampilkan ke input
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                idField.setText(model.getValueAt(row, 0).toString());
                nameField.setText(model.getValueAt(row, 1).toString());
                descField.setText(model.getValueAt(row, 2).toString());
            }
        });

        getContentPane().add(panel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    private boolean isCategoryExist(String name) {
        for (int i = 0; i < model.getRowCount(); i++) {
            if (name.equalsIgnoreCase(model.getValueAt(i, 1).toString())) {
                return true;
            }
        }
        return false;
    }

    private void loadCategoryData() {
        model.setRowCount(0); // bersihkan tabel dulu
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM category");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String desc = rs.getString("description");
                model.addRow(new Object[]{id, name, desc});
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}