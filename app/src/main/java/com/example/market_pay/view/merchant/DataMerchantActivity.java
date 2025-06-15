package com.example.market_pay.view.merchant;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.market_pay.R;
import com.example.market_pay.helper.CloudinaryHelper;
import com.example.market_pay.helper.MerchantHelper;
import com.example.market_pay.utils.DatePicker;
import com.example.market_pay.utils.LoadingDialog;
import com.example.market_pay.utils.TimePicker;
import com.example.market_pay.utils.Toast;
import com.example.market_pay.view.DataPribadiActivity;
import com.example.market_pay.view.customer.DaftarMerchantActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DataMerchantActivity extends AppCompatActivity {

    private Uri gambarUri = null;
    private TextView usaha;
    private String strGambar;
    private Button btnUpdate;
    private ImageView back, gambar;
    private View includedView;
    private LoadingDialog loadingDialog;
    private FirebaseFirestore db;
    private TextInputEditText txtUsaha, txtDeskUsaha, txtBuka, txtTutup, txtGambar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_merchant);

        initViews();
        initListener();
        tampilData();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.data_merchant), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        back = findViewById(R.id.iconBack);
        txtUsaha = findViewById(R.id.txtNamaUsaha);
        txtDeskUsaha = findViewById(R.id.deskUsaha);
        txtBuka = findViewById(R.id.txtBuka);
        txtTutup = findViewById(R.id.txtTutup);
        txtGambar = findViewById(R.id.pilihFile);
        loadingDialog = new LoadingDialog(this);
        btnUpdate = findViewById(R.id.btnUpdateMerchant);

        includedView = findViewById(R.id.itemMerchantView);
        gambar = includedView.findViewById(R.id.image_food);
        usaha = includedView.findViewById(R.id.text_food_name);
    }

    private void initListener() {
        back.setOnClickListener(v -> finish());
        btnUpdate.setOnClickListener(v -> updateData());
        txtGambar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        });
        txtBuka.setOnClickListener(v -> TimePicker.showTimePicker(this, txtBuka));
        txtTutup.setOnClickListener(v -> TimePicker.showTimePicker(this, txtTutup));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            gambarUri = data.getData();
            String fileName = getFileName(gambarUri);
            txtGambar.setText(fileName);
        }
    }

    private void tampilData(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        MerchantHelper.getMerchantByUserId(userId, merchant -> {
            if (merchant != null) {
                txtUsaha.setText(merchant.getUsaha());
                txtDeskUsaha.setText(merchant.getDeskripsi());
                txtBuka.setText(merchant.getBuka());
                txtTutup.setText(merchant.getTutup());
                strGambar = merchant.getImage();
                usaha.setText(merchant.getUsaha());
                Glide.with(this)
                        .load(strGambar)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .into(gambar);
            }else{
                Log.d("CEK MERCHANT", "Data merchant NULL");
            }
        });
    }

    private void updateData() {
        loadingDialog.show();

        if (txtUsaha.getText().toString().trim().isEmpty() || txtDeskUsaha.getText().toString().trim().isEmpty() ||
                txtBuka.getText().toString().trim().isEmpty() || txtTutup.getText().toString().trim().isEmpty()) {
            Toast.getInstance(this).showToast("Data Tidak Boleh Ada yang Kosong");
            loadingDialog.dismiss();
            return;
        }

        CloudinaryHelper.uploadImage(this, gambarUri, new CloudinaryHelper.OnUploadCompleteListener() {
            @Override
            public void onSuccess(String imageUrl) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference editMerchant = db.collection("merchants").document(userId);
                Map<String, Object> updates = new HashMap<>();
                updates.put("usaha", txtUsaha.getText().toString().trim());
                updates.put("deskripsi", txtDeskUsaha.getText().toString());
                updates.put("buka", txtBuka.getText().toString().trim());
                updates.put("tutup", txtTutup.getText().toString().trim());
                if (imageUrl != null) {
                    updates.put("image", imageUrl);
                }
                editMerchant.update(updates)
                .addOnSuccessListener(aVoid -> {
                    loadingDialog.dismiss();
                    Toast.getInstance(DataMerchantActivity.this).showToast("Data Berhasil Diupdate");
                    finish();
                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    Toast.getInstance(DataMerchantActivity.this).showToast("Data Gagal Diupdate");
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                loadingDialog.dismiss();
                Toast.getInstance(DataMerchantActivity.this).showToast(errorMessage);
            }
        });
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