package com.ludic.nearbysolution.dixitdemoapp;

import android.*;
import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;

/**
 * Created by luca.fernandez on 24/08/2017.
 */

public class FindOpenGamesActivity extends AppCompatActivity implements
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
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect other device.");

                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        ActivityCompat.requestPermissions(FindOpenGamesActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }
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

    private final EndpointDiscoveryCallback mEndpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(
                        String endpointId, DiscoveredEndpointInfo discoveredEndpointInfo) {
                    // An endpoint was found!
                    mResultTextView.setVisibility(View.VISIBLE);
                    mResultTextView.setText("FOUND");
                    String name = "PIPPO";
                    Nearby.Connections.requestConnection(
                            mGoogleApiClient,
                            name,
                            endpointId,
                            mConnectionLifecycleCallback)
                            .setResultCallback(
                                    new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(@NonNull Status status) {
                                            if (status.isSuccess()) {
                                                // We successfully requested a connection. Now both sides
                                                // must accept before the connection is established.
                                                mResultTextView.setVisibility(View.VISIBLE);
                                                mResultTextView.setText("ASKING..");
                                            } else {
                                                // Nearby Connections failed to request the connection.
                                            }
                                        }
                                    });

                }

                @Override
                public void onEndpointLost(String endpointId) {
                    // A previously discovered endpoint has gone away.
                }
            };

    private void startDiscovery() {
        Nearby.Connections.startDiscovery(
                mGoogleApiClient,
                "com.ludic.nearbysolution.dixitdemoapp",
                mEndpointDiscoveryCallback,
                new DiscoveryOptions(Strategy.P2P_STAR))
                .setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                if (status.isSuccess()) {
                                    // We're discovering!
                                    mResultTextView.setVisibility(View.VISIBLE);
                                } else {
                                    // We were unable to start discovering.
                                }
                            }
                        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("FERNO", "onConnected");

        startDiscovery();
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
        Log.e("FERNO", "ON DESTROY DISCOVERY");
        Nearby.Connections.stopDiscovery(mGoogleApiClient);
        super.onDestroy();
    }
}
