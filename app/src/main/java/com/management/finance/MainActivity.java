package com.management.finance;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log; // Tambahkan ini

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.management.finance.DashboardActivity;
import com.management.finance.LoginActivity;
import com.management.finance.utils.PinManager; // Pastikan import ini benar

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000; // Durasi splash screen dalam milidetik
    private PinManager pinManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Pastikan view dengan ID "main" ada di layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        pinManager = new PinManager(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                // Cek apakah pengguna sudah login (memiliki PIN)
                if (pinManager.hasPin()) {
                    Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                finish();
            } catch (Exception e) {
                Log.e("MainActivity", "Error during splash screen logic", e);
            }
        }, SPLASH_DURATION);
    }
}