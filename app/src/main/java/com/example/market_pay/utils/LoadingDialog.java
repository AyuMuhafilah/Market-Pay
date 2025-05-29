package com.example.market_pay.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.example.market_pay.R;

public class LoadingDialog {
    private final Dialog dialog;

    public LoadingDialog(Context context) {
        dialog = new Dialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.activity_loading_dialog, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }
    public void show() {
        if (!dialog.isShowing()) dialog.show();
    }
    public void dismiss() {
        if (dialog.isShowing()) dialog.dismiss();
    }
}