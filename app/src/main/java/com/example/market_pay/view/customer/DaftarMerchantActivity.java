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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_merchant);

        // Back
        ImageView back = findViewById(R.id.iconBack);
        back.setOnClickListener(v->{
            finish();
        });

        // Pilih File
        pilihFile = findViewById(R.id.pilihFile);
        pilihFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*"); // atau jenis file tertentu, contoh "image/*"
            startActivityForResult(intent, 100);  // 100 adalah requestCode bebas
        });



        new WilayahHelper(this);
        editTextDate = findViewById(R.id.editTextDate);
        editTextDate.setOnClickListener(v -> DatePicker.showDatePicker(this, editTextDate));

        editTextTimeBuka = findViewById(R.id.editTextTimeBuka);
        editTextTimeBuka.setOnClickListener(v -> TimePicker.showTimePicker(this, editTextTimeBuka));
        editTextTimeTutup = findViewById(R.id.editTextTimeTutup);
        editTextTimeTutup.setOnClickListener(v -> TimePicker.showTimePicker(this, editTextTimeTutup));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            String fileName = getFileName(fileUri);  // fungsi untuk mendapatkan nama file
            TextInputEditText etFile = findViewById(R.id.pilihFile);
            etFile.setText(fileName);
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {  // <-- Cek di sini
                        result = cursor.getString(nameIndex);
                    }
                }
            } finally {
                if (cursor != null) cursor.close();
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }


}