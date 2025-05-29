package com.example.market_pay.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.market_pay.PemasukanFragment;
import com.example.market_pay.PengeluaranFragment;

public class TransaksiAdapter extends FragmentStateAdapter {

    public TransaksiAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new PemasukanFragment();
        } else {
            return new PengeluaranFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // 2 tab: Pemasukan & Pengeluaran
    }
}
