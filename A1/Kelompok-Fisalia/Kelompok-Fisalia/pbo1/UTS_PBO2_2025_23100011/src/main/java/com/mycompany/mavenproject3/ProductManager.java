/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject3;

/**
 *
 * @author ASUS
 */

import java.util.ArrayList;
import java.util.List;

public class ProductManager {
    private static final List<Product> products = new ArrayList<>();

    static {
        products.add(new Product(1, "P001", "Americano", "Coffee", 10000, 10));
        products.add(new Product(2, "P002", "Pandan Latte", "Coffee", 20000, 10));
        products.add(new Product(3, "P003", "Aren Latte", "Coffee", 15000, 10));
        products.add(new Product(4, "P004", "Matcha Frappucino", "Tea", 28000, 10));
        products.add(new Product(5, "P005", "Jus Apel", "Juice", 17000, 10));
    }
    
    public static void addProduct (Product product) {
        products.add(product);
    }

    public static List<Product> getProducts() {
        return new ArrayList<>(products);
    }
    
    public static void editProduct (int index, Product updatedProduct) {
        if (index >= 0 && index < products.size()) {
                products.set(index, updatedProduct);
        }
    }

    public static void deleteProduct(int index) {
        if (index >= 0 && index < products.size()) {
            products.remove(index);
        }
    }

    public static String getText() {
        if (products.isEmpty()) {
            return "Belum ada produk.";
        }
        String text = "";
        
        for (Product product : products) {
            text += " | + product.getName() + ";
        }
        return text;
    }
}