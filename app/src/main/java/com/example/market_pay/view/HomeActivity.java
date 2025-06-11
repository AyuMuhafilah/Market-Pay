package com.example.market_pay.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.market_pay.R;
import com.example.market_pay.helper.UserHelper;
import com.example.market_pay.view.admin.AdminFragment;
import com.example.market_pay.view.customer.CustomerFragment;
import com.example.market_pay.view.customer.TransaksiFragment;
import com.example.market_pay.view.merchant.MerchantFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        });

        setContentView(R.layout.activity_home);
        bottomNav = findViewById(R.id.bottom_navigation);
        Fragment fragmentAwal;
        fragmentAwal = new CustomerFragment();
        // Tampilkan fragment awal
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragmentAwal)
                .commit();

        // Akses Menu Bottom sesuai role
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UserHelper.getUserById(userId, user -> {
            if (user != null) {
                String role = user.getRole();
                if ("admin".equals(role)) {
                    bottomNav.getMenu().findItem(R.id.nav_merchant).setVisible(false);
                } else if ("merchant".equals(role)) {
                    bottomNav.getMenu().findItem(R.id.nav_admin).setVisible(false);
                } else {
                    bottomNav.getMenu().findItem(R.id.nav_admin).setVisible(false);
                    bottomNav.getMenu().findItem(R.id.nav_merchant).setVisible(false);
                }
            }
        });

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                fragment = new CustomerFragment();
            } else if (itemId == R.id.nav_profile) {
                fragment = new ProfileFragment();
            } else if (itemId == R.id.nav_transaksi) {
                fragment = new TransaksiFragment();
            }else if (itemId == R.id.nav_admin) {
                fragment = new AdminFragment();
            }else if (itemId == R.id.nav_merchant) {
                fragment = new MerchantFragment();
            }else {
                fragment = null;
            }
            if (fragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, fragment)
                        .commit();
                return true;
            }
            return false;
        });
        // Tentukan Tab Default
        bottomNav.setSelectedItemId(R.id.nav_home);
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

}