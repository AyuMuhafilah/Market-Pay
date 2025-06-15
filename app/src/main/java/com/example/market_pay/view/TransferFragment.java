package com.example.market_pay.view;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.market_pay.R;
import com.example.market_pay.model.TransaksiModel;
import com.example.market_pay.utils.FormatRupiah;
import com.example.market_pay.utils.LoadingDialog;
import com.example.market_pay.utils.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class TransferFragment extends DialogFragment {

    public Button btnBatal, btnTransfer;
    public TextInputEditText jmlTransfer, txtNoHp;
    public String valTransfer, transId, userId, tujuanUserId;
    public LoadingDialog loadingDialog;
    public Timestamp tgl;

    public TransferFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transfer, container, false);
        btnBatal = view.findViewById(R.id.btnBatal);
        btnTransfer = view.findViewById(R.id.btnTransfer);
        jmlTransfer = view.findViewById(R.id.jmlTransfer);
        txtNoHp = view.findViewById(R.id.txtNoHp);

        loadingDialog = new LoadingDialog(requireContext());
        FormatRupiah.setRupiahFormat(jmlTransfer);

        btnBatal.setOnClickListener(v -> dismiss());
        btnTransfer.setOnClickListener(v -> {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String noTujuan = txtNoHp.getText().toString().trim();
            valTransfer = jmlTransfer.getText().toString().replaceAll("[^\\d]", "");

            if (noTujuan.isEmpty()) {
                Toast.getInstance(requireContext()).showToast("Nomor HP tujuan tidak boleh kosong");
                return;
            }

            if (valTransfer.isEmpty()) {
                Toast.getInstance(requireContext()).showToast("Masukan Jumlah Transfer");
                return;
            }

            if (Integer.parseInt(valTransfer) < 10000) {
                Toast.getInstance(requireContext()).showToast("Minimal Transfer Rp. 10.000");
                return;
            }

            cekUserTujuan(noTujuan);
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

    private void cekUserTujuan(String noTujuan) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("no_hp", noTujuan)
                .limit(1)
                .get()
                .addOnSuccessListener(query -> {
                    if (!query.isEmpty()) {
                        tujuanUserId = query.getDocuments().get(0).getId();
                        if (tujuanUserId.equals(userId)) {
                            Toast.getInstance(requireContext()).showToast("Tidak bisa transfer ke akun sendiri");
                        } else {
                            PinFragment pin = PinFragment.newInstance("cek", () -> SimpanTransaksi(userId, tujuanUserId, Integer.parseInt(valTransfer)));
                            pin.show(requireActivity().getSupportFragmentManager(), "PinDialog");
                        }
                    } else {
                        Toast.getInstance(requireContext()).showToast("Nomor HP tidak ditemukan");
                    }
                })
                .addOnFailureListener(e -> Toast.getInstance(requireContext()).showToast("Gagal mencari pengguna"));
    }

    public void SimpanTransaksi(String dariId, String keId, int jumlah) {
        loadingDialog.show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.runTransaction(transaction -> {
            DocumentSnapshot dariDoc = transaction.get(db.collection("users").document(dariId));
            DocumentSnapshot keDoc = transaction.get(db.collection("users").document(keId));

            Long saldoPengirim = dariDoc.getLong("saldo");
            Long saldoPenerima = keDoc.getLong("saldo");

            if (saldoPengirim == null) saldoPengirim = 0L;
            if (saldoPenerima == null) saldoPenerima = 0L;

            if (saldoPengirim < jumlah) {
                throw new FirebaseFirestoreException("Saldo tidak cukup",
                        FirebaseFirestoreException.Code.ABORTED);
            }

            // Update saldo
            transaction.update(db.collection("users").document(dariId), "saldo", saldoPengirim - jumlah);
            transaction.update(db.collection("users").document(keId), "saldo", saldoPenerima + jumlah);

            // Simpan transaksi pengirim
            String transId1 = UUID.randomUUID().toString();

            TransaksiModel debit = new TransaksiModel(transId1, "Send Money",jumlah, new Timestamp(new Date()), dariId);
            transaction.set(db.collection("transaksi").document(transId1), debit);

            // Simpan transaksi penerima
            String transId2 = UUID.randomUUID().toString();
            TransaksiModel kredit = new TransaksiModel(transId2, "Receive Money", jumlah, new Timestamp(new Date()), keId);
            transaction.set(db.collection("transaksi").document(transId2), kredit);

            return null;
        }).addOnSuccessListener(aVoid -> {
            loadingDialog.dismiss();
            Toast.getInstance(requireContext()).showToast("Transfer Berhasil");
            dismiss();
            requireActivity().recreate();
        }).addOnFailureListener(e -> {
            loadingDialog.dismiss();
            Toast.getInstance(requireContext()).showToast("Transfer Gagal: " + e.getMessage());
        });
    }

    public interface OnPinVerifiedListener {
        void onPinVerified();
    }
}
