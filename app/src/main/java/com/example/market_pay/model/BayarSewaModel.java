package com.example.market_pay.model;

import com.google.firebase.Timestamp;

public class BayarSewaModel {
    private String user_id;
    private String sewa_id;
    private Integer nominal;
    private Timestamp tgl_bayar;

    // Diperlukan oleh Firestore
    public BayarSewaModel() {}

    public BayarSewaModel(String user_id, String sewa_id, Integer nominal, Timestamp tgl_bayar) {
        this.user_id = user_id;
        this.sewa_id = sewa_id;
        this.nominal = nominal;
        this.tgl_bayar = tgl_bayar;
    }

    // Getter & Setter
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSewa_id() {
        return sewa_id;
    }

    public void setSewa_id(String sewa_id) {
        this.sewa_id = sewa_id;
    }

    public Integer getNominal() {
        return nominal;
    }

    public void setNominal(Integer nominal) {
        this.nominal = nominal;
    }

    public Timestamp getTgl_bayar() {
        return tgl_bayar;
    }

    public void setTgl_bayar(Timestamp tgl_bayar) {
        this.tgl_bayar = tgl_bayar;
    }
}
