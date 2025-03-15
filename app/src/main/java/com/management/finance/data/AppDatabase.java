package com.management.finance.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Transaction.class, User.class}, version = 3, exportSchema = false) // Tambahkan User.class dan tingkatkan versinya
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract TransactionDao transactionDao();
    public abstract UserDao userDao(); // Tambahkan ini

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "keuangan_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}