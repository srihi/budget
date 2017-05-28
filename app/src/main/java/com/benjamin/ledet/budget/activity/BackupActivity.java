package com.benjamin.ledet.budget.activity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.benjamin.ledet.budget.BudgetApplication;
import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.backup.Backup;
import com.benjamin.ledet.budget.backup.BudgetBackup;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.firebase.crash.FirebaseCrash;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class BackupActivity extends AppCompatActivity {

    @BindView(R.id.cl_activity_backup)
    CoordinatorLayout clPrincipal;

    @BindView(R.id.activity_main_toolbar)
    Toolbar toolbar;

    @BindView(R.id.rl_folder)
    RelativeLayout rlFolder;

    @BindView(R.id.rl_backup)
    RelativeLayout rlBackup;

    @BindView(R.id.rl_restore)
    RelativeLayout rlRestore;

    @BindView(R.id.iv_backup_folder)
    ImageView ivFolder;

    @BindView(R.id.tv_backup_folder_description)
    TextView tvFolder;

    @BindView(R.id.switch_backup)
    Switch aSwitch;

    @BindView(R.id.tv_last_backup_description)
    TextView tvLastBackup;

    @BindView(R.id.tv_last_restore_description)
    TextView tvLastRestore;

    @BindView(R.id.activity_backup_loading_panel)
    RelativeLayout loadingPanel;

    private static final String TAG = "budget_drive_backup";

    private Backup backup;
    private BudgetBackup budgetBackup = null;
    private GoogleApiClient mGoogleApiClient;
    private IntentSender intentPicker;
    private Realm realm;
    private String backupFolderId;
    private String backupFolderTitle;

    private SharedPreferences sharedPreferences;

    //return to the previous activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class LoadBackupInformationsFromDrive extends AsyncTask<Void, Integer, Boolean> {

        private Context context;

        public LoadBackupInformationsFromDrive(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            backupFolderId = sharedPreferences.getString("backup_folder", "");
            if (!backupFolderId.equals("")) {
                findFolderTitle();
                if(findBackupFromDrive(DriveId.decodeFromString(backupFolderId).asDriveFolder())){
                    return true;
                }
            }
            return false;
        }

        private void findFolderTitle(){
                DriveId.decodeFromString(backupFolderId).asDriveFolder().getMetadata((mGoogleApiClient)).setResultCallback(
                        new ResultCallback<DriveResource.MetadataResult>() {
                            @Override
                            public void onResult(@NonNull DriveResource.MetadataResult result) {
                                if (!result.getStatus().isSuccess()) {
                                    showErrorDialog();
                                    return;
                                }
                                Metadata metadata = result.getMetadata();
                                backupFolderTitle = metadata.getTitle();
                            }
                        }
                );
            }

        private boolean findBackupFromDrive(DriveFolder folder){
            Query query = new Query.Builder()
                    .addFilter(Filters.eq(SearchableField.TITLE, "budget.realm"))
                    .addFilter(Filters.eq(SearchableField.TRASHED, false))
                    .build();

            PendingResult<DriveApi.MetadataBufferResult> pendingResult = folder.queryChildren(mGoogleApiClient,query);
            DriveApi.MetadataBufferResult result = pendingResult.await();
            MetadataBuffer buffer = result.getMetadataBuffer();
            if (buffer.getCount() != 0){
                Metadata metadata = buffer.get(0);
                DriveId driveId = metadata.getDriveId();
                Date modifiedDate = metadata.getModifiedDate();
                long backupSize = metadata.getFileSize();
                budgetBackup = new BudgetBackup(driveId, modifiedDate.getTime(), backupSize);
            }

            return budgetBackup != null;
        }

        @Override
        protected void onPreExecute() {
            loadingPanel.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            //folder name
            if(backupFolderTitle != null){
                tvFolder.setText(backupFolderTitle);
                ivFolder.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.PrimaryColor));
            }

            if(aBoolean){
                //last backup
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(budgetBackup.getModifiedDate());
                tvLastBackup.setText(getString(R.string.activity_backup_date,calendar.get(Calendar.DAY_OF_MONTH),calendar.getDisplayName(Calendar.MONTH,Calendar.SHORT,Locale.getDefault()),calendar.get(Calendar.YEAR),calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE)));

                //last restoration
                long date = sharedPreferences.getLong("last_restore", 0);
                if (date != 0){
                    calendar.setTimeInMillis(date);
                    tvLastRestore.setText(getString(R.string.activity_backup_date,calendar.get(Calendar.DAY_OF_MONTH),calendar.getDisplayName(Calendar.MONTH,Calendar.SHORT,Locale.getDefault()),calendar.get(Calendar.YEAR),calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE)));
                }
            }
            loadingPanel.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_backup);

        ButterKnife.bind(this);

        //display toolbar
        toolbar.setTitle(R.string.title_activity_backup);
        setSupportActionBar(toolbar);
        //display back button
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        BudgetApplication budgetApplication = (BudgetApplication) getApplicationContext();
        realm = budgetApplication.getDBHandler().getRealmInstance();
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        backup = budgetApplication.getBackup();
        backup.init(this);
        backup.start();
        mGoogleApiClient = backup.getClient();

        if(getIntent().getExtras() != null){
            if(getIntent().getExtras().getString("reload","").equals("folder")){
                showFolderSuccessDialog();
            }
            if(getIntent().getExtras().getString("reload","").equals("backup")){
                showBackupSuccessDialog();
            }
        }

        new LoadBackupInformationsFromDrive(this).execute();

        rlFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFolderPicker();

            }
        });

        rlBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!backupFolderId.equals("")){

                    AlertDialog.Builder builder = new AlertDialog.Builder(BackupActivity.this,R.style.CustomAlertDialog);
                    TextView title = new TextView(BackupActivity.this);
                    title.setText(R.string.save_data_label);
                    title.setTextColor(ContextCompat.getColor(BackupActivity.this,R.color.PrimaryColor));
                    title.setGravity(Gravity.CENTER);
                    title.setTextSize(22);
                    builder.setCustomTitle(title);
                    builder.setMessage(R.string.save_data_description);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            uploadToDrive(DriveId.decodeFromString(backupFolderId));
                        }
                    });
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else{
                    showFolderErrorDialog();
                }
            }
        });


        rlRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!backupFolderId.equals("")){
                    if (budgetBackup != null){
                        AlertDialog.Builder builder = new AlertDialog.Builder(BackupActivity.this, R.style.CustomAlertDialog);
                        TextView title = new TextView(BackupActivity.this);
                        title.setText(R.string.restore_data_label);
                        title.setTextColor(ContextCompat.getColor(BackupActivity.this,R.color.PrimaryColor));
                        title.setGravity(Gravity.CENTER);
                        title.setTextSize(22);
                        builder.setCustomTitle(title);
                        builder.setMessage(R.string.restore_data_description);
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                downloadFromDrive(budgetBackup.getDriveId().asDriveFile());
                            }
                        });
                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    else{
                        showBackupErrorDialog();
                    }
                }else{
                    showFolderErrorDialog();
                }
            }
        });

    }

    private void openFolderPicker() {
        try {
            intentPicker = null;
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                if (intentPicker == null)
                    intentPicker = buildIntent();
                //Start the picker to choose a folder
                startIntentSenderForResult(
                        intentPicker, 4, null, 0, 0, 0);
            }
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Unable to send intent", e);
            showErrorDialog();
        }
    }

    private IntentSender buildIntent() {
        return Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(new String[]{DriveFolder.MIME_TYPE})
                .build(mGoogleApiClient);
    }

    private void deleteBackupFromDrive(DriveFolder folder){
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, "budget.realm"))
                .addFilter(Filters.eq(SearchableField.TRASHED, false))
                .build();

        folder.queryChildren(mGoogleApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {

                    @Override
                    public void onResult(@NonNull DriveApi.MetadataBufferResult result) {
                        MetadataBuffer buffer = result.getMetadataBuffer();
                        buffer.get(0).getDriveId().asDriveFile().delete(mGoogleApiClient);
                    }
                });
    }


    public void downloadFromDrive(DriveFile file) {
        file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                    @Override
                    public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                        if (!result.getStatus().isSuccess()) {
                            showErrorDialog();
                            return;
                        }

                        // DriveContents object contains pointers
                        // to the actual byte stream
                        DriveContents contents = result.getDriveContents();
                        InputStream input = contents.getInputStream();

                        try {
                            File file = new File(realm.getPath());
                            OutputStream output = new FileOutputStream(file);
                            try {
                                try {
                                    byte[] buffer = new byte[4 * 1024]; // or other buffer size
                                    int read;

                                    while ((read = input.read(buffer)) != -1) {
                                        output.write(buffer, 0, read);
                                    }
                                    output.flush();
                                } finally {
                                    safeCloseClosable(input);
                                }
                            } catch (Exception e) {
                                reportToFirebase(e, "Error downloading backup from drive");
                                e.printStackTrace();
                            }
                        } catch (FileNotFoundException e) {
                            reportToFirebase(e, "Error downloading backup from drive, file not found");
                            e.printStackTrace();
                        } finally {
                            safeCloseClosable(input);
                        }

                        saveLastRestore(System.currentTimeMillis());

                        // Reboot app
                        Intent mStartActivity = new Intent(getApplicationContext(), MainActivity.class);
                        int mPendingIntentId = 123456;
                        PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        System.exit(0);

                    }
                });
    }

    private void safeCloseClosable(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            reportToFirebase(e, "Error downloading backup from drive, IO Exception");
            e.printStackTrace();
        }
    }

    private void uploadToDrive(DriveId mFolderDriveId) {
        if (mFolderDriveId != null) {
            //Create the file on GDrive
            final DriveFolder folder = mFolderDriveId.asDriveFolder();
            Drive.DriveApi.newDriveContents(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                        @Override
                        public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                            if (!result.getStatus().isSuccess()) {
                                Log.e(TAG, "Error while trying to create new file contents");
                                showErrorDialog();
                                return;
                            }
                            final DriveContents driveContents = result.getDriveContents();

                            // Perform I/O off the UI thread.
                            new Thread() {
                                @Override
                                public void run() {
                                    // write content to DriveContents
                                    OutputStream outputStream = driveContents.getOutputStream();

                                    FileInputStream inputStream = null;
                                    try {
                                        inputStream = new FileInputStream(new File(realm.getPath()));
                                    } catch (FileNotFoundException e) {
                                        reportToFirebase(e, "Error uploading backup from drive, file not found");
                                        showErrorDialog();
                                        e.printStackTrace();
                                    }

                                    final byte[] buf = new byte[1024];
                                    int bytesRead;
                                    try {
                                        if (inputStream != null) {
                                            while ((bytesRead = inputStream.read(buf)) > 0) {
                                                outputStream.write(buf, 0, bytesRead);
                                            }
                                        }
                                    } catch (IOException e) {

                                        showErrorDialog();
                                        e.printStackTrace();
                                    }

                                    if (budgetBackup != null){
                                        deleteBackupFromDrive(folder);
                                    }

                                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                            .setTitle("budget.realm")
                                            .setMimeType("text/plain")
                                            .build();

                                    // create a file in selected folder
                                    folder.createFile(mGoogleApiClient, changeSet, driveContents)
                                            .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                                                @Override
                                                public void onResult(@NonNull DriveFolder.DriveFileResult result) {
                                                    if (!result.getStatus().isSuccess()) {
                                                        Log.d(TAG, "Error while trying to create the file");
                                                        showErrorDialog();
                                                        finish();
                                                        return;
                                                    }
                                                    Intent intent = new Intent(BackupActivity.this,BackupActivity.class);
                                                    intent.putExtra("reload","backup");
                                                    finish();
                                                    startActivity(intent);

                                                }
                                            });
                                }
                            }.start();
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    backup.start();
                }
                break;
            // REQUEST_CODE_PICKER
            case 2:
                intentPicker = null;

                if (resultCode == RESULT_OK) {
                    //Get the folder drive id
                    DriveId mFolderDriveId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    uploadToDrive(mFolderDriveId);
                }
                break;

            // REQUEST_CODE_SELECT
            case 3:
                if (resultCode == RESULT_OK) {
                    // get the selected item's ID
                    DriveId driveId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    DriveFile file = driveId.asDriveFile();
                    downloadFromDrive(file);

                } else {
                    showErrorDialog();
                }
                finish();
                break;
            // REQUEST_CODE_PICKER_FOLDER
            case 4:
                if (resultCode == RESULT_OK) {
                    //Get the folder drive id
                    DriveId mFolderDriveId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    saveBackupFolder(mFolderDriveId.encodeToString());
                    Intent intent = new Intent(BackupActivity.this,BackupActivity.class);
                    intent.putExtra("reload","folder");
                    finish();
                    startActivity(intent);
                }
                break;
        }
    }

    private void saveBackupFolder(String folderPath) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("backup_folder", folderPath);
        editor.apply();
    }

    private void saveLastRestore(long date){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("last_restore", date);
        editor.apply();
    }

    private void showBackupSuccessDialog() {
        Snackbar snackbar = Snackbar.make(clPrincipal , R.string.snackbar_backup_success , Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.PrimaryColor));
        snackbar.show();
    }

    private void showBackupErrorDialog() {
        Snackbar snackbar = Snackbar.make(clPrincipal , R.string.snackbar_backup_error , Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(Color.RED);
        snackbar.show();
    }

    private void showFolderSuccessDialog() {
        Snackbar snackbar = Snackbar.make(clPrincipal , R.string.snackbar_folder_success , Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.PrimaryColor));
        snackbar.show();
    }

    private void showFolderErrorDialog() {
        Snackbar snackbar = Snackbar.make(clPrincipal , R.string.snackbar_folder_error , Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(Color.RED);
        snackbar.show();
    }

    private void showErrorDialog() {
        Snackbar snackbar = Snackbar.make(clPrincipal , R.string.snackbar_error , Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(Color.RED);
        snackbar.show();
    }

    private void reportToFirebase(Exception e, String message) {
        FirebaseCrash.log(message);
        FirebaseCrash.report(e);
    }
}
