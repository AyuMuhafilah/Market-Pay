package com.example.market_pay.view;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.market_pay.R;
import com.example.market_pay.helper.CloudinaryHelper;
import com.example.market_pay.helper.UserHelper;
import com.example.market_pay.utils.AppUtils;
import com.example.market_pay.utils.DatePicker;
import com.example.market_pay.utils.LoadingDialog;
import com.example.market_pay.utils.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DataPribadiActivity extends AppCompatActivity {

    private ImageView back;
    private TextInputEditText txtNik, txtNama, txtNoHp, txtEmail, txtTmpLahir, txtDesa,
            txtTglLahir, txtRtRw, txtDetAlamat, txtUsaha, txtDeskUsaha, txtBuka, txtTutup, txtGambar;
    private RadioGroup txtJk;
    private RadioButton jkp, jkl;
    private Button btnUpdate;
    private Uri gambarUri = null;
    private LoadingDialog loadingDialog;
    private String nik, nama, noHp, email, tmpLahir, tglLahir,jk, desa, rtRw, detAlamat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_pribadi);

        initViews();
        setListeners();
        tampilData();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        back = findViewById(R.id.iconBack);
        txtBuka = findViewById(R.id.txtBuka);
        txtTutup = findViewById(R.id.txtTutup);
        btnUpdate = findViewById(R.id.btnUpdate);
        txtNik = findViewById(R.id.txtNik);
        txtNama = findViewById(R.id.txtNamaLengkap);
        txtNoHp = findViewById(R.id.txtNoHp);
        txtEmail = findViewById(R.id.txtEmail);
        txtTmpLahir = findViewById(R.id.txtTmpLahir);
        txtTglLahir = findViewById(R.id.txtTglLahir);
        txtJk = findViewById(R.id.pilihJk);
        jkp = findViewById(R.id.P);
        jkl = findViewById(R.id.L);
        txtDesa = findViewById(R.id.txtDesa);
        txtRtRw = findViewById(R.id.txtRtRw);
        txtDetAlamat = findViewById(R.id.txtDetAlamat);
        txtGambar = findViewById(R.id.pilihFile);
        loadingDialog = new LoadingDialog(this);
    }

    private void setListeners() {
        back.setOnClickListener(v -> finish());
        txtGambar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        });
        txtTglLahir.setOnClickListener(v -> DatePicker.showDatePicker(this, txtTglLahir));
        btnUpdate.setOnClickListener(v->simpanData());
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

    private void tampilData(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UserHelper.getUserById(userId, user -> {
            if (user != null) {
                txtNik.setText(user.getNik());
                txtNama.setText(user.getNama_lengkap());
                txtNoHp.setText(user.getNo_hp());
                txtEmail.setText(user.getEmail());
                txtTmpLahir.setText(user.getTmp_lahir());
                txtTglLahir.setText(user.getTgl_lahir());
                String jk = user.getJk();
                if (jk != null) {
                    if (jk.equalsIgnoreCase("Laki - laki")) {
                        jkl.setChecked(true);
                    } else if (jk.equalsIgnoreCase("Perempuan")) {
                        jkp.setChecked(true);
                    }
                }
                txtDesa.setText(user.getDesa());
                txtRtRw.setText(user.getRt());
                txtDetAlamat.setText(user.getDet_alamat());
            } else {
                Toast.getInstance(DataPribadiActivity.this).showToast("User Tidak Ditemukan");
            }
        });
    }

    private void simpanData() {
        nik = txtNik.getText().toString().trim();
        nama = txtNama.getText().toString().trim();
        noHp = txtNoHp.getText().toString().trim();
        email = txtEmail.getText().toString().trim();
        tmpLahir = txtTmpLahir.getText().toString().trim();
        tglLahir = txtTglLahir.getText().toString().trim();
        jk = (txtJk.getCheckedRadioButtonId() != -1) ?
                ((RadioButton) findViewById(txtJk.getCheckedRadioButtonId())).getText().toString().trim() : "";
        desa = txtDesa.getText().toString().trim();
        rtRw = txtRtRw.getText().toString().trim();
        detAlamat = txtDetAlamat.getText().toString().trim();

        if (nik.isEmpty() || nama.isEmpty() || noHp.isEmpty() || email.isEmpty() ||
                tmpLahir.isEmpty() || tglLahir.isEmpty() || jk.isEmpty() || desa.isEmpty() ||
                rtRw.isEmpty() || detAlamat.isEmpty()) {
            Toast.getInstance(this).showToast("Data Tidak Boleh Ada yang Kosong");
            return;
        }
        if (!AppUtils.validateNik(this, nik)) return;
        if (!AppUtils.validateNoHp(this, noHp)) return;
        if (!AppUtils.validateEmail(this, email)) return;

        loadingDialog.show();
        if (gambarUri != null) {
            // Jika ada gambar, upload ke Cloudinary terlebih dahulu
            CloudinaryHelper.uploadImage(this, gambarUri, new CloudinaryHelper.OnUploadCompleteListener() {
                @Override
                public void onSuccess(String imageUrl) {
                    updateUserData(imageUrl);
                }
                @Override
                public void onFailure(String errorMessage) {
                    loadingDialog.dismiss();
                    Toast.getInstance(DataPribadiActivity.this).showToast(errorMessage);
                }
            });
        } else {
            // Jika tidak ada gambar, langsung update tanpa field profile
            updateUserData(null);
        }
    }

    private void updateUserData(String imageUrl) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String jk = "";
        if (jkl.isChecked()) {
            jk = "Laki - laki";
        } else if (jkp.isChecked()) {
            jk = "Perempuan";
        }
        DocumentReference userRef = db.collection("users").document(userId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("nik", nik);
        updates.put("nama_lengkap", nama);
        updates.put("no_hp", noHp);
        updates.put("email", email);
        updates.put("tmp_lahir", tmpLahir);
        updates.put("tgl_lahir", tglLahir);
        updates.put("jk", jk);
        updates.put("desa", desa);
        updates.put("rt", rtRw);
        updates.put("det_alamat", detAlamat);
        if (imageUrl != null) {
            updates.put("profile", imageUrl);
        }
        userRef.update(updates)
        .addOnSuccessListener(aVoid -> {
            loadingDialog.dismiss();
            Toast.getInstance(DataPribadiActivity.this).showToast("Data Berhasil Diupdate");
            finish();
        })
        .addOnFailureListener(e -> {
            loadingDialog.dismiss();
            Toast.getInstance(DataPribadiActivity.this).showToast("Data Gagal Diupdate");
        });
    }

}