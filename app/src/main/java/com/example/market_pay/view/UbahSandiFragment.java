package com.example.market_pay.view;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.market_pay.R;
import com.example.market_pay.utils.AppUtils;
import com.example.market_pay.utils.LoadingDialog;
import com.example.market_pay.utils.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UbahSandiFragment extends DialogFragment {

    private Button btnBatal, btnSimpan;
    private String pL, pB, kP;
    private FirebaseUser user;
    private LoadingDialog loadingDialog;
    private TextInputEditText passwordBaru, passwordLama, konfirmasiPassword;

    public UbahSandiFragment() {
        // Required empty public constructor
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ubah_sandi, container, false);

        btnBatal = view.findViewById(R.id.btnBatal);
        btnSimpan = view.findViewById(R.id.btnSimpan);
        passwordLama = view.findViewById(R.id.passwordLama);
        passwordBaru = view.findViewById(R.id.passwordBaru);
        loadingDialog = new LoadingDialog(requireContext());
        konfirmasiPassword = view.findViewById(R.id.konfirmasiPassword);

        btnBatal.setOnClickListener(v -> dismiss());
        btnSimpan.setOnClickListener(v -> {
            loadingDialog.show();
            ubah_password();
        });
        return view;
    }

    private void ubah_password() {
        pL = passwordLama.getText().toString().trim();
        pB = passwordBaru.getText().toString().trim();
        kP = konfirmasiPassword.getText().toString().trim();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (!AppUtils.validatePassword(requireContext(), pB)) {
            loadingDialog.dismiss();
            return;
        }else if (!pB.equals(kP)) {
            loadingDialog.dismiss();
            Toast.getInstance(requireContext()).showToast("Konfirmasi password baru tidak cocok");
            return;
        }
        if (user != null && user.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider
                    .getCredential(user.getEmail(), pL);

            // Re-authenticate dulu
            user.reauthenticate(credential)
            .addOnSuccessListener(aVoid -> {
                // Jika berhasil re-auth, ganti password
                user.updatePassword(pB)
                        .addOnSuccessListener(unused -> {
                            Toast.getInstance(requireContext()).showToast("Password berhasil diubah");
                            loadingDialog.dismiss();
                            dismiss();
                        })
                        .addOnFailureListener(e -> {
                            loadingDialog.dismiss();
                            Toast.getInstance(requireContext()).showToast("Gagal ubah password: " + e.getMessage());
                        });
            })
            .addOnFailureListener(e -> {
                loadingDialog.dismiss();
                Toast.getInstance(requireContext()).showToast("Password lama salah!!!");
            });
        }
    }
}
