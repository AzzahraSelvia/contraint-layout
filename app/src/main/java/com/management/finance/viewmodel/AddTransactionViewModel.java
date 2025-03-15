package com.management.finance.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.management.finance.data.AppDatabase;
import com.management.finance.data.Transaction;
import com.management.finance.data.TransactionDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddTransactionViewModel extends AndroidViewModel {

    private TransactionDao transactionDao;
    private ExecutorService executorService;
    private int userId;

    public AddTransactionViewModel(@NonNull Application application, int userId) {
        super(application);
        executorService = Executors.newFixedThreadPool(5);
        transactionDao = AppDatabase.getInstance(application).transactionDao();
        this.userId = userId;
    }

    public void insert(Transaction transaction) {
        executorService.execute(() -> {
            transactionDao.insert(transaction);
        });
    }

    public LiveData<List<Transaction>> getAllTransactions() {
        return transactionDao.getAllTransactions(userId);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}