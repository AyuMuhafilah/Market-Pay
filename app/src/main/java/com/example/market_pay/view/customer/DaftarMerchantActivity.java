package com.example.market_pay.view.customer;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.market_pay.R;
import com.example.market_pay.helper.WilayahHelper;
import com.example.market_pay.utils.DatePicker;
import com.example.market_pay.utils.TimePicker;
import com.google.android.material.textfield.TextInputEditText;

public class DaftarMerchantActivity extends AppCompatActivity {

    private TextInputEditText editTextDate, pilihFile;
    private EditText editTextTimeBuka, editTextTimeTutup;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_merchant);

        initViews();
        setListeners();
        new WilayahHelper(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        back = findViewById(R.id.iconBack);
        pilihFile = findViewById(R.id.pilihFile);
        editTextDate = findViewById(R.id.editTextDate);
        editTextTimeBuka = findViewById(R.id.txtBuka);
        editTextTimeTutup = findViewById(R.id.txtTutup);
    }

    private void setListeners() {
        back.setOnClickListener(v -> finish());

        pilihFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        });

        editTextDate.setOnClickListener(v -> DatePicker.showDatePicker(this, editTextDate));

        editTextTimeBuka.setOnClickListener(v -> TimePicker.showTimePicker(this, editTextTimeBuka));
        editTextTimeTutup.setOnClickListener(v -> TimePicker.showTimePicker(this, editTextTimeTutup));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            String fileName = getFileName(fileUri);
            pilihFile.setText(fileName);
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }
}
