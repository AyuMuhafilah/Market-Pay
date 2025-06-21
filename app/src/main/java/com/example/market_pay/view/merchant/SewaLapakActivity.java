package com.example.market_pay.view.merchant;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.market_pay.R;
import com.example.market_pay.helper.BayarSewaHelper;
import com.example.market_pay.helper.SewaHelper;
import com.example.market_pay.helper.UserHelper;
import com.example.market_pay.model.BayarSewaModel;
import com.example.market_pay.model.SewaModel;
import com.example.market_pay.utils.AppUtils;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class SewaLapakActivity extends AppCompatActivity {

    private LinearLayout tglLayout;
    private Integer nominal;
    private ImageView back;
    private MaterialButton btnBayar;
    private String user_id, sewa_id, nama, bulan, blnAngka, tglText;
    private TextView namaMerchant, namaBulan, nominalTagihan, statusBayar, tglBayar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sewa_lapak);

        back = findViewById(R.id.iconBack);
        namaMerchant = findViewById(R.id.namaMerchant);
        namaBulan = findViewById(R.id.namaBulan);
        nominalTagihan = findViewById(R.id.nominalTagihan);
        statusBayar = findViewById(R.id.statusBayar);
        btnBayar = findViewById(R.id.btnBayar);
        tglBayar = findViewById(R.id.tglBayar);
        tglLayout = findViewById(R.id.tglLayout);

        back.setOnClickListener(v -> finish());
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UserHelper.getUserById(user_id, user -> {
            if (user != null) {
                nama = AppUtils.formatNama(user.getNama_lengkap());
                namaMerchant.setText(nama);
                bulan = AppUtils.getNamaBulan();
                blnAngka = AppUtils.getAngkaBulan();
                namaBulan.setText(bulan);
                SewaHelper.getSewaAktif(sewaList -> {
                    if (sewaList != null && !sewaList.isEmpty()) {
                        SewaModel sewa = sewaList.get(0);
                        sewa_id = sewa.getId();
                        nominal = sewa.getNominal();
                        nominalTagihan.setText(AppUtils.formatRupiah(sewa.getNominal()));
                    }
                });
                BayarSewaHelper.getBayarSewaByUserAndMonth(user_id, blnAngka, list -> {
                    if (list != null && !list.isEmpty()) {
                        BayarSewaModel bayar = list.get(0);
                        tglText = AppUtils.formatTanggalJam(bayar.getTgl_bayar());
                        tglBayar.setText(tglText);

                        statusBayar.setText("Sudah Dibayar");
                        statusBayar.setTextColor(ContextCompat.getColor(this, R.color.green));
                        tglLayout.setVisibility(View.VISIBLE);
                    }else{
                        btnBayar.setVisibility(View.VISIBLE);
                        statusBayar.setText("Belum Dibayar");
                        statusBayar.setTextColor(ContextCompat.getColor(this, R.color.red));
                    }
                });

            }
        });
        btnBayar.setOnClickListener(v->{
            BayarSewaFragment tfDialog = BayarSewaFragment.newInstance(nominal, sewa_id);
            tfDialog.show(SewaLapakActivity.this.getSupportFragmentManager(), "BayarSewaDialog");
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}