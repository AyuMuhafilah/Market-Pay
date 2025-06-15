package com.example.market_pay.view.merchant;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.example.market_pay.R;

public class MerchantFragment extends Fragment {

    private LinearLayout dataMerchant;
    private View view;
    public MerchantFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_merchant, container, false);
        initViews();
        initListener();
        return view;
    }

    private void initViews() {
        dataMerchant = view.findViewById(R.id.dataMerchant);
    }

    private void initListener() {
        dataMerchant.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), DataMerchantActivity.class));
        });
    }
}