package com.example.market_pay.utils;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatRupiah {

    public static void setRupiahFormat(TextInputEditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    editText.removeTextChangedListener(this);
                    String cleanString = s.toString().replaceAll("[^\\d]", "");
                    try {
                        long parsed = Long.parseLong(cleanString);
                        String formatted = NumberFormat.getInstance(new Locale("id", "ID")).format(parsed);
                        current = formatted;
                        editText.setText(formatted);
                        int cursorPosition = Math.min(formatted.length(), editText.getText().length());
                        editText.setSelection(cursorPosition);
                    } catch (NumberFormatException e) {
                        current = "";
                        editText.setText("");
                    }
                    editText.addTextChangedListener(this);
                }
            }
        });
    }
}
