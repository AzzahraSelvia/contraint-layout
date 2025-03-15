package com.management.finance.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update; // Tambahkan ini

@Dao
public interface UserDao {
    @Insert
    long insert(User user);

    @Update // Tambahkan ini
    int update(User user);

    @Query("SELECT * FROM users WHERE id = :userId")
    LiveData<User> getUserById(int userId);

    @Query("SELECT * FROM users WHERE pin = :pin")
    User getUserByPin(String pin);
}