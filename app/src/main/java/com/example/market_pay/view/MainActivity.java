package com.example.market_pay.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.market_pay.R;
import com.example.market_pay.utils.LoadingDialog;
import com.example.market_pay.utils.Toast;
import com.example.market_pay.model.UserModel;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private TextView txtEmail, txtPassword, txtCreateAccount, txtForgotPassword;
    private MaterialButton btnWithGoogle;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 123;
    private GoogleApiClient mGoogleApiClient;
    private Button btnLogin;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initViews();
        setupGoogleSignIn();
        initListeners();
        loadingDialog = new LoadingDialog(this);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        txtEmail         = findViewById(R.id.txtEmailLogin);
        txtPassword      = findViewById(R.id.txtPassword);
        txtCreateAccount = findViewById(R.id.txtCreateAccount);
        btnWithGoogle    = findViewById(R.id.btnWithGoogle);
        mAuth            = FirebaseAuth.getInstance();
        btnLogin         = findViewById(R.id.btnLogin);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
    }

    private void initListeners() {
        btnWithGoogle.setOnClickListener(v -> loginAkunGoogle());
        txtCreateAccount.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SigninActivity.class))
        );
        btnLogin.setOnClickListener(v-> loginUser());
        txtForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ForgotPasswordActivity.class));
        });
    }

    private void loginUser() {
        String txEmail    = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        if (txEmail.isEmpty() || password.isEmpty()) {
            Toast.getInstance(this).showToast("Email dan Password tidak boleh kosong");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(txEmail).matches()) {
            Toast.getInstance(this).showToast("Email tidak valid");
            return;
        }
        loadingDialog.show();
        mAuth.signInWithEmailAndPassword(txEmail, password)
        .addOnCompleteListener(this, task -> {
            loadingDialog.dismiss();
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String userId = user.getUid();
                    FirebaseFirestore.getInstance().collection("users")
                        .document(userId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            String nama = documentSnapshot.getString("nama_lengkap");
                            if (documentSnapshot.exists()) {
                                Toast.getInstance(this).showToast("Selamat Datang " + nama);
                                Intent intent = new Intent(this, HomeActivity.class);
                                intent.putExtra("userId", userId);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(this, SigninActivity.class);
                                intent.putExtra("userId", userId);
                                startActivity(intent);
                            }
                        });
                }
            } else {
                String errorMessage;
                try {
                    throw task.getException();
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    errorMessage = "Password salah";
                } catch (FirebaseAuthInvalidUserException e) {
                    errorMessage = "Email tidak terdaftar";
                } catch (Exception e) {
                    errorMessage = "Login gagal: " + e.getMessage();
                }
                Toast.getInstance(MainActivity.this).showToast(errorMessage);
            }
        });
    }


    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, connectionResult ->
                        Toast.getInstance(MainActivity.this).showToast("Koneksi dengan akun Google gagal")
                )
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void loginAkunGoogle() {
        loadingDialog.show();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                loadingDialog.dismiss();
                Toast.getInstance(this).showToast("Login Google gagal");
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            loadingDialog.dismiss();
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user == null) return;

                String userId = user.getUid();
                String email = user.getEmail();
                String namaLengkap = user.getDisplayName();
                String foto = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "";

                FirebaseFirestore.getInstance().collection("users")
                        .document(userId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (!documentSnapshot.exists()) {
                                UserModel userModelData = new UserModel(userId, email, namaLengkap, "", "customer", foto,0,"","","","","","","");
                                FirebaseFirestore.getInstance().collection("users")
                                        .document(userId)
                                        .set(userModelData)
                                        .addOnSuccessListener(unused -> {
                                            Toast.getInstance(MainActivity.this).showToast("Login berhasil dan data disimpan");
                                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.getInstance(MainActivity.this)
                                                    .showToast("Login berhasil tapi gagal simpan data user");
                                        });
                            } else {
                                Toast.getInstance(MainActivity.this).showToast("Selamat Datang " + namaLengkap);
                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                            }
                        });
            } else {
                Toast.getInstance(MainActivity.this).showToast("Authentication Failed");
            }
        });
    }
}