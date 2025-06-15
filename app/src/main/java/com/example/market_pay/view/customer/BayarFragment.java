package com.example.market_pay.view.customer;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.market_pay.R;
import com.example.market_pay.model.TransaksiModel;
import com.example.market_pay.utils.LoadingDialog;
import com.example.market_pay.utils.Toast;
import com.example.market_pay.view.HomeActivity;
import com.example.market_pay.view.PinFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class BayarFragment extends DialogFragment {

    public Button btnBatal, btnBayar;
    public TextInputEditText jmlBayar;
    public String valBayar, transId, userId, userIdMerchant;
    public SimpleDateFormat sdf;
    public LoadingDialog loadingDialog;
    public Timestamp tgl;

    public BayarFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bayar, container, false);

        userIdMerchant = getArguments().getString("userIdMerchant");
        btnBatal = view.findViewById(R.id.btnBatal);
        btnBayar = view.findViewById(R.id.btnBayar);
        jmlBayar = view.findViewById(R.id.jmlBayar);

        loadingDialog = new LoadingDialog(requireContext());
        setRupiahFormat(jmlBayar);
        btnBatal.setOnClickListener(v -> dismiss());
        btnBayar.setOnClickListener(v -> {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String cekPin = documentSnapshot.getString("pin");
                            if (cekPin != null && !cekPin.isEmpty()) {
                                valBayar = jmlBayar.getText().toString().replaceAll("[^\\d]", "");
                                if (valBayar.isEmpty()) {
                                    Toast.getInstance(requireContext()).showToast("Masukan Nominal Bayar");
                                } else {
                                    PinFragment pin = PinFragment.newInstance("cek", () -> {
                                        SimpanTransaksi(userId, userIdMerchant, Integer.parseInt(valBayar));
                                    });
                                    pin.show(requireActivity().getSupportFragmentManager(), "PinDialog");
                                }
                            } else {
                                Toast.getInstance(requireContext()).showToast("PIN belum terdaftar");
                            }
                        }
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

    public void SimpanTransaksi(String userIdPembeli, String userIdMerchant, Integer jmlBayar) {
        loadingDialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        tgl = new Timestamp(new Date());

        // Cek saldo pembeli terlebih dahulu
        db.collection("users").document(userIdPembeli)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long saldoLama = documentSnapshot.getLong("saldo");
                        if (saldoLama == null) saldoLama = 0L;

                        // Jika saldo tidak cukup, hentikan proses
                        if (saldoLama < jmlBayar) {
                            loadingDialog.dismiss();
                            Toast.getInstance(requireContext()).showToast("Saldo tidak cukup");
                            return;
                        }

                        // Jika saldo cukup, simpan transaksi pembeli
                        String transIdPembeli = UUID.randomUUID().toString();
                        String transIdMerchant = UUID.randomUUID().toString();

                        TransaksiModel transaksiPembeli = new TransaksiModel(
                                transIdPembeli, "Payment", jmlBayar, tgl, userIdPembeli
                        );
                        TransaksiModel transaksiMerchant = new TransaksiModel(
                                transIdMerchant, "Income", jmlBayar, tgl, userIdMerchant
                        );

                        db.collection("transaksi").document(transIdPembeli)
                                .set(transaksiPembeli)
                                .addOnSuccessListener(aVoid -> {
                                    db.collection("transaksi").document(transIdMerchant)
                                            .set(transaksiMerchant)
                                            .addOnSuccessListener(aVoid2 -> {
                                                // 4. Update saldo pembeli & merchant
                                                UpdateSaldo(userIdPembeli, userIdMerchant, jmlBayar);
                                            })
                                            .addOnFailureListener(e -> {
                                                loadingDialog.dismiss();
                                                Toast.getInstance(requireContext()).showToast("Gagal simpan transaksi merchant");
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    loadingDialog.dismiss();
                                    Toast.getInstance(requireContext()).showToast("Gagal simpan transaksi pembeli");
                                });
                    } else {
                        loadingDialog.dismiss();
                        Toast.getInstance(requireContext()).showToast("Data pengguna tidak ditemukan");
                    }
                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    Toast.getInstance(requireContext()).showToast("Gagal mengakses data pengguna");
                });
    }

    public void UpdateSaldo(String userIdPembeli, String userIdMerchant, int jmlBayar) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userIdPembeli)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long saldoLama = documentSnapshot.getLong("saldo");
                        if (saldoLama == null) saldoLama = 0L;

                        long saldoBaru = saldoLama - jmlBayar;

                        db.collection("users").document(userIdPembeli)
                                .update("saldo", saldoBaru)
                                .addOnSuccessListener(aVoid -> {
                                    // Lanjut tambah saldo ke merchant
                                    TambahSaldoMerchant(userIdMerchant, jmlBayar);
                                })
                                .addOnFailureListener(e -> {
                                    loadingDialog.dismiss();
                                    Toast.getInstance(requireContext()).showToast("Gagal update saldo pembeli");
                                });
                    } else {
                        loadingDialog.dismiss();
                        Toast.getInstance(requireContext()).showToast("Pengguna pembeli tidak ditemukan");
                    }
                });
    }

    public void TambahSaldoMerchant(String userIdMerchant, int jmlBayar) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userIdMerchant)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long saldoLama = documentSnapshot.getLong("saldo");
                        if (saldoLama == null) saldoLama = 0L;

                        long saldoBaru = saldoLama + jmlBayar;

                        db.collection("users").document(userIdMerchant)
                                .update("saldo", saldoBaru)
                                .addOnSuccessListener(aVoid -> {
                                    loadingDialog.dismiss();
                                    Toast.getInstance(requireContext()).showToast("Pembayaran berhasil");
                                    startActivity(new Intent(requireContext(), HomeActivity.class));
                                })
                                .addOnFailureListener(e -> {
                                    loadingDialog.dismiss();
                                    Toast.getInstance(requireContext()).showToast("Gagal update saldo merchant");
                                });
                    } else {
                        loadingDialog.dismiss();
                        Toast.getInstance(requireContext()).showToast("Merchant tidak ditemukan");
                    }
                });
    }


    public void setRupiahFormat(TextInputEditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    editText.removeTextChangedListener(this);
                    String cleanString = s.toString().replaceAll("[^\\d]", "");
                    try {
                        long parsed = Long.parseLong(cleanString);
                        String formatted = NumberFormat.getInstance(new Locale("id", "ID")).format(parsed);
                        current = formatted;
                        editText.setText(formatted);
                        int cursorPosition = Math.min(formatted.length(), editText.getText().length());
                        editText.setSelection(cursorPosition);
                    } catch (NumberFormatException e) {
                        current = "";
                        editText.setText("");
                    }
                    editText.addTextChangedListener(this);
                }
            }
        });
    }

    public interface OnPinVerifiedListener {
        void onPinVerified();
    }
}
