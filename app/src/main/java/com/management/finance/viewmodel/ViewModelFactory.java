package com.management.finance.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private Application application;
    private int userId;

    public ViewModelFactory(Application application, int userId) {
        this.application = application;
        this.userId = userId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DashboardViewModel.class)) {
            return (T) new DashboardViewModel(application, userId);
        } else if (modelClass.isAssignableFrom(AddTransactionViewModel.class)) {
            return (T) new AddTransactionViewModel(application, userId);
        } else if (modelClass.isAssignableFrom(ReportViewModel.class)) {
            return (T) new ReportViewModel(application, userId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}