package com.example.market_pay.view;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.market_pay.R;
import com.example.market_pay.utils.SecurityUtils;
import com.example.market_pay.utils.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class PinFragment extends DialogFragment {

    public static final String ARG_AKSI = "aksi";

    private ImageView btnClose;
    private EditText[] pins;
    private EditText pin1, pin2, pin3, pin4;
    private String aksi;
    private static TopupFragment.OnPinVerifiedListener listener;

    public PinFragment() {
        // Required empty public constructor
    }

    public static PinFragment newInstance(String mode, TopupFragment.OnPinVerifiedListener verifiedListener) {
        PinFragment fragment = new PinFragment();
        listener = verifiedListener;
        Bundle args = new Bundle();
        args.putString(ARG_AKSI, mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pin, container, false);

        aksi = getArguments() != null ? getArguments().getString(ARG_AKSI, "cek") : "cek";

        btnClose = view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> dismiss());

        pin1 = view.findViewById(R.id.pin1);
        pin2 = view.findViewById(R.id.pin2);
        pin3 = view.findViewById(R.id.pin3);
        pin4 = view.findViewById(R.id.pin4);

        pins = new EditText[]{pin1, pin2, pin3, pin4};
        for (int i = 0; i < pins.length; i++) {
            final int currentIndex = i;

            pins[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && currentIndex < pins.length - 1) {
                        pins[currentIndex + 1].requestFocus();
                    }
                }
                @Override
                public void afterTextChanged(Editable s) {
                    boolean allFilled = true;
                    for (EditText pin : pins) {
                        if (pin.getText().toString().isEmpty()) {
                            allFilled = false;
                            break;
                        }
                    }
                    if (allFilled) {
                        checkPin();
                    }
                }
            });
            pins[i].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (pins[currentIndex].getText().toString().isEmpty() && currentIndex > 0) {
                        pins[currentIndex - 1].requestFocus();
                        pins[currentIndex - 1].setSelection(pins[currentIndex - 1].getText().length());
                        return true;
                    }
                }
                return false;
            });
        }
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
            pin1.requestFocus();
        }
    }
    private void checkPin() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StringBuilder pinCode = new StringBuilder();

        for (EditText pin : pins) {
            pinCode.append(pin.getText().toString());
        }

        simpan(pinCode.toString(), aksi, userId);
    }
    private void simpan(String pin, String aksi, String userId) {
        String hashedPin = SecurityUtils.sha256(pin);
        if ("cek".equals(aksi)) {
            cekPinFirestore(userId, hashedPin);
        } else if ("ubah".equals(aksi)) {
            simpanKeFirestore(userId, hashedPin);
        } else {
            dismiss();
        }
    }

    private void simpanKeFirestore(String userId, String hashedPin) {
        FirebaseFirestore.getInstance().collection("users").document(userId)
        .update("pin", hashedPin)
        .addOnSuccessListener(aVoid -> {
            Toast.getInstance(requireContext()).showToast("Pin Berhasil Disimpan");
            dismiss();
        })
        .addOnFailureListener(e -> {
            Toast.getInstance(requireContext()).showToast("Pin Gagal Disimpan");
            dismiss();
        });
    }

    private void cekPinFirestore(String userId, String hashedPinInput) {
        FirebaseFirestore.getInstance().collection("users").document(userId)
        .get()
        .addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String savedHash = documentSnapshot.getString("pin");
                if (hashedPinInput.equals(savedHash)) {
                    if (listener != null) listener.onPinVerified();
                    dismiss();
                } else {
                    Toast.getInstance(requireContext()).showToast("PIN Tidak Sesuai!!!");
                    pin1.setText("");
                    pin2.setText("");
                    pin3.setText("");
                    pin4.setText("");
                    pin1.requestFocus();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clear listener supaya gak memory leak
        listener = null;
    }
}

