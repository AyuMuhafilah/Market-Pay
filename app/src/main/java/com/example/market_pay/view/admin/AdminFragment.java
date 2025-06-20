package com.example.market_pay.view.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.example.market_pay.R;

public class AdminFragment extends Fragment {

    private LinearLayout verifikasi, kelolaMerchant;

    public AdminFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);
        verifikasi = view.findViewById(R.id.verifikasiMerchant);
        kelolaMerchant = view.findViewById(R.id.kelolaMerchant);
        verifikasi.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), VerifikasiMerchantActivity.class));
        });
        kelolaMerchant.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), KelolaMerchantActivity.class));
        });
        return view;
    }
}