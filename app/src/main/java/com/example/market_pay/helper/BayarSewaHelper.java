package com.example.market_pay.helper;

import com.example.market_pay.model.BayarSewaModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BayarSewaHelper {

    public interface BayarSewaCallback {
        void onResult(List<BayarSewaModel> list);
    }

    public static void getBayarSewaByUserAndMonth(String userId, String bulan, BayarSewaCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("bayar_sewa")
                .whereEqualTo("user_id", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<BayarSewaModel> result = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        BayarSewaModel data = doc.toObject(BayarSewaModel.class);
                        if (data != null && data.getTgl_bayar() != null) {
                            Date tanggal = data.getTgl_bayar().toDate();
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(tanggal);

                            // Ambil bulan dalam format 2 digit
                            String month = new SimpleDateFormat("MM", Locale.getDefault()).format(cal.getTime());

                            if (bulan.equals(month)) {
                                result.add(data);
                            }
                        }
                    }
                    callback.onResult(result);
                })
                .addOnFailureListener(e -> {
                    callback.onResult(null);
                });
    }
}

