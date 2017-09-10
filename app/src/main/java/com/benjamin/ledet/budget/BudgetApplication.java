package com.benjamin.ledet.budget;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.benjamin.ledet.budget.model.DatabaseHandler;

public class BudgetApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    @NonNull
    public DatabaseHandler getDatabaseHandler() {
        return new DatabaseHandler(getApplicationContext());
    }

    public SharedPreferences getSharedPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    public static Context getContext(){
        return mContext;
    }

}
