package com.management.finance;

import android.app.Application;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.management.finance.data.AppDatabase;
import com.management.finance.data.Transaction;
import com.management.finance.viewmodel.AddTransactionViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {

    private AutoCompleteTextView typeAutoCompleteTextView, categoryAutoCompleteTextView;
    private EditText amountEditText, dateEditText, descriptionEditText;
    private Button saveButton;

    private AddTransactionViewModel addTransactionViewModel;
    private Calendar calendar;
    private Date selectedDate;
    private AppDatabase db;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        typeAutoCompleteTextView = findViewById(R.id.typeAutoCompleteTextView);
        categoryAutoCompleteTextView = findViewById(R.id.categoryAutoCompleteTextView);
        amountEditText = findViewById(R.id.amountEditText);
        dateEditText = findViewById(R.id.dateEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        saveButton = findViewById(R.id.saveButton);

        db = AppDatabase.getInstance(this);

        // Dapatkan userId dari SharedPreferences
        userId = getLoggedInUserId();
        Log.d("AddTransactionActivity", "User ID: " + userId);  // Tambahkan log ini

        // Inisialisasi ViewModel dengan Factory
        AddTransactionViewModelFactory factory = new AddTransactionViewModelFactory(getApplication(), userId);
        addTransactionViewModel = new ViewModelProvider(this, factory).get(AddTransactionViewModel.class);

        calendar = Calendar.getInstance();

        // Setup AutoCompleteTextView untuk Type
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                R.array.transaction_types, android.R.layout.simple_spinner_dropdown_item);
        typeAutoCompleteTextView.setAdapter(typeAdapter);

        // Setup AutoCompleteTextView untuk Category
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.transaction_categories, android.R.layout.simple_spinner_dropdown_item);
        categoryAutoCompleteTextView.setAdapter(categoryAdapter);

        // Date Picker
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                selectedDate = calendar.getTime();
                updateDateLabel();
            }
        };

        dateEditText.setOnClickListener(view -> {
            new DatePickerDialog(AddTransactionActivity.this, dateSetListener,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        saveButton.setOnClickListener(v -> {
            String type = typeAutoCompleteTextView.getText().toString();
            String category = categoryAutoCompleteTextView.getText().toString();
            String amountString = amountEditText.getText().toString();
            String description = descriptionEditText.getText().toString();

            if (amountString.isEmpty()) {
                Toast.makeText(this, "Jumlah harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountString);
            Date date = selectedDate;

            Transaction transaction = new Transaction(type, amount, category, date, description, userId);
            AppDatabase.databaseWriteExecutor.execute(() -> {
                db.transactionDao().insert(transaction);
            });


            Toast.makeText(this, "Transaksi berhasil disimpan", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void updateDateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateEditText.setText(sdf.format(calendar.getTime()));
    }

    private int getLoggedInUserId() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getInt("userId", -1); // Mengembalikan -1 jika userId tidak ditemukan
    }

    // Factory untuk membuat AddTransactionViewModel dengan parameter
    private static class AddTransactionViewModelFactory implements ViewModelProvider.Factory {
        private Application application;
        private int userId;

        public AddTransactionViewModelFactory(Application application, int userId) {
            this.application = application;
            this.userId = userId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(AddTransactionViewModel.class)) {
                return (T) new AddTransactionViewModel(application, userId);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}