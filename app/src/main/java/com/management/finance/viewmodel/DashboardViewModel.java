package com.management.finance.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import com.management.finance.data.Transaction;
import com.management.finance.data.TransactionRepository;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DashboardViewModel extends AndroidViewModel {

    private TransactionRepository repository;
    private MediatorLiveData<List<Transaction>> allTransactions = new MediatorLiveData<>();
    private MediatorLiveData<Double> saldo = new MediatorLiveData<>();
    private MediatorLiveData<Double> totalPemasukan = new MediatorLiveData<>();
    private MediatorLiveData<Double> totalPengeluaran = new MediatorLiveData<>();
    private int userId; // Tambahkan ini

    public DashboardViewModel(@NonNull Application application, int userId) {
        super(application);
        this.userId = userId;
        repository = new TransactionRepository(application);

        // Inisialisasi LiveData untuk Pemasukan, Pengeluaran, dan Saldo
        initializeLiveData();
        loadTransactions();
    }

    private void initializeLiveData() {
        // Dapatkan bulan dan tahun saat ini dalam format YYYY-MM
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        String yearMonth = sdf.format(Calendar.getInstance().getTime());

        // Ambil LiveData untuk total pemasukan dan pengeluaran untuk bulan ini
        LiveData<Double> incomeLiveData = repository.getTotalIncome(userId, yearMonth);
        LiveData<Double> expenseLiveData = repository.getTotalExpense(userId, yearMonth);

        // Tambahkan incomeLiveData ke MediatorLiveData untuk totalPemasukan
        totalPemasukan.addSource(incomeLiveData, value -> {
            if (value == null) {
                totalPemasukan.setValue(0.0);
            } else {
                totalPemasukan.setValue(value);
            }
            updateSaldo();
        });

        // Tambahkan expenseLiveData ke MediatorLiveData untuk totalPengeluaran
        totalPengeluaran.addSource(expenseLiveData, value -> {
            if (value == null) {
                totalPengeluaran.setValue(0.0);
            } else {
                totalPengeluaran.setValue(value);
            }
            updateSaldo();
        });
        saldo.setValue(0.0); // inisialisasi
    }

    private void loadTransactions() {
        LiveData<List<Transaction>> transactionLiveData = repository.getAllTransactions(userId);
        allTransactions.addSource(transactionLiveData, value -> {
            allTransactions.setValue(value);
        });
    }

    private void updateSaldo() {
        Double pemasukan = totalPemasukan.getValue() != null ? totalPemasukan.getValue() : 0.0;
        Double pengeluaran = totalPengeluaran.getValue() != null ? totalPengeluaran.getValue() : 0.0;
        saldo.setValue(pemasukan - pengeluaran);
    }

    public LiveData<Double> getSaldo() {
        return saldo;
    }

    public LiveData<Double> getTotalPemasukan() {
        return totalPemasukan;
    }

    public LiveData<Double> getTotalPengeluaran() {
        return totalPengeluaran;
    }

    public LiveData<List<Transaction>> getAllTransactions() {
        return allTransactions;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
        loadTransactions(); // Reload transactions when userId changes
        initializeLiveData(); // Re-initialize LiveData when userId changes
    }
}