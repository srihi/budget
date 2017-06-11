package com.benjamin.ledet.budget;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.benjamin.ledet.budget.model.DatabaseHandler;

public class BudgetApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @NonNull
    public DatabaseHandler getDatabaseHandler() {
        return new DatabaseHandler(getApplicationContext());
    }

    public SharedPreferences getSharedPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

}
