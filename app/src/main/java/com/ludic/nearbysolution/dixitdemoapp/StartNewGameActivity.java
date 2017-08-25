package com.ludic.nearbysolution.dixitdemoapp;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.ludic.nearbysolution.dixitdemoapp.PlayersDataActivity.USERNAME;
import static com.ludic.nearbysolution.dixitdemoapp.PlayersDataActivity.USER_DATA;

/**
 * Created by luca.fernandez on 24/08/2017.
 */

public class StartNewGameActivity extends BaseActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private GoogleApiClient mGoogleApiClient;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private TextView mResultTextView;
    private ListView mDiscovererListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    List<HashMap<String, String>> listItems = new ArrayList<HashMap<String, String>>();

    // Create the item mapping
    String[] from = new String[]{"id", "name"};
    int[] to = new int[]{R.id.endpoint_id, R.id.endpoint_name};

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    private SimpleAdapter adapter;


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
            Log.d("FERNO", "onConnectionInitiated endpointId: " + endpointId);

            mResultTextView.setText("CONNECTING..");

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("id", endpointId);
            map.put("name", connectionInfo.getEndpointName());

            listItems.add(map);
            adapter.notifyDataSetChanged();

//
//            Nearby.Connections.acceptConnection(
//                    mGoogleApiClient, endpointId, mPayloadCallback);

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
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Nearby.CONNECTIONS_API)
                    .setAccountName(getSharedPreferences(USER_DATA, 0).getString(USERNAME, ""))
                    .build();
        }
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
        adapter = new SimpleAdapter(this, listItems, R.layout.device_list_item, from, to);
        mDiscovererListView = (ListView) findViewById(R.id.endpoint_list);
        mDiscovererListView.setAdapter(adapter);
        mDiscovererListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap map = listItems.get(position);
                String endpointId = map.get("id").toString();


                Nearby.Connections.acceptConnection(
                        mGoogleApiClient, endpointId, mPayloadCallback);

            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.disconnect();
                    mGoogleApiClient.connect();
                } else {
                    mGoogleApiClient.connect();
                }

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
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
                                    Log.d("FERNO", "We're advertising!");
                                    mResultTextView.setVisibility(View.VISIBLE);
                                } else {
                                    // We were unable to start advertising.
                                    Log.d("FERNO", "We were unable to start advertising.");

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
            Nearby.Connections.stopAdvertising(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("FERNO", "onConnected");
        ConnectionResult result = mGoogleApiClient.getConnectionResult(Nearby.CONNECTIONS_API);
        String error = result.getErrorMessage();
        Log.d("FERNO", "error: " + error);

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
        super.onDestroy();
    }
}
