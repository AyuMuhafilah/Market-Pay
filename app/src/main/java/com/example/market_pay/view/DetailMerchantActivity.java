package com.example.market_pay.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.market_pay.R;
import com.example.market_pay.helper.MerchantHelper;
import com.example.market_pay.model.MerchantModel;
import com.example.market_pay.utils.Toast;
import com.example.market_pay.view.customer.BayarFragment;


public class DetailMerchantActivity extends AppCompatActivity {

    private ImageView vGambar, btnBack;
    private View includedView;
    private String usaha, deskripsi, buka, tutup, gambar, userIdMerchant;
    private TextView txtDesk, txtBuka, vUsaha;
    private Button btnBatal, btnBayar;
    private static final String TAG = "DetailMerchantActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_merchant);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        });

        txtDesk = findViewById(R.id.txtDeskripsi);
        txtBuka = findViewById(R.id.txtBuka);

        includedView = findViewById(R.id.itemMerchantView);
        vGambar = includedView.findViewById(R.id.image_food);
        vUsaha = includedView.findViewById(R.id.text_food_name);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        btnBatal = findViewById(R.id.btnBatal);
        btnBatal.setOnClickListener(v -> finish());

        btnBayar = findViewById(R.id.btnBayar);

        String userId = getIntent().getStringExtra("userId");
        ambilDataMerchant(userId);
    }

    private void ambilDataMerchant(String userId) {
        MerchantHelper.getMerchantByUserId(userId, new MerchantHelper.MerchantCallback() {
            @Override
            public void onMerchantResult(MerchantModel merchant) {
                if (merchant != null) {
                    usaha = merchant.getUsaha();
                    deskripsi = merchant.getDeskripsi();
                    buka = merchant.getBuka();
                    tutup = merchant.getTutup();
                    gambar = merchant.getImage();
                    userIdMerchant = merchant.getUserId();

                    vUsaha.setText(usaha);
                    txtDesk.setText(deskripsi);
                    txtBuka.setText("  " + buka + " - " + tutup);

                    Glide.with(DetailMerchantActivity.this)
                            .load(gambar)
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.error_image)
                            .into(vGambar);

                    btnBayar.setOnClickListener(v -> {
                        BayarFragment dialog = new BayarFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("userIdMerchant", userIdMerchant);
                        dialog.setArguments(bundle);
                        dialog.show(getSupportFragmentManager(), "BayarDialog");
                    });
                } else {
                    Toast.getInstance(DetailMerchantActivity.this).showToast("Merchant tidak ditemukan");
                }
            }
        });
    }
}
