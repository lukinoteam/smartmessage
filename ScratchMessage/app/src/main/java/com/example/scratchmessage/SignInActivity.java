package com.example.scratchmessage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.stetho.Stetho;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import service.FireBaseService;


public class SignInActivity extends AppCompatActivity {

    final int RC_SIGN_IN = 123;

    SignInButton btnSignIn;

    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount account;

    private String userName;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Stetho.initializeWithDefaults(this);

        btnSignIn = findViewById(R.id.btnSignIn);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        sharedPreferences = getSharedPreferences("SHARED_PREFERENCES_ACCOUNT_SESSION", Context.MODE_PRIVATE);

        Intent firebaseInsertMessage = new Intent(SignInActivity.this, FireBaseService.class);
        startService(firebaseInsertMessage);
    }

    @Override
    protected void onStart() {
        super.onStart();

        account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null)
            updateUI(account);
    }

    public void generateUserName(GoogleSignInAccount acc) {
        String email = acc.getEmail();

        String[] emailSplit = email.split("@");
        String[] hostSplit = emailSplit[1].split("\\.");
        userName = emailSplit[0] + "-" + hostSplit[0];
    }

    public void updateUI(GoogleSignInAccount acc) {
        generateUserName(acc);

        editor = sharedPreferences.edit();
        if (acc.getPhotoUrl() == null) {
            editor.putString("avaUriSession", "default");
        } else {
            editor.putString("avaUriSession", String.valueOf(acc.getPhotoUrl()));
        }
        editor.putString("userNameSession", userName);
        editor.putString("emailSession", acc.getEmail());
        editor.putString("idSession", acc.getId());
        editor.putBoolean("onlineSession", false);
        editor.apply();

        Intent retrieveData = new Intent("retrieveData");
        sendBroadcast(retrieveData);

        Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            if (account != null) {
                signUpToDatabase(account);
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("testerror", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void signUpToDatabase(GoogleSignInAccount acc) {
        updateUI(account);


        Intent signUpIntent = new Intent("signUpGoogleAccount");
        sendBroadcast(signUpIntent);
    }
}
