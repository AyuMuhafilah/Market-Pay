package com.example.market_pay.model;

public class SewaModel {
    private String sewa_id;
    private Integer nominal;
    private Boolean status;

    public SewaModel() {
        // Diperlukan oleh Firestore
    }

    public SewaModel(String sewa_id, Integer nominal, Boolean status) {
        this.sewa_id = sewa_id;
        this.status = status;
        this.nominal = nominal;
    }

    // Getter & Setter
    public String getId() { return sewa_id; }
    public void setId(String sewa_id) { this.sewa_id = sewa_id; }

    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }

    public Integer getNominal() { return nominal; }
    public void setNominal(Integer nominal) { this.nominal = nominal; }
}

