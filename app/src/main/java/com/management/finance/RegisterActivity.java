package com.management.finance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.management.finance.data.AppDatabase;
import com.management.finance.data.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText pinEditText, confirmPinEditText;
    private Button registerButton;
    private TextView loginTextView;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        pinEditText = findViewById(R.id.pinEditText);
        confirmPinEditText = findViewById(R.id.confirmPinEditText);
        registerButton = findViewById(R.id.registerButton);
        loginTextView = findViewById(R.id.loginTextView);

        db = AppDatabase.getInstance(this);

        registerButton.setOnClickListener(v -> {
            String pin = pinEditText.getText().toString();
            String confirmPin = confirmPinEditText.getText().toString();

            if (pin.equals(confirmPin)) {
                if (pin.length() == 6) {
                    // Periksa apakah PIN sudah digunakan
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        User existingUser = db.userDao().getUserByPin(pin);

                        if (existingUser != null) {
                            // PIN sudah digunakan
                            runOnUiThread(() -> Toast.makeText(this, "PIN sudah digunakan. Silakan gunakan PIN lain.", Toast.LENGTH_SHORT).show());
                        } else {
                            // PIN belum digunakan, buat User baru
                            User user = new User(pin);

                            // Simpan user ke database
                            long userId = db.userDao().insert(user);

                            // Simpan userId ke SharedPreferences dan Pindah ke LoginActivity setelah pendaftaran berhasil
                            runOnUiThread(() -> {
                                SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putInt("userId", (int) userId); // Simpan ID pengguna, ubah ke int
                                editor.apply();

                                Toast.makeText(this, "PIN berhasil disimpan", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish(); // Kembali ke LoginActivity
                            });
                        }
                    });
                } else {
                    Toast.makeText(this, "PIN harus 6 digit", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "PIN tidak sama", Toast.LENGTH_SHORT).show();
            }
        });

        loginTextView.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}