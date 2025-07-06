package com.mycompany.mavenproject3;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
@RequestMapping("/api/categories")
public class CategoryController {

    @GetMapping
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM category");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categories.add(new Category(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    @PostMapping
    public String addCategory(@RequestBody Category c) {
        System.out.println("Received Category: " + c);
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO category (id, name, description) VALUES (?, ?, ?)")) {

            stmt.setString(1, c.getId());
            stmt.setString(2, c.getName());
            stmt.setString(3, c.getDescription());
            stmt.executeUpdate();
            return "Kategori berhasil ditambahkan";
        } catch (SQLException e) {
            e.printStackTrace();  // ← penting untuk melihat error SQL-nya
            return "Gagal menambahkan kategori: " + e.getMessage(); // ← agar pesan error dikirimkan
        }
    }

    @PutMapping("/{id}")
    public String updateCategory(@PathVariable String id, @RequestBody Category c) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE category SET name=?, description=? WHERE id=?")) {

            stmt.setString(1, c.getName());
            stmt.setString(2, c.getDescription());
            stmt.setString(3, id);
            int rows = stmt.executeUpdate();
            return rows > 0 ? "Kategori berhasil diupdate" : "Kategori tidak ditemukan";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Gagal mengupdate kategori";
        }
    }

    @DeleteMapping("/{id}")
    public String deleteCategory(@PathVariable String id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM category WHERE id=?")) {

            stmt.setString(1, id);
            int rows = stmt.executeUpdate();
            return rows > 0 ? "Kategori berhasil dihapus" : "Kategori tidak ditemukan";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Gagal menghapus kategori";
        }
    }
}
