package com.example.market_pay.model;

public class MerchantModel {
    private String merchant_id;
    private String userId;
    private String usaha;
    private String deskripsi;
    private String buka;
    private String tutup;
    private String image;
    private Boolean sts_merchant;

    public MerchantModel() {
    }

    public MerchantModel(String merchant_id, String userId, String usaha, String deskripsi, String buka, String tutup, String image, Boolean sts_merchant) {
        this.merchant_id = merchant_id;
        this.userId = userId;
        this.usaha = usaha;
        this.deskripsi = deskripsi;
        this.buka = buka;
        this.tutup = tutup;
        this.image = image;
        this.sts_merchant = sts_merchant;
    }

    // Getter dan Setter
    public String getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsaha() {
        return usaha;
    }

    public void setUsaha(String usaha) {
        this.usaha = usaha;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getBuka() {
        return buka;
    }

    public void setBuka(String buka) {
        this.buka = buka;
    }

    public String getTutup() {
        return tutup;
    }

    public void setTutup(String tutup) {
        this.tutup = tutup;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean getStatus() {
        return sts_merchant;
    }

    public void setStatus(Boolean status) {
        this.sts_merchant = status;
    }
}
