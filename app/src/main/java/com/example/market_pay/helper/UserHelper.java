package com.example.market_pay.helper;

import com.example.market_pay.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserHelper {
    public interface UserCallback {
        void onUserLoaded(UserModel user);
        void onError(Exception e);
    }

    public static void getCurrentUser(UserCallback callback) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(firebaseUser.getUid())
                    .get()
                    .addOnSuccessListener(doc -> {
                        UserModel user = doc.toObject(UserModel.class);
                        if (user != null) {
                            callback.onUserLoaded(user);
                        }
                    })
                    .addOnFailureListener(callback::onError);
        }
    }
}

