package com.example.market_pay.helper;

import android.util.Log;

import com.example.market_pay.model.SewaModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SewaHelper {

    // Fungsi untuk ambil data sewa dengan status true
    public static void getSewaAktif(SewaCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("sewa")
                .whereEqualTo("status", true)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<SewaModel> sewaList = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        SewaModel sewa = doc.toObject(SewaModel.class);
                        if (sewa != null) {
                            sewa.setId(doc.getId());
                            sewaList.add(sewa);
                        }
                    }
                    callback.onSewaListResult(sewaList);
                })
                .addOnFailureListener(e -> {
                    Log.e("SEWA_HELPER", "Gagal ambil data: " + e.getMessage());
                    callback.onSewaListResult(new ArrayList<>()); // biar null-safe
                });
    }

    // Interface callback langsung di file ini
    public interface SewaCallback {
        void onSewaListResult(List<SewaModel> list);
    }
}

