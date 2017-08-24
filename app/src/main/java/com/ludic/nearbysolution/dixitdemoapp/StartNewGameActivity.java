package com.ludic.nearbysolution.dixitdemoapp;

import android.*;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;

/**
 * Created by luca.fernandez on 24/08/2017.
 */

public class StartNewGameActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private GoogleApiClient mGoogleApiClient;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private TextView mResultTextView;

    private PayloadCallback mPayloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(String s, Payload payload) {

        }

        @Override
        public void onPayloadTransferUpdate(String s, PayloadTransferUpdate payloadTransferUpdate) {

        }
    };
    private ConnectionLifecycleCallback mConnectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
            Log.d("FERNO", "onConnectionInitiated");

            mResultTextView.setText("CONNECTING..");
            Nearby.Connections.acceptConnection(
                    mGoogleApiClient, endpointId, mPayloadCallback);

        }

        @Override
        public void onConnectionResult(String endpointId, ConnectionResolution result) {
            Log.d("FERNO", "onConnectionResult");
            switch (result.getStatus().getStatusCode()) {
                case ConnectionsStatusCodes.STATUS_OK:
                    // We're connected! Can now start sending and receiving data.
                    mResultTextView.setText("CONNECTED!!!");
                    break;
                case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                    // The connection was rejected by one or both sides.
                    mResultTextView.setText("REJECTED!!!");
                    break;
            }
        }

        @Override
        public void onDisconnected(String s) {
            Log.d("FERNO", "onDisconnected");

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_new_game_activity);
        mResultTextView = (TextView) findViewById(R.id.result_text);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.CONNECTIONS_API)
                .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check 
            if (this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect other device.");
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        ActivityCompat.requestPermissions(StartNewGameActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }
    }

    private void startAdvertising() {
        Nearby.Connections.startAdvertising(
                mGoogleApiClient,
                "SUPER_HUB", //getUserNickname(),
                "com.ludic.nearbysolution.dixitdemoapp",//SERVICE_ID,
                mConnectionLifecycleCallback,
                new AdvertisingOptions(Strategy.P2P_STAR))
                .setResultCallback(
                        new ResultCallback<Connections.StartAdvertisingResult>() {
                            @Override
                            public void onResult(@NonNull Connections.StartAdvertisingResult result) {
                                if (result.getStatus().isSuccess()) {
                                    // We're advertising!
                                    mResultTextView.setVisibility(View.VISIBLE);
                                } else {
                                    // We were unable to start advertising.
                                }
                            }
                        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("FERNO", "onConnected");

        startAdvertising();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("FERNO", "onConnectionSuspended");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("FERNO", "onConnectionFailed");

    }

    @Override
    protected void onDestroy() {
        Log.e("FERNO", "ON DESTROY START NEW GAME ACT");
        Nearby.Connections.stopAdvertising(mGoogleApiClient);
        super.onDestroy();
    }
}
