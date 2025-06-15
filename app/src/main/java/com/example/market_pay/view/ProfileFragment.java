package com.example.market_pay.view;

import static com.example.market_pay.view.customer.CustomerFragment.formatNama;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.market_pay.R;
import com.example.market_pay.utils.ConfirmDialog;
import com.example.market_pay.helper.UserHelper;
import com.example.market_pay.view.customer.DaftarMerchantActivity;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {
    private LinearLayout dataPribadi, daftarMerchant, pengaturan, logout, submenuPengaturan, ubahPin, ubahKataSandi;
    private TextView namaUser;
    private ImageView arrow;
    private ShapeableImageView profile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Aktifkan Dropdown Pengaturan
        arrow = view.findViewById(R.id.arrow);
        pengaturan = view.findViewById(R.id.pengaturan);
        profile = view.findViewById(R.id.profileImage);
        ubahPin = view.findViewById(R.id.ubahPin);
        ubahKataSandi = view.findViewById(R.id.ubahKataSandi);
        submenuPengaturan = view.findViewById(R.id.submenuPengaturan);
        namaUser = view.findViewById(R.id.namaUser);
        daftarMerchant = view.findViewById(R.id.daftarMerchant);
        dataPribadi = view.findViewById(R.id.dataPribadi);

        pengaturan.setOnClickListener(v -> {
            if (submenuPengaturan.getVisibility() == View.GONE) {
                submenuPengaturan.setVisibility(View.VISIBLE);
                arrow.setRotation(90f);
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
                arrow.setRotation(0);
                submenuPengaturan.setVisibility(View.GONE);
            }
        });

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UserHelper.getUserById(userId, user -> {
            if (user != null) {
                String role = user.getRole();
                if (role.equals("customer")) {
                    daftarMerchant.setVisibility(View.VISIBLE);
                }
                namaUser.setText(formatNama(user.getNama_lengkap()));
                String imageUrl = user.getProfile();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.user)
                            .error(R.drawable.user)
                            .into(profile);
                } else {
                Glide.with(this)
                    .load(R.drawable.user)
                    .into(profile);
                }
            }
        });


        // Data Pribadi
        dataPribadi.setOnClickListener(v->{
            startActivity(new Intent(requireContext(), DataPribadiActivity.class));
        });

        // Daftar Merchant
        daftarMerchant.setOnClickListener(v->{
            startActivity(new Intent(requireContext(), DaftarMerchantActivity.class));
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