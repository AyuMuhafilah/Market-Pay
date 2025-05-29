package com.example.market_pay.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.EditText;
import java.util.Calendar;
import java.util.Locale;

public class DatePicker {
    public static void showDatePicker(Context context, EditText editText, int defaultYear, int defaultMonth, int defaultDay) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                android.R.style.Theme_Holo_Light_Dialog,
                (view, year, monthOfYear, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(),
                            "%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year);
                    editText.setText(selectedDate);
                },
                defaultYear, defaultMonth, defaultDay
        );
        datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        datePickerDialog.getDatePicker().setCalendarViewShown(false);
        datePickerDialog.getDatePicker().setSpinnersShown(true);
        datePickerDialog.setOnCancelListener(dialog -> editText.setText(""));
        datePickerDialog.show();
    }
    public static void showDatePicker(Context context, EditText editText) {
        showDatePicker(context, editText, 2002, Calendar.FEBRUARY, 10);
    }
}
