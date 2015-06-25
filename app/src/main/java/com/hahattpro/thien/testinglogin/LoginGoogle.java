package com.hahattpro.thien.testinglogin;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.plus.Plus;


public class LoginGoogle extends Activity {

    GoogleApiClient mGoogleApiClient;
    GoogleApiClient.ConnectionCallbacks callbacks;
    GoogleApiClient.OnConnectionFailedListener connectionFailedListener;
    int GOOGLE_DRIVE_LOGIN_REQUEST_CODE = 101;
    String GOOGLEDRIVE_LOG_TAG = "GOOGLE DRIVE";
    int ACCOUNT_PICKER_REQUEST_CODE = 102;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_google);
        button = (Button) findViewById(R.id.mButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleAccountPicker();
            }
        });

        callbacks = new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                Log.i(GOOGLEDRIVE_LOG_TAG,"onConnected");
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.i(GOOGLEDRIVE_LOG_TAG,"onConnectionSuspended");
            }
        };

        connectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                if (connectionResult.hasResolution()) {
                    try {
                        //For first login when user choose account then ask for permission
                        //must call onActivityResult
                        Log.i(GOOGLEDRIVE_LOG_TAG,"onConnection failed has resolution");
                        connectionResult.startResolutionForResult(LoginGoogle.this, GOOGLE_DRIVE_LOGIN_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        // Unable to resolve, message user appropriately
                        Log.i(GOOGLEDRIVE_LOG_TAG, "something wrong");
                        e.printStackTrace();
                    }
                } else {
                    GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), LoginGoogle.this, 0).show();
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_DRIVE_LOGIN_REQUEST_CODE)
            if (resultCode == RESULT_OK)
                mGoogleApiClient.connect();
        if (requestCode == ACCOUNT_PICKER_REQUEST_CODE)
        {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            Log.i(GOOGLEDRIVE_LOG_TAG,"result account = "+accountName);
            LoginGoogleApi(accountName);
        }
    }


    ///////////////
    ////////////
    /////////
    public void LoginGoogleApi(String AccountName)
    {
        Log.i(GOOGLEDRIVE_LOG_TAG,"set account name " + AccountName);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .setAccountName(AccountName)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(callbacks)
                .addOnConnectionFailedListener(connectionFailedListener)
                .build();
        //mGoogleApiClient.clearDefaultAccountAndReconnect();
        mGoogleApiClient.connect();
    }

    public void GoogleAccountPicker()
    {
        Log.i(GOOGLEDRIVE_LOG_TAG,"account picker");
        Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
                false, null, null, null, null);
        startActivityForResult(intent, ACCOUNT_PICKER_REQUEST_CODE);
    }
}
