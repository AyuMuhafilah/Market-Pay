package com.example.market_pay.utils;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.EditText;
import java.util.Locale;

public class TimePicker {
    public static void showTimePicker(Context context, EditText editText, int hour, int minute) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                context,
                android.R.style.Theme_Holo_Light_Dialog,
                (view, hourOfDay, minute1) -> {
                    String selectedTime = String.format(Locale.getDefault(),
                            "%02d:%02d", hourOfDay, minute1);
                    editText.setText(selectedTime);
                },
                hour, minute, true
        );

        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.setOnCancelListener(dialog -> editText.setText(""));
        timePickerDialog.show();
    }
    public static void showTimePicker(Context context, EditText editText) {
        showTimePicker(context, editText, 12, 0);
    }
}
