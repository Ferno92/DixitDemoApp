package com.ludic.nearbysolution.dixitdemoapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static com.ludic.nearbysolution.dixitdemoapp.PlayersDataActivity.USERNAME;
import static com.ludic.nearbysolution.dixitdemoapp.PlayersDataActivity.USER_DATA;

/**
 * Created by luca.fernandez on 24/08/2017.
 */

public class FindOpenGamesActivity extends BaseActivity implements
        DixitApplication.DixitAppListener {

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private TextView mResultTextView;
    private ListView mEndpointListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    List<HashMap<String, String>> listItems = new ArrayList<HashMap<String, String>>();

    // Create the item mapping
    String[] from = new String[]{"id", "name"};
    int[] to = new int[]{R.id.endpoint_id, R.id.endpoint_name};

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    private SimpleAdapter adapter;


    @Override
    public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
        Log.d("FERNO", "onConnectionInitiated endpointId: " + endpointId);

        mResultTextView.setText("CONNECTING..");
        Nearby.Connections.acceptConnection(
                DixitApplication.getGoogleApiClient(), endpointId, DixitApplication.getPayloadCallback());

    }

    @Override
    public void onConnectionResult(String endpointId, ConnectionResolution result) {
        Log.d("FERNO", "onConnectionResult");
        switch (result.getStatus().getStatusCode()) {
            case ConnectionsStatusCodes.STATUS_OK:
                // We're connected! Can now start sending and receiving data.
                mResultTextView.setText("CONNECTED!!!");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(FindOpenGamesActivity.this, PlayersActivity.class);
                        startActivity(i);
                        finish();
                    }
                }, 1000);
                break;
            case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                // The connection was rejected by one or both sides.
                mResultTextView.setText("REJECTED!!!");
                break;
            default:
                mResultTextView.setText("WHAT? CODE: " + result.getStatus().getStatusCode());
        }
    }

    @Override
    public void onDisconnected(String s) {
        Log.d("FERNO", "onDisconnected");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_games_activity);
        mResultTextView = (TextView) findViewById(R.id.result_text);
        if (DixitApplication.getGoogleApiClient() == null) {
            DixitApplication.initGoogleApiClient();
        }
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
        adapter = new SimpleAdapter(this, listItems, R.layout.device_list_item, from, to);
        mEndpointListView = (ListView) findViewById(R.id.endpoint_list);
        mEndpointListView.setAdapter(adapter);
        mEndpointListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap map = listItems.get(position);
                String endpointId = map.get("id").toString();

                String name = getSharedPreferences(USER_DATA, 0).getString(USERNAME, "NO-NAME");
                Nearby.Connections.requestConnection(
                        DixitApplication.getGoogleApiClient(),
                        name,
                        endpointId,
                        DixitApplication.getConnectionLifecycleCallback())
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
                                            Log.d("FERNO", "Nearby Connections failed to request the connection, status: " + status.getStatusCode());
                                            mResultTextView.setText("ERROR: " + status.getStatusCode());
                                            // Nearby Connections failed to request the connection. repeat?
                                        }
                                    }
                                });
            }
        });
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DixitApplication.initGoogleApiClient();

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        DixitApplication.setDixitListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        DixitApplication.getGoogleApiClient().connect();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private final EndpointDiscoveryCallback mEndpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(
                        String endpointId, DiscoveredEndpointInfo discoveredEndpointInfo) {
                    // An endpoint was found!
                    mResultTextView.setVisibility(View.VISIBLE);
                    mResultTextView.setText("FOUND");
//                    String name = getSharedPreferences(USER_DATA, 0).getString(USERNAME, "NO-NAME");
                    String endpointName = discoveredEndpointInfo.getEndpointName();

//                    discovererListItems.add("Endpoint: " + endpointName + " Click to connect");
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("id", endpointId);
                    map.put("name", endpointName);
                    listItems.add(map);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onEndpointLost(String endpointId) {
                    // A previously discovered endpoint has gone away.
                    Log.d("FERNO", "A previously discovered endpoint has gone away.");
                }
            };

    private void startDiscovery() {
        Nearby.Connections.startDiscovery(
                DixitApplication.getGoogleApiClient(),
                "com.ludic.nearbysolution.dixitdemoapp",
                mEndpointDiscoveryCallback,
                new DiscoveryOptions(Strategy.P2P_STAR))
                .setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                if (status.isSuccess()) {
                                    // We're discovering!
                                    Log.d("FERNO", "We're discovering!");
                                    mResultTextView.setVisibility(View.VISIBLE);
                                } else {
                                    Log.d("FERNO", "We were unable to start discovering.");
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
    public void onPayloadReceived(String s, Payload payload) {

    }

    @Override
    public void onPayloadTransferUpdate(String s, PayloadTransferUpdate payloadTransferUpdate) {

    }

    @Override
    protected void onDestroy() {
        Log.e("FERNO", "ON DESTROY DISCOVERY");
        super.onDestroy();
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
