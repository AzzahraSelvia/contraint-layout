package com.management.finance.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PinManager {

    private static final String PREF_NAME = "PinPrefs";
    private static final String KEY_PIN = "pin";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;

    // shared pref mode
    private int PRIVATE_MODE = 0;

    public PinManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void savePin(String pin) {
        editor.putString(KEY_PIN, pin);
        editor.commit();
        Log.d("PinManager", "PIN saved: " + pin); // Add this line
    }

    public boolean checkPin(String pin) {
        String savedPin = pref.getString(KEY_PIN, null);
        Log.d("PinManager", "Saved PIN: " + savedPin); // Add this line
        Log.d("PinManager", "Entered PIN: " + pin); // Add this line
        return savedPin != null && savedPin.equals(pin);
    }

    public boolean hasPin() {
        return pref.contains(KEY_PIN);
    }

    public void clearPin() {
        editor.remove(KEY_PIN);
        editor.commit();
    }
}