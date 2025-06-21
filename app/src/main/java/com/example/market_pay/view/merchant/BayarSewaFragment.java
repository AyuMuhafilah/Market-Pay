package com.example.market_pay.view.merchant;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.market_pay.R;
import com.example.market_pay.model.BayarSewaModel;
import com.example.market_pay.model.TransaksiModel;
import com.example.market_pay.utils.FormatRupiah;
import com.example.market_pay.utils.LoadingDialog;
import com.example.market_pay.utils.Toast;
import com.example.market_pay.view.PinFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Date;
import java.util.UUID;

public class BayarSewaFragment extends DialogFragment {

    public static final String ARG_NOMINAL = "nominal_tagihan";
    public static final String ARG_SEWA_ID = "sewa_id";

    public String userId, sewa_id;
    public Button btnBatal, btnBayar;
    public LoadingDialog loadingDialog;
    public TextInputEditText jmlBayar;

    private int nominal;

    public BayarSewaFragment() {}

    public static BayarSewaFragment newInstance(int nominal, String sewaId) {
        BayarSewaFragment fragment = new BayarSewaFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_NOMINAL, nominal);
        args.putString(ARG_SEWA_ID, sewaId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bayar_sewa, container, false);

        btnBatal = view.findViewById(R.id.btnBatal);
        btnBayar = view.findViewById(R.id.btnBayar);
        jmlBayar = view.findViewById(R.id.jmlBayar);

        loadingDialog = new LoadingDialog(requireContext());

        // Ambil nominal dari argument
        if (getArguments() != null) {
            nominal = getArguments().getInt(ARG_NOMINAL, 0);
            sewa_id = getArguments().getString(ARG_SEWA_ID);
            jmlBayar.setText(String.valueOf(nominal));
        }
        btnBatal.setOnClickListener(v -> dismiss());
        btnBayar.setOnClickListener(v -> {
            loadingDialog.show();
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Long saldo = documentSnapshot.getLong("saldo");

                    if (saldo == null || saldo < nominal) {
                        loadingDialog.dismiss();
                        Toast.getInstance(requireContext()).showToast("Saldo tidak mencukupi untuk melakukan pembayaran");
                        return;
                    }

                    String cekPin = documentSnapshot.getString("pin");
                    if (cekPin != null && !cekPin.isEmpty()) {
                        // Saldo cukup dan PIN tersedia -> lanjut ke verifikasi PIN
                        PinFragment pin = PinFragment.newInstance("cek", () -> {
                            SimpanTransaksi(userId, sewa_id, nominal);
                        });
                        pin.show(requireActivity().getSupportFragmentManager(), "PinDialog");
                    } else {
                        loadingDialog.dismiss();
                        Toast.getInstance(requireContext()).showToast("PIN belum terdaftar");
                    }
                } else {
                    loadingDialog.dismiss();
                    Toast.getInstance(requireContext()).showToast("Data pengguna tidak ditemukan");
                }
            })
            .addOnFailureListener(e -> {
                loadingDialog.dismiss();
                Toast.getInstance(requireContext()).showToast("Gagal mengambil data pengguna");
                Log.e("FirestoreError", "Gagal mengambil data pengguna", e);
            });
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }
    }

    public interface OnPinVerifiedListener {
        void onPinVerified();
    }

    private void SimpanTransaksi(String userId, String sewaId, Integer nominal) {
        loadingDialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Auto generate ID bayar dan simpan ke koleksi bayar_sewa
        String idBayar = db.collection("bayar_sewa").document().getId();
        BayarSewaModel bayar = new BayarSewaModel(userId, sewaId, nominal, new Timestamp(new Date()));

        db.collection("bayar_sewa")
                .document(idBayar)
                .set(bayar)
                .addOnSuccessListener(aVoid -> {
                    // Update saldo user
                    DocumentReference userRef = db.collection("users").document(userId);

                    db.runTransaction(transaction -> {
                        DocumentSnapshot snapshot = transaction.get(userRef);
                        Long currentSaldo = snapshot.getLong("saldo");

                        if (currentSaldo == null || currentSaldo < nominal) {
                            throw new FirebaseFirestoreException("Saldo tidak mencukupi", FirebaseFirestoreException.Code.ABORTED);
                        }
                        // Kurangi saldo
                        transaction.update(userRef, "saldo", currentSaldo - nominal);
                        // Simpan riwayat transaksi
                        String idTransaksi = db.collection("transaksi").document().getId();
                        TransaksiModel transaksi = new TransaksiModel(
                                idTransaksi,
                                "Payment",
                                nominal,
                                new Timestamp(new Date()),
                                userId
                        );
                        DocumentReference transaksiRef = db.collection("transaksi").document(idTransaksi);
                        transaction.set(transaksiRef, transaksi);

                        return null;
                    }).addOnSuccessListener(unused -> {
                        loadingDialog.dismiss();
                        Toast.getInstance(requireContext()).showToast("Pembayaran berhasil disimpan");
                        dismiss();
                        requireActivity().recreate();
                    }).addOnFailureListener(e -> {
                        loadingDialog.dismiss();
                        Toast.getInstance(requireContext()).showToast("Gagal update saldo/transaksi: " + e.getMessage());
                    });

                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    Toast.getInstance(requireContext()).showToast("Gagal simpan pembayaran: " + e.getMessage());
                });
    }


}

