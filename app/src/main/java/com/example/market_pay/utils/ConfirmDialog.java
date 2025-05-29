package com.example.market_pay.utils;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

public class ConfirmDialog {
    public static void show(Context context, String message, DialogInterface.OnClickListener positiveListener) {
        new AlertDialog.Builder(context)
                .setTitle("Konfirmasi")
                .setMessage(message)
                .setPositiveButton("Ya", positiveListener)
                .setNegativeButton("Tidak", null)
                .show();
    }
}
