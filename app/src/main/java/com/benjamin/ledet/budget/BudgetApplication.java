package com.benjamin.ledet.budget;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.benjamin.ledet.budget.Realm.DatabaseHandler;

public class BudgetApplication extends Application {

    private static BudgetApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @NonNull
    public DatabaseHandler getDBHandler() {
        return new DatabaseHandler(getApplicationContext());
    }


    public static BudgetApplication getInstance() {
        if (sInstance == null) {
            sInstance = new BudgetApplication();
        }
        return sInstance;
    }

    public SharedPreferences getPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

}
