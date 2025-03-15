package com.management.finance;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.management.finance.R;
import com.management.finance.viewmodel.SettingsViewModel;

public class SettingsActivity extends AppCompatActivity {

    private MaterialCardView changePinCardView, resetDataCardView;
    private SettingsViewModel settingsViewModel;
    private MaterialButton logoutButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        changePinCardView = findViewById(R.id.changePinCardView);
        resetDataCardView = findViewById(R.id.resetDataCardView);
        logoutButton = findViewById(R.id.logoutButton);
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE);

        changePinCardView.setOnClickListener(v -> showChangePinDialog());
        resetDataCardView.setOnClickListener(v -> showResetConfirmationDialog());

        logoutButton.setOnClickListener(v -> performLogout());
    }

    private void performLogout() {
        // Hapus status login dari SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.remove("userId"); // Optional: Remove userId as well
        editor.apply();

        // Navigasi kembali ke halaman login
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showChangePinDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ubah PIN");

        final EditText oldPinEditText = new EditText(this);
        oldPinEditText.setHint("PIN Lama");
        final EditText newPinEditText = new EditText(this);
        newPinEditText.setHint("PIN Baru");
        final EditText confirmNewPinEditText = new EditText(this);
        confirmNewPinEditText.setHint("Konfirmasi PIN Baru");

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.addView(oldPinEditText);
        layout.addView(newPinEditText);
        layout.addView(confirmNewPinEditText);

        builder.setView(layout);

        builder.setPositiveButton("Simpan", (dialog, which) -> {
            String oldPin = oldPinEditText.getText().toString().trim();
            String newPin = newPinEditText.getText().toString().trim();
            String confirmNewPin = confirmNewPinEditText.getText().toString().trim();

            settingsViewModel.getUser().observe(this, user -> {
                if (user != null) {
                    if (!user.getPin().equals(oldPin)) {
                        Toast.makeText(this, "PIN lama salah", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!newPin.equals(confirmNewPin)) {
                        Toast.makeText(this, "PIN baru tidak sama", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (newPin.length() != 6) {
                        Toast.makeText(this, "PIN baru harus 6 digit", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    user.setPin(newPin);
                    settingsViewModel.updateUser(user);
                    Toast.makeText(this, "PIN berhasil diubah", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "User tidak ditemukan", Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Batal", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showResetConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Data");
        builder.setMessage("Apakah Anda yakin ingin menghapus semua data transaksi?");

        builder.setPositiveButton("Ya", (dialog, which) -> {
            settingsViewModel.resetData(1); // Supposing that userId is 1.
            Toast.makeText(this, "Data berhasil direset", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Tidak", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}