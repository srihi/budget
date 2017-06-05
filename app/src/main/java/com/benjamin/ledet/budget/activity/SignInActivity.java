package com.benjamin.ledet.budget.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

        //set default preferences at the first launch of the application
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

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
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent,9001);
    }

    //create the user with the google's account information
    private void handleSignInResult(GoogleSignInResult result) {
        //Storing the information of the user
        if(result.isSuccess()){
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null){

                User user = new User();
                user.setId(databaseHandler.getUserNextKey());
                user.setEmail(acct.getEmail());
                user.setGivenName(acct.getGivenName());
                user.setFamilyName(acct.getFamilyName());
                if(acct.getPhotoUrl() != null){
                    user.setPhotoUrl(acct.getPhotoUrl().toString());
                }
                databaseHandler.addUser(user);
                sharedPreferences.edit().putBoolean("first_launch",false).apply();
                //start main activity
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
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
}
