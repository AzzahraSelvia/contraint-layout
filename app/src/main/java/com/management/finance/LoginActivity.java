package com.management.finance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.management.finance.R;
import com.management.finance.data.AppDatabase;
import com.management.finance.data.User;

public class LoginActivity extends AppCompatActivity {

    private EditText pinEditText;
    private Button loginButton;
    private TextView registerTextView;
    private AppDatabase db;
    private SharedPreferences sharedPreferences; // Tambahkan ini

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pinEditText = findViewById(R.id.pinEditText);
        loginButton = findViewById(R.id.loginButton);
        registerTextView = findViewById(R.id.registerTextView);

        db = AppDatabase.getInstance(this);
        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE); // Inisialisasi SharedPreferences

        // Periksa status login saat activity dibuat
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            // Pengguna sudah login, arahkan ke DashboardActivity
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
            finish(); // Tutup LoginActivity
            return; // Penting: Keluar dari onCreate() jika sudah login
        }

        loginButton.setOnClickListener(v -> {
            String pin = pinEditText.getText().toString();

            AppDatabase.databaseWriteExecutor.execute(() -> {
                // Coba dapatkan pengguna dengan PIN yang dimasukkan
                User user = db.userDao().getUserByPin(pin);

                if (user != null) {
                    // Simpan status login dan userId ke SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", true); // Simpan status login
                    editor.putInt("userId", user.getId()); // Simpan ID pengguna
                    editor.apply();

                    // Jika pengguna ditemukan, pindah ke DashboardActivity
                    runOnUiThread(() -> {
                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    // Jika pengguna tidak ditemukan, tampilkan pesan kesalahan
                    runOnUiThread(() -> Toast.makeText(this, "PIN salah", Toast.LENGTH_SHORT).show());
                }
            });
        });

        registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}