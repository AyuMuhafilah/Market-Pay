package com.example.market_pay.utils;

import android.content.Context;

public class Toast {
    // Instansi statis yang akan digunakan untuk akses global
    public static Toast instance;

    // Context aplikasi, untuk memastikan dapat diakses di seluruh aplikasi
    public Context context;

    // Constructor privat untuk menghindari instansiasi dari luar kelas
    public Toast(Context context) {
        this.context = context.getApplicationContext();
    }

    // Method untuk mendapatkan instansi Singleton
    public static synchronized Toast getInstance(Context context) {
        if (instance == null) {
            instance = new Toast(context);
        }
        return instance;
    }

    public void showToast(String message) {
        android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show();
    }
}

