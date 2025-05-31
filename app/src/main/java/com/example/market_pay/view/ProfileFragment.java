package com.example.market_pay.view;

import static com.example.market_pay.view.customer.CustomerFragment.formatNama;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.market_pay.R;
import com.example.market_pay.model.UserModel;
import com.example.market_pay.utils.ConfirmDialog;
import com.example.market_pay.utils.UserUtils;
import com.example.market_pay.view.customer.DaftarMerchantActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {
    private LinearLayout dataPribadi, daftarMerchant, Pengaturan, logout;
    private TextView namaUser;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Daftar Merchant
        daftarMerchant = view.findViewById(R.id.daftarMerchant);
        daftarMerchant.setOnClickListener(v->{
            Intent intent = new Intent(requireContext(), DaftarMerchantActivity.class);
            startActivity(intent);
        });

        namaUser = view.findViewById(R.id.namaUser);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UserUtils.getUserData(userId, new UserUtils.UserDataCallback() {
            @Override
            public void onUserData(UserModel user) {
                namaUser.setText(formatNama(user.getNama_lengkap()));
            }
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