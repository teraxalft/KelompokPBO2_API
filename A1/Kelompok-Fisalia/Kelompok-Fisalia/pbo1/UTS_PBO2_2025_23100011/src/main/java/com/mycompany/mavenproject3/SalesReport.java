/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject3;

/**
 *
 * @author ASUS
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;

public class SalesReport extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private JDateChooser startDateChooser, endDateChooser;
    private JComboBox<String> namaProdukCombo;

    public SalesReport(Mavenproject3 mainApp) {
        setTitle("WK. Cuan | Laporan Penjualan");
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header: Judul dan Tanggal akses
        JLabel lblJudul = new JLabel("LAPORAN PENJUALAN", SwingConstants.CENTER);
        lblJudul.setFont(new Font("Arial", Font.BOLD, 18));
        lblJudul.setBorder(BorderFactory.createEmptyBorder(10, 0, 2, 0));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        JLabel lblTanggalAkses = new JLabel(
                "Tanggal akses: " + dtf.format(LocalDate.now()),
                SwingConstants.CENTER
        );
        lblTanggalAkses.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTanggalAkses.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JPanel panelJudul = new JPanel(new BorderLayout());
        panelJudul.add(lblJudul, BorderLayout.NORTH);
        panelJudul.add(lblTanggalAkses, BorderLayout.SOUTH);

        // Filter Panel
        JPanel panelFilter = new JPanel(new FlowLayout());
        panelFilter.setBackground(Color.LIGHT_GRAY);

        startDateChooser = new JDateChooser();
        endDateChooser = new JDateChooser();
        Date today = new Date();
        startDateChooser.setDate(today);
        endDateChooser.setDate(today);

        namaProdukCombo = new JComboBox<>();
        namaProdukCombo.addItem("-- Semua --");
        for (Product p : mainApp.getProductList()) {
            namaProdukCombo.addItem(p.getName());
        }

        JButton btnFilter = new JButton("Filter");

        panelFilter.add(new JLabel("Tanggal Mulai"));
        panelFilter.add(startDateChooser);
        panelFilter.add(new JLabel("Tanggal Selesai"));
        panelFilter.add(endDateChooser);
        panelFilter.add(new JLabel("Nama Produk"));
        panelFilter.add(namaProdukCombo);
        panelFilter.add(btnFilter);

        // Panel atas
        JPanel panelAtas = new JPanel(new BorderLayout());
        panelAtas.add(panelJudul, BorderLayout.NORTH);
        panelAtas.add(panelFilter, BorderLayout.CENTER);

        // Tabel
        String[] kolom = {"Tanggal", "Kode Produk", "Nama Produk", "Harga Satuan", "Jumlah", "Total"};
        tableModel = new DefaultTableModel(null, kolom);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        add(panelAtas, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Tombol Filter
        btnFilter.addActionListener(e -> {
            tableModel.setRowCount(0); // hapus data lama
            List<Sale> sales = mainApp.getSales();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

            for (Sale s : sales) {
                boolean cocok = true;

                Date start = startDateChooser.getDate();
                Date end = endDateChooser.getDate();
                if (start != null && s.getDate().before(start)) cocok = false;
                if (end != null && s.getDate().after(end)) cocok = false;

                String namaDipilih = (String) namaProdukCombo.getSelectedItem();
                if (!"-- Semua --".equals(namaDipilih) && !s.getProductName().equals(namaDipilih)) cocok = false;

                if (cocok) {
                    tableModel.addRow(new Object[]{
                            sdf.format(s.getDate()),
                            s.getProductCode(),
                            s.getProductName(),
                            s.getPrice(),
                            s.getQuantity(),
                            s.getTotal()
                    });
                }
            }
        });
    }
}
