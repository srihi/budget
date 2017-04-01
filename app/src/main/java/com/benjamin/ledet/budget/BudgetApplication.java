package com.benjamin.ledet.budget;

import android.app.Application;
import android.support.annotation.NonNull;

import com.benjamin.ledet.budget.Realm.DatabaseHandler;
import com.benjamin.ledet.budget.backup.Backup;
import com.benjamin.ledet.budget.backup.GoogleDriveBackup;

/**
 * Created by benjaminledet on 05/03/2017.
 */

public class BudgetApplication extends Application {

    private static BudgetApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @NonNull
    public Backup getBackup() {
        return new GoogleDriveBackup();
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

}
