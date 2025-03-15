package com.management.finance.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.management.finance.data.AppDatabase;
import com.management.finance.data.User;
import com.management.finance.data.UserDao;
import com.management.finance.data.Transaction;
import com.management.finance.data.TransactionDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsViewModel extends AndroidViewModel {

    private UserDao userDao;
    private TransactionDao transactionDao;
    private LiveData<User> user;
    private ExecutorService executorService;

    public SettingsViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        userDao = database.userDao();
        transactionDao = database.transactionDao();
        executorService = Executors.newFixedThreadPool(5);

        // Asumsikan Anda hanya memiliki satu user dengan ID 1 (atau cara Anda mendapatkan user yang sedang login)
        user = userDao.getUserById(1);
    }

    public LiveData<User> getUser() {
        return user;
    }

    public void updateUser(User user) {
        executorService.execute(() -> {
            userDao.update(user); // Gunakan update sekarang
        });
    }

    public void resetData(int userId) {
        executorService.execute(() -> {
            // Hapus semua transaksi
            transactionDao.deleteAllTransactions(userId);
        });
    }
}