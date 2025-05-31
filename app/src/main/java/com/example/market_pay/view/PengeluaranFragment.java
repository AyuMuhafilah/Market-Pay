package com.example.market_pay.view;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.market_pay.R;
import com.example.market_pay.utils.Toast;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class PengeluaranFragment extends Fragment {

    private TextView cardText, textNominal;
    private FirebaseFirestore db;
    private LinearLayout containerCards;
    private Drawable drawable;
    private View cardView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pengeluaran, container, false);
        // Inisialisasi Firestore
        db = FirebaseFirestore.getInstance();
        String userIdLogin = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Ambil container layout
        containerCards = view.findViewById(R.id.containerCards);

        // Ambil data dari Firestore dengan filter
        CollectionReference pemasukanRef = db.collection("transaksi");
        pemasukanRef.whereEqualTo("user_id", userIdLogin)
                .whereIn("jenis", Arrays.asList("Send Money", "Payment"))
                .orderBy("tgl", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    containerCards.setVisibility(View.VISIBLE);
                    containerCards.removeAllViews();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String keterangan = doc.getString("jenis");
                        Timestamp tglTimestamp = doc.getTimestamp("tgl");
                        String tglText = "";
                        if (tglTimestamp != null) {
                            Date date = tglTimestamp.toDate();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                            tglText = sdf.format(date);
                        }
                        Double nominal = doc.getDouble("nominal");
                        String nominalTextStr = "0.00";
                        if (nominal != null) {
                            nominalTextStr = String.format("%,.0f", nominal);
                        }
                        cardView = inflater.inflate(R.layout.card_view, containerCards, false);
                        cardText = cardView.findViewById(R.id.cardText);
                        textNominal = cardView.findViewById(R.id.textNominal);
                        // Set drawable
                        if ("Payment".equals(keterangan)) {
                            drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_payment);
                        }else{
                            drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_send);
                        }
                        cardText.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                        // Set Text
                        cardText.setText(keterangan + "\n" + tglText);
                        textNominal.setText("-Rp. " + nominalTextStr);
                        textNominal.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
                        containerCards.addView(cardView);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.getInstance(getContext()).showToast("Error: " + e.getMessage());
                });
        return view;
    }
}

