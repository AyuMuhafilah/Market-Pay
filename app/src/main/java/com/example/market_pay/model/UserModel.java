package com.example.market_pay.model;

public class UserModel {
    public String user_id, email, nama_lengkap, no_hp, role, profile;
    public Integer saldo;

    public UserModel(String user_id, String email, String nama_lengkap, String no_hp, String role, String profile, Integer saldo) {
        this.user_id = user_id;
        this.email = email;
        this.nama_lengkap = nama_lengkap;
        this.no_hp = no_hp;
        this.role = role;
        this.profile = profile;
        this.saldo = saldo;
    }

    public UserModel() {}

    // Getter dan Setter
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNama_lengkap() {
        return nama_lengkap;
    }

    public void setNama_lengkap(String nama_lengkap) {
        this.nama_lengkap = nama_lengkap;
    }

    public String getNo_hp() {
        return no_hp;
    }

    public void setNo_hp(String no_hp) {
        this.no_hp = no_hp;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public Integer getSaldo() {
        return saldo;
    }

    public void setSaldo(Integer saldo) {
        this.saldo = saldo;
    }
}
