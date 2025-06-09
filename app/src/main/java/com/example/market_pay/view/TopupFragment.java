package com.example.market_pay.view;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.market_pay.R;
import com.example.market_pay.model.TransaksiModel;
import com.example.market_pay.utils.LoadingDialog;
import com.example.market_pay.utils.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class TopupFragment extends DialogFragment {

    public Button btnBatal, btnTopup;
    public TextInputEditText jmlTopup;
    public String valTopup, transId, userId;
    public SimpleDateFormat sdf;
    public LoadingDialog loadingDialog;
    public Timestamp tgl;

    public TopupFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topup, container, false);
        btnBatal = view.findViewById(R.id.btnBatal);
        btnTopup = view.findViewById(R.id.btnTopup);
        jmlTopup = view.findViewById(R.id.jmlTopup);

        loadingDialog = new LoadingDialog(requireContext());
        setRupiahFormat(jmlTopup);
        btnBatal.setOnClickListener(v -> dismiss());
        btnTopup.setOnClickListener(v -> {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String cekPin = documentSnapshot.getString("pin");
                            if (cekPin != null && !cekPin.isEmpty()) {
                                valTopup = jmlTopup.getText().toString().replaceAll("[^\\d]", "");
                                if (valTopup.isEmpty()) {
                                    Toast.getInstance(requireContext()).showToast("Masukan Jumlah TopUp");
                                } else if (Integer.parseInt(valTopup) < 10000) {
                                    Toast.getInstance(requireContext()).showToast("Minimal TopUp Rp. 10.000");
                                } else {
                                    PinFragment pin = PinFragment.newInstance("cek", () -> {
                                        SimpanTransaksi(userId, Integer.parseInt(valTopup));
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

    public void SimpanTransaksi(String userId, Integer jmlTopup) {
        transId = UUID.randomUUID().toString();
        tgl = new Timestamp(new Date());
        TransaksiModel trans = new TransaksiModel(transId, "TopUp", jmlTopup, tgl, userId);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("transaksi").document(transId)
                .set(trans)
                .addOnSuccessListener(aVoid -> {
                    loadingDialog.dismiss();
                    UpdateSaldo(userId);
                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    Toast.getInstance(requireContext()).showToast("TopUp Saldo Gagal");
                });
    }

    public void UpdateSaldo(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId)
        .get()
        .addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Ambil saldo lama
                Long saldoLama = documentSnapshot.getLong("saldo");
                if (saldoLama == null) saldoLama = 0L;
                // Hitung saldo baru
                long saldoBaru = saldoLama + Integer.parseInt(valTopup);
                // Update saldo
                db.collection("users").document(userId)
                .update("saldo", saldoBaru)
                .addOnSuccessListener(aVoid -> {
                    Toast.getInstance(requireContext()).showToast("TopUp Saldo Berhasil");
                    dismiss();
                    requireActivity().recreate();
                })
                .addOnFailureListener(e -> {
                    Toast.getInstance(requireContext()).showToast("TopUp Saldo Gagal");
                });
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
