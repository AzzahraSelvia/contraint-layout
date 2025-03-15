package com.management.finance.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.management.finance.data.AppDatabase;
import com.management.finance.data.TransactionDao;

public class ReportViewModel extends AndroidViewModel {

    private TransactionDao transactionDao;
    private int userId;

    public ReportViewModel(@NonNull Application application, int userId) {
        super(application);
        transactionDao = AppDatabase.getInstance(application).transactionDao();
        this.userId = userId;
    }

    public LiveData<Double> getTotalPemasukan(String yearMonth) {
        return transactionDao.getTotalIncome(userId, yearMonth);
    }

    public LiveData<Double> getTotalPengeluaran(String yearMonth) {
        return transactionDao.getTotalExpense(userId, yearMonth);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}