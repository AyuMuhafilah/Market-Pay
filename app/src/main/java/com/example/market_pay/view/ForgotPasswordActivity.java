    package com.example.market_pay.view;

    import android.content.Intent;
    import android.os.Bundle;
    import android.util.Patterns;
    import android.widget.Button;
    import android.widget.EditText;

    import androidx.activity.EdgeToEdge;
    import androidx.activity.OnBackPressedCallback;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.graphics.Insets;
    import androidx.core.view.ViewCompat;
    import androidx.core.view.WindowInsetsCompat;

    import com.example.market_pay.R;
    import com.example.market_pay.utils.LoadingDialog;
    import com.example.market_pay.utils.Toast;
    import com.google.firebase.auth.FirebaseAuth;

    public class ForgotPasswordActivity extends AppCompatActivity {

        private EditText txtEmail;
        private Button btnResetPassword, btnBack;
        private FirebaseAuth mAuth;
        private LoadingDialog loadingDialog;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_forgot_password);

            initViews();
            initListeners();
            loadingDialog = new LoadingDialog(this);

            getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                }
            });

            btnBack.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        private void initViews() {
            btnBack          = findViewById(R.id.btnBack);
            txtEmail         = findViewById(R.id.txtEmail);
            btnResetPassword = findViewById(R.id.btnResetPassword);
            mAuth            = FirebaseAuth.getInstance();
        }

        private void initListeners() {
            btnResetPassword.setOnClickListener(v -> resetPassword());
        }

        private void resetPassword() {
            String email = txtEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.getInstance(this).showToast("Email tidak boleh kosong");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.getInstance(this).showToast("Format email tidak valid");
                return;
            }
            loadingDialog.show();
            mAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener(unused -> {
                loadingDialog.dismiss();
                Toast.getInstance(this).showToast("Link reset password telah dikirim");
                startActivity(new Intent(this, MainActivity.class));
                finish();
            })
            .addOnFailureListener(e -> {
                loadingDialog.dismiss();
                Toast.getInstance(this).showToast("Gagal: " + e.getMessage());
            });
        }
    }
