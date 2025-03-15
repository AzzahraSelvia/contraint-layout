package com.management.finance;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.management.finance.R;
import com.management.finance.utils.PinManager;
import com.management.finance.viewmodel.DashboardViewModel;

public class DashboardActivity extends AppCompatActivity {

    private TextView saldoTextView, pemasukanTextView, pengeluaranTextView;
    private FloatingActionButton tambahTransaksiButton;
    private PinManager pinManager;
    private int userId;
    private ImageButton settingsButton, reportButton, logoutButton;
    private Toolbar toolbar;
    private TextView toolbarTitle;

    private DashboardViewModel dashboardViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        saldoTextView = findViewById(R.id.saldoTextView);
        pemasukanTextView = findViewById(R.id.pemasukanTextView);
        pengeluaranTextView = findViewById(R.id.pengeluaranTextView);
        pengeluaranTextView = findViewById(R.id.pengeluaranTextView);
        tambahTransaksiButton = findViewById(R.id.tambahTransaksiButton);
        settingsButton = findViewById(R.id.settingsButton);
        reportButton = findViewById(R.id.reportButton);
        logoutButton = findViewById(R.id.logoutButton);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = toolbar.findViewById(R.id.toolbarTitle);

        pinManager = new PinManager(this);

        // Get the logged-in user ID
        userId = getLoggedInUserId();
        Log.d("DashboardActivity", "User ID: " + userId);

        // Redirect to LoginActivity if userId is -1
        if (userId == -1) {
            Log.d("DashboardActivity", "User not logged in. Redirecting to LoginActivity.");
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return; // Important: Exit onCreate()
        }

        DashboardViewModelFactory factory = new DashboardViewModelFactory(getApplication(), userId);
        dashboardViewModel = new ViewModelProvider(this, factory).get(DashboardViewModel.class);

        tambahTransaksiButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AddTransactionActivity.class);
            startActivity(intent);
        });

        dashboardViewModel.getSaldo().observe(this, saldo -> {
            saldoTextView.setText("Saldo: " + saldo);
        });

        dashboardViewModel.getTotalPemasukan().observe(this, pemasukan -> {
            pemasukanTextView.setText("Pemasukan: " + pemasukan);
        });

        dashboardViewModel.getTotalPengeluaran().observe(this, pengeluaran -> {
            pengeluaranTextView.setText("Pengeluaran: " + pengeluaran);
        });

        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        reportButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ReportActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            pinManager.clearPin();
            SharedPreferences prefs = getSharedPreferences("login_prefs", MODE_PRIVATE); // Use "login_prefs"
            SharedPreferences.Editor editor = prefs.edit();
            try {
                editor.remove("userId");
                editor.putBoolean("isLoggedIn", false); // Clear isLoggedIn flag
                editor.apply();
                Log.d("DashboardActivity", "Logout successful. userId and isLoggedIn cleared.");
            } catch (Exception e) {
                Log.e("DashboardActivity", "Error clearing SharedPreferences during logout", e); // Updated tag
            }

            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        toolbarTitle.setText("Dashboard");
    }

    private int getLoggedInUserId() {
        SharedPreferences prefs = getSharedPreferences("login_prefs", MODE_PRIVATE); // Use "login_prefs"
        return prefs.getInt("userId", -1);
    }

    private static class DashboardViewModelFactory implements ViewModelProvider.Factory {
        private Application application;
        private int userId;

        public DashboardViewModelFactory(Application application, int userId) {
            this.application = application;
            this.userId = userId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(DashboardViewModel.class)) {
                return (T) new DashboardViewModel(application, userId);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}