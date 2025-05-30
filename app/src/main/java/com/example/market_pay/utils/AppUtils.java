package com.example.market_pay.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Patterns;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppUtils {

    public static String formatRupiah(int nominal) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        formatRupiah.setMaximumFractionDigits(0);
        return formatRupiah.format(nominal);
    }

    public static String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss", Locale.getDefault());
        return sdf.format(new Date());
    }

    public static boolean validateNoHp(Context context, String noHp) {
        if (noHp.length() < 11 || noHp.length() > 13) {
            Toast.getInstance(context).showToast("Nomor HP tidak valid");
            return false;
        }
        return true;
    }

    public static boolean validateEmail(Context context, String email) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.getInstance(context).showToast("Email tidak valid");
            return false;
        }
        return true;
    }

    public static boolean validatePassword(Context context, String password) {
        if (password.length() < 6){
            Toast.getInstance(context).showToast("Password Minimal 6 Karakter");
            return false;
        }
        return true;
    }

    public static boolean validateNik(Context context, String password) {
        if (password.length() != 16){
            Toast.getInstance(context).showToast("Nik tidak valid");
            return false;
        }
        return true;
    }
    public static boolean validateImage(Context context, Uri gambarUri) {
        if (gambarUri == null) {
            Toast.getInstance(context).showToast("Pilih Gambar Terlebih Dahulu");
            return false;
        }
        return true;
    }


}
