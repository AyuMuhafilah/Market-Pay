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

    // Ambil user merchant yang ID-nya ada di merchant status=false
    public static void getMerchant(UserListCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Ambil semua merchant dengan status == false
        db.collection("merchants")
        .whereEqualTo("status", false)
        .get()
        .addOnSuccessListener(querySnapshot -> {
            List<String> userIds = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                String userId = doc.getString("userId");
                Object statusObj = doc.get("status");
                if (statusObj instanceof Boolean && !(Boolean) statusObj) {
                    if (userId != null) userIds.add(userId);
                }
            }
            if (!userIds.isEmpty()) {
                // Ambil maksimal 25 dulu (karena Firestore limit)
                List<String> limitedUserIds = userIds.subList(0, Math.min(25, userIds.size()));
                // Step 2: Query ke users
                db.collection("users")
                .whereEqualTo("role", "customer")
                .whereIn("user_id", limitedUserIds)
                .get()
                .addOnSuccessListener(userSnapshot -> {
                    List<UserModel> userList = new ArrayList<>();
                    for (DocumentSnapshot userDoc : userSnapshot.getDocuments()) {
                        UserModel user = userDoc.toObject(UserModel.class);
                        if (user != null) userList.add(user);
                    }
                    callback.onUserListResult(userList);
                })
                .addOnFailureListener(e -> {
                    callback.onUserListResult(null);
                });
            } else {
                // Tidak ada pedagang dengan status merchant false
                callback.onUserListResult(new ArrayList<>()); // Biar tetap aman null-safe
            }
        })
        .addOnFailureListener(e -> {
            // Gagal ambil merchant
            callback.onUserListResult(null);
        });
    }

    public static void getAllMerchant(UserListCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("merchants")
                .whereEqualTo("status", true)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<String> userIds = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String userId = doc.getString("userId");
                        if (userId != null) userIds.add(userId);
                    }

                    if (!userIds.isEmpty()) {
                        // Ambil data user berdasarkan userId
                        db.collection("users")
                                .whereIn("user_id", userIds.subList(0, Math.min(25, userIds.size())))
                                .get()
                                .addOnSuccessListener(userSnap -> {
                                    List<UserModel> userList = new ArrayList<>();
                                    for (DocumentSnapshot userDoc : userSnap.getDocuments()) {
                                        UserModel user = userDoc.toObject(UserModel.class);
                                        userList.add(user);
                                    }
                                    callback.onUserListResult(userList);
                                })
                                .addOnFailureListener(e -> callback.onUserListResult(null));
                    } else {
                        callback.onUserListResult(new ArrayList<>());
                    }
                })
                .addOnFailureListener(e -> callback.onUserListResult(null));
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
