package com.example.market_pay.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.market_pay.R;
import com.example.market_pay.utils.AppUtils;
import com.example.market_pay.utils.LoadingDialog;
import com.example.market_pay.utils.Toast;
import com.example.market_pay.model.UserModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SigninActivity extends AppCompatActivity {
    private TextInputEditText txtNamaLengkap, txtNoHp, txtEmail, txtPassword;
    private TextView txtLogin;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin);

        initViews();
        initListeners();
        loadingDialog = new LoadingDialog(this);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signin), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        txtNamaLengkap = findViewById(R.id.txtNamaLengkap);
        txtNoHp        = findViewById(R.id.txtNoHp);
        txtEmail       = findViewById(R.id.txtEmail);
        txtPassword    = findViewById(R.id.txtPassword);
        txtLogin       = findViewById(R.id.txtLogin);
        btnRegister    = findViewById(R.id.btnRegister);
        mAuth          = FirebaseAuth.getInstance();
        firestore      = FirebaseFirestore.getInstance();
    }

    private void initListeners() {
        txtLogin.setOnClickListener(v -> {
            startActivity(new Intent(SigninActivity.this, MainActivity.class));
            finish();
        });
        btnRegister.setOnClickListener(v -> Register());
    }

    private void Register() {
        String namaLengkap = txtNamaLengkap.getText().toString().trim();
        String noHp        = txtNoHp.getText().toString().trim();
        String email       = txtEmail.getText().toString().trim();
        String password    = txtPassword.getText().toString().trim();
        if (namaLengkap.isEmpty() || noHp.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.getInstance(this).showToast("Data Tidak Boleh Ada yang Kosong");
            return;
        }
        if (!AppUtils.validateNoHp(this, noHp)) {
            return;
        }
        if (!AppUtils.validateEmail(this, email)) {
            return;
        }
        if (!AppUtils.validatePassword(this, password)) {
            return;
        }
        loadingDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String userId = mAuth.getCurrentUser().getUid();
                UserModel userModel = new UserModel(userId, email, namaLengkap, noHp, "customer", "",0,"","","","","","","");
                firestore.collection("users")
                .document(userId)
                .set(userModel)
                .addOnSuccessListener(unused -> {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(loginTask -> {
                                if (loginTask.isSuccessful()) {
                                    FirebaseUser userLoggedIn = mAuth.getCurrentUser();
                                    if (userLoggedIn != null) {
                                        String userIdLoggedIn = userLoggedIn.getUid();
                                        FirebaseFirestore.getInstance().collection("users")
                                                .document(userIdLoggedIn)
                                                .get()
                                                .addOnSuccessListener(documentSnapshot -> {
                                                    loadingDialog.dismiss();
                                                    if (documentSnapshot.exists()) {
                                                        String namaLengkapLoggedIn = documentSnapshot.getString("nama_lengkap");
                                                        Toast.getInstance(SigninActivity.this).showToast("Selamat Datang " + namaLengkapLoggedIn);
                                                        Intent intent = new Intent(SigninActivity.this, HomeActivity.class);
                                                        intent.putExtra("userId", userId);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                    }
                                } else {
                                    Toast.getInstance(SigninActivity.this).showToast("Login gagal setelah registrasi: " + loginTask.getException().getMessage());
                                    loadingDialog.dismiss();
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.getInstance(this).showToast("Gagal menyimpan data user");
                    loadingDialog.dismiss();
                });
            } else {
                Toast.getInstance(this).showToast("Gagal registrasi: " + task.getException().getMessage());
                loadingDialog.dismiss();
            }
        });
    }


}
