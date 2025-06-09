package com.example.market_pay.model;

import com.google.firebase.Timestamp;

public class TransaksiModel {
    private String id;
    private String jenis;
    private Integer nominal;
    private Timestamp tgl;
    private String user_id;

    public TransaksiModel() {
        // Diperlukan oleh Firestore
    }

    public TransaksiModel(String id, String jenis, Integer nominal, Timestamp tgl, String user_id) {
        this.id = id;
        this.jenis = jenis;
        this.nominal = nominal;
        this.tgl = tgl;
        this.user_id = user_id;
    }

    // Getter & Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getJenis() { return jenis; }
    public void setJenis(String jenis) { this.jenis = jenis; }

    public Integer getNominal() { return nominal; }
    public void setNominal(Integer nominal) { this.nominal = nominal; }

    public Timestamp getTgl() { return tgl; }
    public void setTgl(Timestamp tgl) { this.tgl = tgl; }

    public String getUser_id() { return user_id; }
    public void setUser_id(String user_id) { this.user_id = user_id; }
}

