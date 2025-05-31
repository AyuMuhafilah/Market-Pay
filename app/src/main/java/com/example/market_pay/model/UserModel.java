package com.example.market_pay.model;

import java.util.Date;

public class UserModel {
    private Integer saldo;
    private String user_id;
    private String email;
    private String nama_lengkap;
    private String no_hp;
    private String role;
    private String profile;
    private String nik;
    private String tmp_lahir;
    private String tgl_lahir;  // pake Date
    private String jk;
    private String desa;
    private String Rt;
    private String det_alamat;

    public UserModel() {}

    public UserModel(String user_id, String email, String nama_lengkap, String no_hp, String role, String profile, Integer saldo,
                     String nik, String tmp_lahir, String tgl_lahir, String jk, String desa, String Rt, String det_alamat) {
        this.user_id = user_id;
        this.email = email;
        this.nama_lengkap = nama_lengkap;
        this.no_hp = no_hp;
        this.role = role;
        this.profile = profile;
        this.saldo = saldo;
        this.nik = nik;
        this.tmp_lahir = tmp_lahir;
        this.tgl_lahir = tgl_lahir;
        this.jk = jk;
        this.desa = desa;
        this.Rt = Rt;
        this.det_alamat = det_alamat;
    }

    // getter dan setter

    public String getUser_id() { return user_id; }
    public void setUser_id(String user_id) { this.user_id = user_id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNama_lengkap() { return nama_lengkap; }
    public void setNama_lengkap(String nama_lengkap) { this.nama_lengkap = nama_lengkap; }

    public String getNo_hp() { return no_hp; }
    public void setNo_hp(String no_hp) { this.no_hp = no_hp; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getProfile() { return profile; }
    public void setProfile(String profile) { this.profile = profile; }

    public Integer getSaldo() { return saldo; }
    public void setSaldo(Integer saldo) { this.saldo = saldo; }

    public String getNik() { return nik; }
    public void setNik(String nik) { this.nik = nik; }

    public String getTmp_lahir() { return tmp_lahir; }
    public void setTmp_lahir(String tmp_lahir) { this.tmp_lahir = tmp_lahir; }

    public String getTgl_lahir() { return tgl_lahir; }
    public void setTgl_lahir(String tgl_lahir) { this.tgl_lahir = tgl_lahir; }

    public String getJk() { return jk; }
    public void setJk(String jk) { this.jk = jk; }

    public String getDesa() { return desa; }
    public void setDesa(String desa) { this.desa = desa; }

    public String getRt() { return Rt; }
    public void setRt(String Rt) { this.Rt = Rt; }

    public String getDet_alamat() { return det_alamat; }
    public void setDet_alamat(String det_alamat) { this.det_alamat = det_alamat; }
}
