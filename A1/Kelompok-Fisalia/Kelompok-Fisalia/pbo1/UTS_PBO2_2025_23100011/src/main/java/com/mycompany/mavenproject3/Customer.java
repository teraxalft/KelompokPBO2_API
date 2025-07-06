    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
     */
    package com.mycompany.mavenproject3;

    /**
     *
     * @author ASUS
     */

    public class Customer {
        private int id;
        private String name;
        private String phone;
        private String email;
        private String address;

        public Customer() {
        }


        // Constructor untuk membuat Customer baru (ID akan di-generate DB)
        public Customer(String name, String phone, String email, String address) {
            this.name = name;
            this.phone = phone;
            this.email = email;
            this.address = address;
        }

        // Constructor untuk Customer yang sudah ada di DB (dengan ID)
        public Customer(int id, String name, String phone, String email, String address) {
            this.id = id;
            this.name = name;
            this.phone = phone;
            this.email = email;
            this.address = address;
        }

        public int getId() {
            return id;
        }

        // Tambahkan setter untuk ID jika diperlukan, terutama saat mengambil dari DB
        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public String getPhone() {
            return phone;
        }

        public String getEmail() {
            return email;
        }

        public String getAddress() {
            return address;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
