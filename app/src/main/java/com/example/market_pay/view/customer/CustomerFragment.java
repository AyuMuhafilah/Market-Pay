package com.example.market_pay.view.customer;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.market_pay.R;
import com.example.market_pay.adapter.MerchantAdapter;
import com.example.market_pay.utils.AppUtils;
import com.example.market_pay.utils.ConfirmDialog;
import com.example.market_pay.utils.GridUtils;
import com.example.market_pay.utils.Toast;
import com.example.market_pay.helper.UserHelper;
import com.example.market_pay.view.HomeActivity;
import com.example.market_pay.model.MerchantModel;
import com.example.market_pay.view.TopupFragment;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class CustomerFragment extends Fragment {
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private List<MerchantModel> merchantList;
    private TextView topup, transfer;
    private ShapeableImageView profile;

    public CustomerFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer, container, false);

        recyclerView = view.findViewById(R.id.recycler_merchant);
        TextView namaUser = view.findViewById(R.id.textUser);
        TextInputEditText jmlSaldo = view.findViewById(R.id.txtSaldo);
        ImageView btnLogout = view.findViewById(R.id.btnLogout);
        profile = view.findViewById(R.id.profileImage);

        db = FirebaseFirestore.getInstance();
        merchantList = new ArrayList<>();

        int numberOfColumns = calculateNoOfColumns(getContext(), 180);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), numberOfColumns);
        recyclerView.setLayoutManager(gridLayoutManager);
        int spacingInPixels = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new GridUtils(numberOfColumns, spacingInPixels, true));

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UserHelper.getUserById(userId, user -> {
            if (user != null && isAdded()) {
                namaUser.setText(formatNama(user.getNama_lengkap()));
                int saldo = user.getSaldo();
                jmlSaldo.setText(AppUtils.formatRupiah(saldo));
                String imageUrl = user.getProfile();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(requireContext())
                            .load(imageUrl)
                            .placeholder(R.drawable.user)
                            .error(R.drawable.user)
                            .into(profile);
                } else {
                    Glide.with(requireContext())
                            .load(R.drawable.user)
                            .into(profile);
                }
            }
        });

        btnLogout.setOnClickListener(v -> {
            ConfirmDialog.show(requireContext(), "Yakin ingin logout?", (dialog, which) -> {
                if (requireActivity() instanceof HomeActivity) {
                    ((HomeActivity) requireActivity()).logout();
                }
            });
        });
        tampilMerchants();

        // Topup
        TopupFragment dialog = new TopupFragment();
        topup = view.findViewById(R.id.topup);
        topup.setOnClickListener(v -> {
            dialog.show(requireActivity().getSupportFragmentManager(), "TopupDialog");
        });
        return view;
    }

    private int calculateNoOfColumns(Context context, float columnWidthDp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / columnWidthDp);
        return Math.max(2, noOfColumns);
    }

    private void tampilMerchants() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("merchants")
                .whereEqualTo("status", true)
                .whereNotEqualTo("userId", userId)
                .orderBy("userId")
                .orderBy("usaha", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    merchantList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        MerchantModel merchant = doc.toObject(MerchantModel.class);
                        if (merchant != null) {
                            merchantList.add(merchant);
                        }
                    }

                    if (isAdded() && getContext() != null) {
                        MerchantAdapter adapter = new MerchantAdapter(getContext(), merchantList);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setVisibility(merchantList.isEmpty() ? View.GONE : View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    if (isAdded() && getContext() != null) {
                        Toast.getInstance(getContext()).showToast("Gagal Ambil Data: " + e.getMessage());
                    }
                });
    }

    public static String formatNama(String namaLengkap) {
        String[] kata = namaLengkap.split("\\s+");
        if (kata.length < 2) return namaLengkap;
        StringBuilder hasil = new StringBuilder(kata[0] + " " + kata[1] + " ");
        for (int i = 2; i < kata.length; i++) {
            hasil.append(Character.toUpperCase(kata[i].charAt(0)));
            if (i < kata.length - 1) hasil.append(".");
        }
        return hasil.toString();
    }
}