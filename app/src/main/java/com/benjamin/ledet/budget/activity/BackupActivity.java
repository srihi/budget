package com.benjamin.ledet.budget.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.benjamin.ledet.budget.BudgetApplication;
import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.Realm.FormatDateTime;
import com.benjamin.ledet.budget.backup.Backup;
import com.benjamin.ledet.budget.model.BudgetBackup;
import com.google.android.gms.common.api.GoogleApiClient;
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
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by benjamin on 02/04/2017.
 */

public class BackupActivity extends AppCompatActivity {

    @BindView(R.id.cl_activity_backup)
    CoordinatorLayout clPrincipal;

    @BindView(R.id.activity_main_toolbar)
    Toolbar toolbar;

    @BindView(R.id.rl_backup)
    RelativeLayout rlBackup;

    @BindView(R.id.rl_restore)
    RelativeLayout rlRestore;

    @BindView(R.id.bt_backup_folder)
    Button btFolder;

    @BindView(R.id.tv_backup_folder_description)
    TextView tvFolder;

    @BindView(R.id.switch_backup)
    Switch aSwitch;

    @BindView(R.id.tv_last_backup_description)
    TextView tvLastBackup;

    private static final String TAG = "budget_drive_backup";

    private Backup backup;
    private BudgetBackup budgetBackup;
    private GoogleApiClient mGoogleApiClient;
    private IntentSender intentPicker;
    private Realm realm;
    private String backupFolder;
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

    //Snackbar snackbar = Snackbar.make(clPrincipal , "test" , Snackbar.LENGTH_SHORT);
    //snackbar.getView().setBackgroundColor(ContextCompat.getColor(v.getContext(),R.color.PrimaryColor));
    //snackbar.show();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_backup);

        ButterKnife.bind(this);

        //display toolbar
        toolbar.setTitle(getResources().getString(R.string.save_and_restore_string));
        setSupportActionBar(toolbar);
        //display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        BudgetApplication budgetApplication = (BudgetApplication) getApplicationContext();
        realm = budgetApplication.getDBHandler().getRealmInstance();
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        backup = budgetApplication.getBackup();
        backup.init(this);
        backup.start();
        mGoogleApiClient = backup.getClient();

        btFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFolderPicker();

            }
        });

        rlBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadToDrive(DriveId.decodeFromString(backupFolder));
            }
        });


        rlRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Show backup folder, if exists
        backupFolder = sharedPreferences.getString("backup_folder", "");
        if (!("").equals(backupFolder)) {
            setBackupFolderTitle(DriveId.decodeFromString(backupFolder));
            btFolder.setBackgroundTintList(ContextCompat.getColorStateList(this,R.color.PrimaryColor));
            findBackupFromDrive(DriveId.decodeFromString(backupFolder).asDriveFolder());
        }

    }

    private void setBackupFolderTitle(DriveId id) {
        id.asDriveFolder().getMetadata((mGoogleApiClient)).setResultCallback(
                new ResultCallback<DriveResource.MetadataResult>() {
                    @Override
                    public void onResult(@NonNull DriveResource.MetadataResult result) {
                        if (!result.getStatus().isSuccess()) {
                            showErrorDialog();
                            return;
                        }
                        Metadata metadata = result.getMetadata();
                        tvFolder.setText(metadata.getTitle());
                    }
                }
        );
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

    private void findBackupFromDrive(DriveFolder folder){
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, "budget.realm"))
                .addFilter(Filters.eq(SearchableField.TRASHED, false))
                .build();

        folder.queryChildren(mGoogleApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {

                    @Override
                    public void onResult(@NonNull DriveApi.MetadataBufferResult result) {
                        MetadataBuffer buffer = result.getMetadataBuffer();
                        BudgetBackup budgetBackup = null;
                        if (buffer.getCount() != 0){
                            Metadata metadata = buffer.get(0);
                            DriveId driveId = metadata.getDriveId();
                            Date modifiedDate = metadata.getModifiedDate();
                            long backupSize = metadata.getFileSize();
                            budgetBackup = new BudgetBackup(driveId, modifiedDate, backupSize);
                        }
                        setBackupFile(budgetBackup);
                        if (budgetBackup != null){
                            setTvLastBackup();
                        }

                    }
                });
    }

    private void setBackupFile(BudgetBackup budgetBackup){
        this.budgetBackup = budgetBackup;
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

    private void setTvLastBackup(){
        FormatDateTime formatDateTime = new FormatDateTime(this);
        tvLastBackup.setText("Le " + formatDateTime.formatDate(budgetBackup.getModifiedDate()));
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

                        Toast.makeText(getApplicationContext(), R.string.activity_backup_drive_message_restart, Toast.LENGTH_LONG).show();

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
                                                    // Restart activity to apply changes
                                                    Intent intent = getIntent();
                                                    finish();
                                                    startActivity(intent);
                                                    showSuccessDialog();
                                                }
                                            });
                                }
                            }.start();
                        }
                    });
        }
    }

    private void openOnDrive(DriveId driveId) {
        driveId.asDriveFolder().getMetadata((mGoogleApiClient)).setResultCallback(
                new ResultCallback<DriveResource.MetadataResult>() {
                    @Override
                    public void onResult(@NonNull DriveResource.MetadataResult result) {
                        if (!result.getStatus().isSuccess()) {
                            showErrorDialog();
                            return;
                        }
                        Metadata metadata = result.getMetadata();
                        String url = metadata.getAlternateLink();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                }
        );
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

                    saveBackupFolder(mFolderDriveId.encodeToString());

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
                    // Restart activity to apply changes
                    Intent intent = getIntent();
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

    private void showSuccessDialog() {
        Toast.makeText(getApplicationContext(), R.string.activity_backup_drive_success, Toast.LENGTH_SHORT).show();
    }

    private void showErrorDialog() {
        Toast.makeText(getApplicationContext(), R.string.activity_backup_drive_failed, Toast.LENGTH_SHORT).show();
    }

    private void reportToFirebase(Exception e, String message) {
        FirebaseCrash.log(message);
        FirebaseCrash.report(e);
    }
}
