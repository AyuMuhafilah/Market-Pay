package com.example.market_pay.view.customer;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.market_pay.R;
import com.example.market_pay.adapter.MerchantAdapter;
import com.example.market_pay.model.UserModel;
import com.example.market_pay.utils.AppUtils;
import com.example.market_pay.utils.ConfirmDialog;
import com.example.market_pay.utils.UserUtils;
import com.example.market_pay.view.HomeActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

public class CustomerFragment extends Fragment {

    public CustomerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_merchant);
        TextView namaUser = view.findViewById(R.id.textUser);
        TextInputEditText jmlSaldo = view.findViewById(R.id.txtSaldo);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UserUtils.getUserData(userId, new UserUtils.UserDataCallback() {
            @Override
            public void onUserData(UserModel user) {
                namaUser.setText(formatNama(user.getNama_lengkap()));
                int saldo = user.getSaldo();
                jmlSaldo.setText(AppUtils.formatRupiah(saldo));
            }
        });
        // Logout
        ImageView btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            ConfirmDialog.show(requireContext(), "Yakin ingin logout?", (dialog, which) -> {
                if (requireActivity() instanceof HomeActivity) {
                    ((HomeActivity) requireActivity()).logout();
                }
            });
        });

        int numberOfColumns = calculateNoOfColumns(getContext(), 180);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), numberOfColumns);
        recyclerView.setLayoutManager(gridLayoutManager);

        List<String> foodList = Arrays.asList("Burger", "Chicken Crispi", "Manisan", "Martabak", "Sate", "Street Food");
        List<Integer> imageList = Arrays.asList(
                R.drawable.img_burger,
                R.drawable.img_chicken_crispy,
                R.drawable.img_manisan,
                R.drawable.img_martabak,
                R.drawable.img_sate,
                R.drawable.img_street_food
        );
        MerchantAdapter adapter = new MerchantAdapter(requireContext(), foodList, imageList);
        recyclerView.setAdapter(adapter);
        if (foodList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
        }

        return view;
    }

    private int calculateNoOfColumns(Context context, float columnWidthDp) {
        float screenWidthDp = context.getResources().getDisplayMetrics().widthPixels / context.getResources().getDisplayMetrics().density;
        return Math.max(2, (int) (screenWidthDp / columnWidthDp)); // Minimum 2 columns
    }

    public static String formatNama(String namaLengkap) {
        String[] kata = namaLengkap.split("\\s+");
        if (kata.length < 2) {
            // Kalau cuma 1 kata, tampilkan apa adanya
            return namaLengkap;
        }
        // Dua kata pertama
        String hasil = kata[0] + " " + kata[1] + " ";
        StringBuilder inisial = new StringBuilder();
        for (int i = 2; i < kata.length; i++) {
            inisial.append(Character.toUpperCase(kata[i].charAt(0)));
            if (i < kata.length - 1) {
                inisial.append(".");
            }
        }
        return hasil + inisial.toString();
    }


}
