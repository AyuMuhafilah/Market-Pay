package com.example.market_pay.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class AppUtils {

    public static String formatRupiah(int nominal) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        formatRupiah.setMaximumFractionDigits(0);
        return formatRupiah.format(nominal);
    }

}
