package com.mycompany.mavenproject3;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @GetMapping
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM product")) {
            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("stock")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    @PostMapping
    public String addProduct(@RequestBody Product p) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO product (code, name, category, price, stock) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setString(1, p.getCode());
            stmt.setString(2, p.getName());
            stmt.setString(3, p.getCategory());
            stmt.setDouble(4, p.getPrice());
            stmt.setInt(5, p.getStock());
            stmt.executeUpdate();
            return "Produk berhasil ditambahkan";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Gagal menambahkan produk";
        }
    }

    @PutMapping("/{id}")
    public String updateProduct(@PathVariable int id, @RequestBody Product p) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE product SET code=?, name=?, category=?, price=?, stock=? WHERE id=?")) {
            stmt.setString(1, p.getCode());
            stmt.setString(2, p.getName());
            stmt.setString(3, p.getCategory());
            stmt.setDouble(4, p.getPrice());
            stmt.setInt(5, p.getStock());
            stmt.setInt(6, id);
            int rows = stmt.executeUpdate();
            return rows > 0 ? "Produk berhasil diupdate" : "Produk tidak ditemukan";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Gagal mengupdate produk";
        }
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM product WHERE id=?")) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            return rows > 0 ? "Produk berhasil dihapus" : "Produk tidak ditemukan";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Gagal menghapus produk";
        }
    }
}

