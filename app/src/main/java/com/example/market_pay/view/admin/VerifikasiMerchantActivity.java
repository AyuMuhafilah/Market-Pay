package com.example.market_pay.view.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.market_pay.R;
import com.example.market_pay.helper.UserHelper;
import com.example.market_pay.model.UserModel;
import com.example.market_pay.utils.ConfirmDialog;
import com.example.market_pay.utils.Toast;
import com.google.firebase.firestore.FirebaseFirestore;

public class VerifikasiMerchantActivity extends AppCompatActivity {

    ImageView back, btnHapus, btnCheck;
    String userId;
    LinearLayout containerCards;
    TextView txtNik, txtNama;
    View cardView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifikasi_merchant);

        initViews();
        initListener();
        tampilData();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.verifikasi_merchant), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        back = findViewById(R.id.iconBack);
    }

    private void initListener() {
        back.setOnClickListener(v -> finish());
    }

    private void tampilData() {
        UserHelper.getMerchant(users -> {
            containerCards = findViewById(R.id.containerCards);
            containerCards.removeAllViews(); // Tetap lakukan clear di awal

            if (users != null && !users.isEmpty()) {
                for (UserModel user : users) {
                    String userId = user.getUser_id();
                    View cardView = LayoutInflater.from(this).inflate(R.layout.listview, containerCards, false);

                    TextView txtNik = cardView.findViewById(R.id.txtNik);
                    TextView txtNama = cardView.findViewById(R.id.txtNama);
                    btnCheck = cardView.findViewById(R.id.btnCheck);
                    btnHapus = cardView.findViewById(R.id.btnHapus);

                    txtNik.setText(user.getNik() != null ? user.getNik() : "-");
                    txtNama.setText(user.getNama_lengkap() != null ? user.getNama_lengkap() : "-");

                    btnCheck.setOnClickListener(v -> {
                        ConfirmDialog.show(this, "Yakin ingin verifikasi merchant ini?", (dialog, which) -> {
                            FirebaseFirestore.getInstance()
                                    .collection("merchants")
                                    .document(userId)
                                    .update("status", true)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.getInstance(this).showToast("Merchant Berhasil diverifikasi ");
                                        runOnUiThread(this::tampilData);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.getInstance(this).showToast("Gagal Verifikasi merchant");
                                    });
                        });
                    });

                    btnHapus.setOnClickListener(v -> {
                        ConfirmDialog.show(this, "Yakin ingin menghapus merchant ini?", (dialog, which) -> {
                            FirebaseFirestore.getInstance()
                                    .collection("merchants")
                                    .document(userId)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.getInstance(this).showToast("Merchant Berhasil dihapus ");
                                        runOnUiThread(this::tampilData);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.getInstance(this).showToast("Gagal menghapus merchant");
                                    });
                        });
                    });

                    containerCards.addView(cardView);
                }

                containerCards.setVisibility(View.VISIBLE);
            } else {
                // Tambahkan ini biar UI jelas kosong
                TextView kosong = new TextView(this);
                kosong.setText("Tidak ada merchant yang perlu diverifikasi.");
                kosong.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                containerCards.addView(kosong);
            }
        });
    }
}
