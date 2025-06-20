package com.example.market_pay.view.admin;

import android.content.Intent;
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
import com.example.market_pay.view.merchant.DataMerchantActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class KelolaMerchantActivity extends AppCompatActivity {

    ImageView back, btnCheck, btnHapus;
    LinearLayout containerCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelola_merchant);

        initViews();
        initListener();
        tampilData();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
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
        UserHelper.getAllMerchant(users -> {
            containerCards = findViewById(R.id.containerCards);
            containerCards.removeAllViews();
            if (users != null && !users.isEmpty()) {
                for (UserModel user : users) {
                    String userId = user.getUser_id();
                    View cardView = LayoutInflater.from(this).inflate(R.layout.listview, containerCards, false);

                    TextView txtNik = cardView.findViewById(R.id.txtNik);
                    TextView txtNama = cardView.findViewById(R.id.txtNama);
                    btnCheck = cardView.findViewById(R.id.btnCheck);
                    btnHapus = cardView.findViewById(R.id.btnHapus);
                    btnCheck.setImageResource(R.drawable.ic_edit);
                    btnCheck.setOnClickListener(v -> {
                        Intent intent = new Intent(this, DataMerchantActivity.class);
                        intent.putExtra("user_id", userId); // Kirim userId ke activity tujuan
                        startActivity(intent);
                    });

                    btnHapus.setOnClickListener(v -> {
                        ConfirmDialog.show(v.getContext(), "Apakah kamu yakin ingin menonaktifkan merchant ini?", (dialog, which) -> {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("merchants")
                            .whereEqualTo("userId", userId) // berdasarkan userId merchant ini
                            .limit(1)
                            .get()
                            .addOnSuccessListener(querySnapshot -> {
                                if (!querySnapshot.isEmpty()) {
                                    DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                                    String docId = doc.getId();
                                    db.collection("merchants").document(docId)
                                            .update("status", false)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.getInstance(this).showToast("Merchant berhasil di-nonaktifkan");
                                                containerCards.removeView(cardView);
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.getInstance(this).showToast("Gagal Menonaktifkan Merchant");
                                            });
                                } else {
                                    Toast.getInstance(this).showToast("Data Merchant Tidak Ditemukan");
                                }
                            });
                        });
                    });


                    txtNik.setText(user.getNik() != null ? user.getNik() : "-");
                    txtNama.setText(user.getNama_lengkap() != null ? user.getNama_lengkap() : "-");
                    containerCards.addView(cardView);
                }
                containerCards.setVisibility(View.VISIBLE);
            }
        });
    }
}