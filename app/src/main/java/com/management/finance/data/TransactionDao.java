package com.management.finance.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TransactionDao {

    @Insert
    void insert(Transaction transaction);

    @Update
    void update(Transaction transaction);

    @Delete
    void delete(Transaction transaction);

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC")
    LiveData<List<Transaction>> getAllTransactions(int userId);

    @Query("SELECT * FROM transactions WHERE userId = :userId AND strftime('%Y-%m', date / 1000, 'unixepoch') = :yearMonth ORDER BY date DESC")
    LiveData<List<Transaction>> getTransactionsByMonth(int userId, String yearMonth);

    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND type = 'Pemasukan' AND strftime('%Y-%m', date / 1000, 'unixepoch') = :yearMonth")
    LiveData<Double> getTotalIncome(int userId, String yearMonth);

    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND type = 'Pengeluaran' AND strftime('%Y-%m', date / 1000, 'unixepoch') = :yearMonth")
    LiveData<Double> getTotalExpense(int userId, String yearMonth);

    // Tambahkan query ini
    @Query("DELETE FROM transactions WHERE userId = :userId")
    void deleteAllTransactions(int userId);
}