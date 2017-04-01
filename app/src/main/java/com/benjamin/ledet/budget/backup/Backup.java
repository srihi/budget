package com.benjamin.ledet.budget.backup;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by benjaminledet on 15/03/2017.
 */

public interface Backup {
    void init(@NonNull final Activity activity);

    void start();

    void stop();

    GoogleApiClient getClient();
}
