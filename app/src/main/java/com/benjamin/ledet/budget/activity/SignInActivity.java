package com.benjamin.ledet.budget.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.benjamin.ledet.budget.BudgetApplication;
import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.model.DatabaseHandler;
import com.benjamin.ledet.budget.model.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignInActivity extends AppCompatActivity  implements GoogleApiClient.OnConnectionFailedListener  {

    @BindView(R.id.google_sign_in)
    Button googleSignIn;

    private DatabaseHandler databaseHandler;
    private SharedPreferences sharedPreferences;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        BudgetApplication budgetApplication = (BudgetApplication) getApplication();
        databaseHandler = budgetApplication.getDBHandler();
        sharedPreferences = budgetApplication.getPreferences();

        //build the google sign in api
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        //if there is a user, then he has asked to change account
                        if(databaseHandler.getUser() != null) {
                            signOut();

                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(this)
                .build();

        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mGoogleApiClient != null){
                    mGoogleApiClient.stopAutoManage(SignInActivity.this);
                    mGoogleApiClient.disconnect();
                }
                signIn();
            }
        });

    }

    //show the google sign in api
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent,9001);
    }

    //create the user with the google account information
    private void handleSignInResult(GoogleSignInResult result) {
        //Storing the information of the user
        if(result.isSuccess()){
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null){

                User user = new User();
                user.setId(1);
                user.setEmail(acct.getEmail());
                user.setGivenName(acct.getGivenName());
                user.setFamilyName(acct.getFamilyName());
                if(acct.getPhotoUrl() != null){
                    user.setPhotoUrl(acct.getPhotoUrl().toString());
                }
                databaseHandler.addUser(user);

                //start main activity
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

    //sign out user
    private void signOut() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.CustomAlertDialog);
        TextView title = new TextView(this);
        title.setText(R.string.activity_sign_in_change_account_title);
        title.setTextColor(ContextCompat.getColor(this,R.color.PrimaryColor));
        title.setGravity(Gravity.CENTER);
        title.setTextSize(22);
        builder.setCustomTitle(title);
        builder.setMessage(R.string.activity_sign_in_change_account_message);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                // ...
                            }
                        });
                revokeAccess();
                //reset backup information
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("backup_folder", "");
                editor.putLong("last_restore", 0);
                editor.apply();
                //delete the user
                databaseHandler.deleteUser(databaseHandler.getUser());

            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //disconnect account
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 9001) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.e("MainActivity", "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onBackPressed() {
        //disable the back button if the user come from the setting activity to prevent connection errors
        if(isTaskRoot()){
            super.onBackPressed();
        }else {
            Snackbar snackbar = Snackbar.make(googleSignIn , R.string.activity_sign_in_on_back_pressed , Snackbar.LENGTH_SHORT);
            snackbar.getView().setBackgroundColor(Color.RED);
            snackbar.show();
        }
    }
}
