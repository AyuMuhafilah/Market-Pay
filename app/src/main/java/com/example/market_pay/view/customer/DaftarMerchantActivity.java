package com.example.market_pay.view.customer;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.market_pay.R;
import com.example.market_pay.helper.CloudinaryHelper;
import com.example.market_pay.helper.MerchantHelper;
import com.example.market_pay.helper.WilayahHelper;
import com.example.market_pay.model.MerchantModel;
import com.example.market_pay.model.UserModel;
import com.example.market_pay.utils.AppUtils;
import com.example.market_pay.utils.DatePicker;
import com.example.market_pay.utils.LoadingDialog;
import com.example.market_pay.utils.TimePicker;
import com.example.market_pay.utils.Toast;
import com.example.market_pay.helper.UserHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

public class DaftarMerchantActivity extends AppCompatActivity {

    private TextInputEditText txtNik, txtNama, txtNoHp, txtEmail, txtTmpLahir,
            txtTglLahir, txtRtRw, txtDetAlamat, txtUsaha, txtDeskUsaha, txtBuka, txtTutup, txtGambar;
    private AutoCompleteTextView txtDesa;
    private RadioGroup txtJk;
    private RadioButton jkp, jkl;
    private Button btnDaftar;
    private ImageView back;
    private Uri gambarUri = null;
    private LoadingDialog loadingDialog;
    private RecyclerView recyclerViewKel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_merchant);

        initViews();
        setListeners();
        tampilData();
        new WilayahHelper(this);

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
        btnDaftar = findViewById(R.id.btnDaftarMerchant);
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
        txtUsaha = findViewById(R.id.txtNamaUsaha);
        txtDeskUsaha = findViewById(R.id.deskUsaha);
        txtBuka = findViewById(R.id.txtBuka);
        txtTutup = findViewById(R.id.txtTutup);
        txtGambar = findViewById(R.id.pilihFile);
        loadingDialog = new LoadingDialog(this);
        recyclerViewKel = findViewById(R.id.recyclerViewKel);
    }

    private void setListeners() {
        back.setOnClickListener(v -> finish());
        txtGambar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        });
        txtTglLahir.setOnClickListener(v -> DatePicker.showDatePicker(this, txtTglLahir));
        txtBuka.setOnClickListener(v -> TimePicker.showTimePicker(this, txtBuka));
        txtTutup.setOnClickListener(v -> TimePicker.showTimePicker(this, txtTutup));
        btnDaftar.setOnClickListener(v -> formValidasi());
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

    private void formValidasi() {
        String nik = txtNik.getText().toString().trim();
        String nama = txtNama.getText().toString().trim();
        String noHp = txtNoHp.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        String tmpLahir = txtTmpLahir.getText().toString().trim();
        String tglLahir = txtTglLahir.getText().toString().trim();
        String jk = (txtJk.getCheckedRadioButtonId() != -1) ?
                ((RadioButton) findViewById(txtJk.getCheckedRadioButtonId())).getText().toString().trim() : "";
        String desa = txtDesa.getText().toString().trim();
        String rtRw = txtRtRw.getText().toString().trim();
        String detAlamat = txtDetAlamat.getText().toString().trim();
        String usaha = txtUsaha.getText().toString().trim();
        String deskUsaha = txtDeskUsaha.getText().toString().trim();
        String buka = txtBuka.getText().toString().trim();
        String tutup = txtTutup.getText().toString().trim();
        String gambar = txtGambar.getText().toString().trim();

        if (nik.isEmpty() || nama.isEmpty() || noHp.isEmpty() || email.isEmpty() ||
                tmpLahir.isEmpty() || tglLahir.isEmpty() || jk.isEmpty() || desa.isEmpty() ||
                rtRw.isEmpty() || detAlamat.isEmpty() || usaha.isEmpty() || deskUsaha.isEmpty() ||
                buka.isEmpty() || tutup.isEmpty() || gambar.isEmpty()) {
            Toast.getInstance(this).showToast("Data Tidak Boleh Ada yang Kosong");
            return;
        }
        if (!AppUtils.validateNik(this, nik)) return;
        if (!AppUtils.validateNoHp(this, noHp)) return;
        if (!AppUtils.validateEmail(this, email)) return;
        if (!AppUtils.validateImage(this, gambarUri)) return;
        simpanData(usaha, deskUsaha, buka, tutup);
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
                Toast.getInstance(DaftarMerchantActivity.this).showToast("User Tidak Ditemukan");
            }
        });
        MerchantHelper.getMerchantByUserId(userId, merchant -> {
            if (merchant != null) {
                txtUsaha.setText(merchant.getUsaha());
                txtDeskUsaha.setText(merchant.getDeskripsi());
                txtBuka.setText(merchant.getBuka());
                txtTutup.setText(merchant.getTutup());
            }
        });
    }

    private void simpanData(String usaha, String deskUsaha, String buka, String tutup) {
        loadingDialog.show();
        CloudinaryHelper.uploadImage(this, gambarUri, new CloudinaryHelper.OnUploadCompleteListener() {
            @Override
            public void onSuccess(String imageUrl) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                simpanDataMerchant(userId, usaha, deskUsaha, buka, tutup, imageUrl);
                updateDataUser(userId);
            }

            @Override
            public void onFailure(String errorMessage) {
                loadingDialog.dismiss();
                Toast.getInstance(DaftarMerchantActivity.this).showToast(errorMessage);
            }
        });
    }

    private void simpanDataMerchant(String userId, String usaha, String deskripsi,
                                       String buka, String tutup, String imageUrl) {
        String merchantId = UUID.randomUUID().toString();
        MerchantModel merchant = new MerchantModel(merchantId, userId, usaha, deskripsi, buka, tutup, imageUrl, false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("merchants").document(userId)
                .set(merchant)
                .addOnSuccessListener(aVoid -> {
                    loadingDialog.dismiss();
                    Toast.getInstance(this).showToast("Data merchant berhasil disimpan, tunggu validasi Admin!!!");
                    finish();
                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    Toast.getInstance(this).showToast("Gagal menyimpan data merchant");
                });
    }

    private void updateDataUser(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String jk = "";
        if (jkl.isChecked()) {
            jk = "Laki - laki";
        } else if (jkp.isChecked()) {
            jk = "Perempuan";
        }
        // Update data user di Firestore
        db.collection("users").document(userId)
            .update(
                "nik", txtNik.getText().toString().trim(),
                "nama_lengkap", txtNama.getText().toString().trim(),
                "no_hp", txtNoHp.getText().toString().trim(),
                "email", txtEmail.getText().toString().trim(),
                "tmp_lahir", txtTmpLahir.getText().toString().trim(),
                "tgl_lahir", txtTglLahir.getText().toString().trim(),
                "jk", jk,
                "desa", txtDesa.getText().toString().trim(),
                "rt", txtRtRw.getText().toString().trim(),
                "det_alamat", txtDetAlamat.getText().toString().trim()
            );
    }

}
