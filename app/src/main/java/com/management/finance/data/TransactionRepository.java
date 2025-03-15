package com.management.finance.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TransactionRepository {

    private TransactionDao transactionDao;
    private LiveData<List<Transaction>> allTransactions;

    public TransactionRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        transactionDao = database.transactionDao();
        // allTransactions = transactionDao.getAllTransactions(); Hapus baris ini
    }

    public void insert(Transaction transaction) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            transactionDao.insert(transaction);
        });
    }

    public void update(Transaction transaction) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            transactionDao.update(transaction);
        });
    }

    public void delete(Transaction transaction) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            transactionDao.delete(transaction);
        });
    }

    public LiveData<List<Transaction>> getAllTransactions(int userId) {
        return transactionDao.getAllTransactions(userId);
    }

    public LiveData<Double> getTotalIncome(int userId, String yearMonth) {
        return transactionDao.getTotalIncome(userId, yearMonth);
    }

    public LiveData<Double> getTotalExpense(int userId, String yearMonth) {
        return transactionDao.getTotalExpense(userId, yearMonth);
    }

    public LiveData<List<Transaction>> getTransactionsByMonth(int userId, String yearMonth) {
        return transactionDao.getTransactionsByMonth(userId, yearMonth);
    }
}