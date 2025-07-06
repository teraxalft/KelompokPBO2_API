package com.mycompany.mavenproject3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Mavenproject3 extends JFrame implements Runnable {
    private String text;
    private int x;
    private int width;

    private BannerPanel bannerPanel;
    private JButton addProductButton;
    private JButton addSellButton;
    private JButton addCustomerButton;
    private JButton kelolaKategoriButton;
    private JButton laporanPenjualanButton;
    private JButton logoutButton;

    private List<Product> productList = new ArrayList<>();
    private List<String> categoryList = new ArrayList<>();
    private List<ProductForm> productForms = new ArrayList<>();
    private List<Sale> sales = new ArrayList<>();

    public Mavenproject3() {
        setTitle("WK. STI Chill");
        setSize(800, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        loadProductsFromDatabase();
        loadCategoriesFromDatabase();

        this.text = getBannerTextFromProducts();
        this.x = -getFontMetrics(new Font("Arial", Font.BOLD, 18)).stringWidth(text);

        bannerPanel = new BannerPanel();
        add(bannerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        addProductButton = new JButton("Kelola Produk");
        addSellButton = new JButton("Penjualan");
        addCustomerButton = new JButton("Kelola Customer");
        kelolaKategoriButton = new JButton("Kategori Produk");
        laporanPenjualanButton = new JButton("Laporan Penjualan");
        logoutButton = new JButton("Logout");

        bottomPanel.add(addProductButton);
        bottomPanel.add(addSellButton);
        bottomPanel.add(addCustomerButton);
        bottomPanel.add(kelolaKategoriButton);
        bottomPanel.add(laporanPenjualanButton);
        bottomPanel.add(logoutButton);

        add(bottomPanel, BorderLayout.SOUTH);

        addProductButton.addActionListener(e -> {
            ProductForm pf = new ProductForm(this);
            productForms.add(pf);
            pf.setVisible(true);
        });

        addSellButton.addActionListener(e -> new SellingForm(this).setVisible(true));
        addCustomerButton.addActionListener(e -> new CustomerForm().setVisible(true));
        kelolaKategoriButton.addActionListener(e -> new CategoryForm(this).setVisible(true));
        laporanPenjualanButton.addActionListener(e -> new SalesReport(this).setVisible(true));

        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin logout?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                System.exit(0);
            }
        });

        setVisible(true);
        new Thread(this).start();
    }

    private void loadProductsFromDatabase() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM product")) {

            productList.clear();
            while (rs.next()) {
                int id = rs.getInt("id");
                String code = rs.getString("code");
                String name = rs.getString("name");
                String category = rs.getString("category");
                int price = rs.getInt("price");
                int stock = rs.getInt("stock");
                productList.add(new Product(id, code, name, category, price, stock));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal mengambil data produk dari database", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCategoriesFromDatabase() {
        categoryList.clear();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM category")) {

            while (rs.next()) {
                String name = rs.getString("name");
                categoryList.add(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class BannerPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLUE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString(text, x, getHeight() / 2 + 7);
        }
    }

    @Override
    public void run() {
        width = getWidth();
        while (true) {
            x += 2;
            if (x > width) {
                x = -getFontMetrics(new Font("Arial", Font.BOLD, 18)).stringWidth(text);
            }
            bannerPanel.repaint();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public String getBannerTextFromProducts() {
        StringBuilder sb = new StringBuilder("Menu yang tersedia: ");
        for (int i = 0; i < productList.size(); i++) {
            sb.append(productList.get(i).getName());
            if (i < productList.size() - 1) sb.append(" | ");
        }
        return sb.toString();
    }

    public void setBannerText(String newText) {
        this.text = newText;
        this.x = -getFontMetrics(new Font("Arial", Font.BOLD, 18)).stringWidth(text);
    }

    public void refreshBanner() {
        loadProductsFromDatabase();
        setBannerText(getBannerTextFromProducts());
    }

    public List<Product> getProductList() {
        return productList;
    }

    public List<String> getCategoryList() {
        return categoryList;
    }

    public void addCategory(String category) {
        if (!categoryList.contains(category)) {
            categoryList.add(category);
            updateAllProductFormCategoryCombo();
        }
    }

    public void removeCategory(String category) {
        categoryList.remove(category);
        updateAllProductFormCategoryCombo();
    }

    public void updateAllProductFormCategoryCombo() {
        loadCategoriesFromDatabase();
        for (ProductForm pf : productForms) {
            pf.updateCategoryCombo();
        }
    }

    public List<Sale> getSales() {
        return sales;
    }

    public void addSale(Sale sale) {
        sales.add(sale);
    }

    public static void main(String[] args) {
        JOptionPane.showMessageDialog(null, "Akses ditolak! Silakan login terlebih dahulu.", "Akses Dibatasi", JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }
}
