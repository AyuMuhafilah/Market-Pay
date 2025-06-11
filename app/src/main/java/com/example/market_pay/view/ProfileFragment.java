package com.example.market_pay.view;

import static com.example.market_pay.view.customer.CustomerFragment.formatNama;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.market_pay.R;
import com.example.market_pay.model.UserModel;
import com.example.market_pay.utils.ConfirmDialog;
import com.example.market_pay.helper.UserHelper;
import com.example.market_pay.view.customer.DaftarMerchantActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {
    private LinearLayout dataPribadi, daftarMerchant, pengaturan, logout, submenuPengaturan, ubahPin, ubahKataSandi;
    private TextView namaUser;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Aktifkan Dropdown Pengaturan
        pengaturan = view.findViewById(R.id.pengaturan);
        ubahPin = view.findViewById(R.id.ubahPin);
        ubahKataSandi = view.findViewById(R.id.ubahKataSandi);
        submenuPengaturan = view.findViewById(R.id.submenuPengaturan);

        pengaturan.setOnClickListener(v -> {
            if (submenuPengaturan.getVisibility() == View.GONE) {
                submenuPengaturan.setVisibility(View.VISIBLE);
                ubahPin.setOnClickListener(v2 -> {
                    PinFragment pinFragment = PinFragment.newInstance("ubah", () -> {
                    });
                    pinFragment.show(getParentFragmentManager(), "PinFragment");
                });
                ubahKataSandi.setOnClickListener(v2 -> {
                    UbahSandiFragment dialog = new UbahSandiFragment();
                    dialog.show(getParentFragmentManager(), "UbahSandiFragment");
                });
            } else {
                submenuPengaturan.setVisibility(View.GONE);
            }
        });

        namaUser = view.findViewById(R.id.namaUser);
        daftarMerchant = view.findViewById(R.id.daftarMerchant);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UserHelper.getUserById(userId, user -> {
            if (user != null) {
                String role = user.getRole();
                if (role.equals("merchant")){
                    // Hilangkan Daftar Merchant
                    daftarMerchant.setVisibility(view.GONE);
                }
                namaUser.setText(formatNama(user.getNama_lengkap()));
            }
        });

        // Daftar Merchant
        daftarMerchant.setOnClickListener(v->{
            Intent intent = new Intent(requireContext(), DaftarMerchantActivity.class);
            startActivity(intent);
        });


        // Logout
        logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(v -> {
            ConfirmDialog.show(requireContext(), "Yakin ingin logout?", (dialog, which) -> {
                if (requireActivity() instanceof HomeActivity) {
                    ((HomeActivity) requireActivity()).logout();
                }
            });
        });
        return view;
    }
}