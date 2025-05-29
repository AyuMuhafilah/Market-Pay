package com.example.market_pay.utils;

import android.util.Log;

import com.example.market_pay.model.UserModel;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserUtils {

    public static void getUserData(String userId, UserDataCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserModel user = documentSnapshot.toObject(UserModel.class);
                        callback.onUserData(user);
                    } else {
                        callback.onUserData(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("User", "Gagal ambil data user: " + e.getMessage());
                    e.printStackTrace();
                    callback.onUserData(null);
                });
    }

    // Interface callback untuk mengembalikan data UserModel
    public interface UserDataCallback {
        void onUserData(UserModel user);
    }
}
