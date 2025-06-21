package com.example.market_pay.model;

public class BayarSewaWithMerchant {
    public String tanggal;
    public String namaUser;
    public String bulan;
    public String nominal;
    public String status;

    public BayarSewaWithMerchant(String tanggal, String namaUser, String bulan, String nominal, String status) {
        this.tanggal = tanggal;
        this.namaUser = namaUser;
        this.bulan = bulan;
        this.nominal = nominal;
        this.status = status;
    }
}


