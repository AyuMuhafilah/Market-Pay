package com.example.market_pay.helper;

import android.util.Log;

import com.example.market_pay.model.MerchantModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class MerchantHelper {

    // Ambil data merchant berdasarkan userId
    public static void getMerchantByUserId(String userId, MerchantCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("merchants")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        MerchantModel merchant = doc.toObject(MerchantModel.class);
                        callback.onMerchantResult(merchant);
                    } else {
                        callback.onMerchantResult(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MerchantHelper", "Gagal ambil merchant: " + e.getMessage());
                    callback.onMerchantResult(null);
                });
    }

    // Ambil list merchant dengan query fleksibel
    public static void queryMerchants(Query query, MerchantListCallback callback) {
        query.get()
        .addOnSuccessListener(querySnapshot -> {
            List<MerchantModel> merchantList = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                MerchantModel merchant = doc.toObject(MerchantModel.class);
                if (merchant != null) merchantList.add(merchant);
            }
            callback.onMerchantListResult(merchantList);
        })
        .addOnFailureListener(e -> {
            Log.e("MerchantHelper", "Gagal ambil list merchant: " + e.getMessage());
            callback.onMerchantListResult(null);
        });
    }

    // Callback untuk 1 merchant
    public interface MerchantCallback {
        void onMerchantResult(MerchantModel merchant);
    }

    // Callback untuk list merchant
    public interface MerchantListCallback {
        void onMerchantListResult(List<MerchantModel> merchantList);
    }
}
