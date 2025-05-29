package com.example.market_pay;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.market_pay.utils.ConfirmDialog;
import com.example.market_pay.view.HomeActivity;
import com.example.market_pay.view.customer.DaftarMerchantActivity;

public class ProfileFragment extends Fragment {
    private LinearLayout dataPribadi, daftarMerchant, Pengaturan, logout;
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