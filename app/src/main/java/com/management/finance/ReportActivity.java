package com.management.finance;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.management.finance.R;
import com.management.finance.viewmodel.ReportViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity {

    private TextView totalPemasukanTextView, totalPengeluaranTextView;
    private ReportViewModel reportViewModel;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        totalPemasukanTextView = findViewById(R.id.totalPemasukanTextView);
        totalPengeluaranTextView = findViewById(R.id.totalPengeluaranTextView);

        userId = getLoggedInUserId();
        Log.d("ReportActivity", "User ID: " + userId);  // Tambahkan log ini


        // Inisialisasi ViewModel dengan Factory
        ReportViewModelFactory factory = new ReportViewModelFactory(getApplication(), userId);
        reportViewModel = new ViewModelProvider(this, factory).get(ReportViewModel.class);

        // Mendapatkan bulan dan tahun saat ini
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        String yearMonth = dateFormat.format(calendar.getTime());

        // Observe data dari ViewModel
        reportViewModel.getTotalPemasukan(yearMonth).observe(this, totalPemasukan -> {
            if (totalPemasukan != null) {
                totalPemasukanTextView.setText("Total Pemasukan: " + totalPemasukan);
            } else {
                totalPemasukanTextView.setText("Total Pemasukan: 0.0");
            }
        });

        reportViewModel.getTotalPengeluaran(yearMonth).observe(this, totalPengeluaran -> {
            if (totalPengeluaran != null) {
                totalPengeluaranTextView.setText("Total Pengeluaran: " + totalPengeluaran);
            } else {
                totalPengeluaranTextView.setText("Total Pengeluaran: 0.0");
            }
        });
    }

    private int getLoggedInUserId() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getInt("userId", -1); // Mengembalikan -1 jika userId tidak ditemukan
    }
    // Factory untuk membuat AddTransactionViewModel dengan parameter
    private static class ReportViewModelFactory implements ViewModelProvider.Factory {
        private Application application;
        private int userId;

        public ReportViewModelFactory(Application application, int userId) {
            this.application = application;
            this.userId = userId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ReportViewModel.class)) {
                return (T) new ReportViewModel(application, userId);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}