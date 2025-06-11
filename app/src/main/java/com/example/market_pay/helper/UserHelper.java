package com.example.market_pay.helper;

import android.util.Log;

import com.example.market_pay.model.UserModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class UserHelper {

    // Ambil data user berdasarkan ID
    public static void getUserById(String userId, UserCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserModel user = documentSnapshot.toObject(UserModel.class);
                        callback.onUserResult(user);
                    } else {
                        callback.onUserResult(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("UserHelper", "Gagal ambil user: " + e.getMessage());
                    callback.onUserResult(null);
                });
    }

    // Ambil list user dengan query fleksibel
    public static void queryUsers(Query query, UserListCallback callback) {
        query.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<UserModel> userList = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        UserModel user = doc.toObject(UserModel.class);
                        if (user != null) userList.add(user);
                    }
                    callback.onUserListResult(userList);
                })
                .addOnFailureListener(e -> {
                    Log.e("UserHelper", "Gagal ambil list user: " + e.getMessage());
                    callback.onUserListResult(null);
                });
    }

    // Callback untuk 1 user
    public interface UserCallback {
        void onUserResult(UserModel user);
    }

    // Callback untuk list user
    public interface UserListCallback {
        void onUserListResult(List<UserModel> userList);
    }

    // CARA PEMANGGILAN
//    UserHelper.getUserById("userId123", user -> {
//        if (user != null) {
//
//        } else {
//
//        }
//    });

//    Query query = FirebaseFirestore.getInstance()
//            .collection("users")
//            .whereEqualTo("role", "pedagang")
//            .orderBy("nama_lengkap");
//
//    UserHelper.queryUsers(query, users -> {
//        if (users != null && !users.isEmpty()) {
//
//        } else {
//
//        }
//    });


}
