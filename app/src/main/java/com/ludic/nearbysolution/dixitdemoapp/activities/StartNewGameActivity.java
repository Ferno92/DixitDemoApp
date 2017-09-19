package com.ludic.nearbysolution.dixitdemoapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.ludic.nearbysolution.dixitdemoapp.R;
import com.ludic.nearbysolution.dixitdemoapp.application.DixitApplication;
import com.ludic.nearbysolution.dixitdemoapp.interfaces.DixitAppListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by luca.fernandez on 24/08/2017.
 */

public class StartNewGameActivity extends BaseActivity implements DixitAppListener {

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private TextView mResultTextView;
    private ListView mDiscovererListView;
    private ListView mPlayersListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    List<HashMap<String, String>> discovererListItems = new ArrayList<HashMap<String, String>>();
    List<HashMap<String, String>> playersListItems = new ArrayList<HashMap<String, String>>();

    // Create the item mapping
    String[] from = new String[]{"id", "name"};
    int[] to = new int[]{R.id.endpoint_id, R.id.endpoint_name};

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    private SimpleAdapter discovererAdapter;
    private SimpleAdapter playersAdapter;

    @Override
    public void onConnectionInitiated(String discovererId, ConnectionInfo connectionInfo) {
        Log.d("FERNO", "onConnectionInitiated discovererId: " + discovererId);

        mResultTextView.setText("CONNECTING..");

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("id", discovererId);
        map.put("name", connectionInfo.getEndpointName());

        discovererListItems.add(map);
        discovererAdapter.notifyDataSetChanged();

//
//            Nearby.Connections.acceptConnection(
//                    mGoogleApiClient, endpointId, mPayloadCallback);

    }

    @Override
    public void onConnectionResult(String discovererId, ConnectionResolution result) {
        switch (result.getStatus().getStatusCode()) {
            case ConnectionsStatusCodes.STATUS_OK:
                // We're connected! Can now start sending and receiving data.
                HashMap row = null;
                mResultTextView.setText("CONNECTED!!!");
                for (int i = 0; i < discovererListItems.size(); i++) {
                    row = discovererListItems.get(i);
                    if (row.get("id") == discovererId) {
                        break;
                    }
                }
                if (row != null) {
                    playersListItems.add(row);
                    playersAdapter.notifyDataSetChanged();
                    discovererListItems.remove(row);
                    discovererAdapter.notifyDataSetChanged();
                }
                break;
            case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                // The connection was rejected by one or both sides.
                mResultTextView.setText("REJECTED!!!");
                break;
        }
    }

    @Override
    public void onDisconnected(String deviceId) {
        Log.d("FERNO", "onDisconnected");

        for(int i = 0; i < playersListItems.size(); i++){
            if(playersListItems.get(i).get("id").equals(deviceId)){
                Log.d("FERNO", "disconnected item: " + playersListItems.get(i).get("name"));
                playersListItems.remove(i);
                playersAdapter.notifyDataSetChanged();
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_room_activity);
        TextView hubName = (TextView) findViewById(R.id.hub_name);
        hubName.setText(hubName.getText().toString() + getSharedPreferences(PlayersDataActivity.USER_DATA, 0).getString(PlayersDataActivity.USERNAME, "") + " - ");// + id
        mResultTextView = (TextView) findViewById(R.id.result_text);
        if (DixitApplication.getGoogleApiClient() == null) {
            DixitApplication.initGoogleApiClient();
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
        discovererAdapter = new SimpleAdapter(this, discovererListItems, R.layout.device_list_item, from, to);

        mDiscovererListView = (ListView) findViewById(R.id.discoverer_list);
        mDiscovererListView.setAdapter(discovererAdapter);
        mDiscovererListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap map = discovererListItems.get(position);
                String discovererId = map.get("id").toString();


                Nearby.Connections.acceptConnection(
                        DixitApplication.getGoogleApiClient(), discovererId, DixitApplication.getPayloadCallback());

            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh_discoverer);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (DixitApplication.getGoogleApiClient().isConnected()) {
                    DixitApplication.getGoogleApiClient().disconnect();
                    DixitApplication.getGoogleApiClient().connect();
                } else {
                    DixitApplication.getGoogleApiClient().connect();
                }

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        SwipeRefreshLayout swiperefreshPlayer = (SwipeRefreshLayout) findViewById(R.id.swiperefresh_player);
        swiperefreshPlayer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //TODO: refresh users logged in
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        //players

        playersAdapter = new SimpleAdapter(this, playersListItems, R.layout.device_list_item, from, to);

        mPlayersListView = (ListView) findViewById(R.id.players_list);
        mPlayersListView.setAdapter(playersAdapter);

        DixitApplication.setDixitListener(this);

        Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(playersListItems != null && playersListItems.size() > 0) {
                    DixitApplication.saveConnectedUsers(playersListItems);

                    Intent i = new Intent(StartNewGameActivity.this, GameInstanceActivity.class);
                    startActivity(i);
                }
            }
        });
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


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("FERNO", "onConnected");
        ConnectionResult result = DixitApplication.getGoogleApiClient().getConnectionResult(Nearby.CONNECTIONS_API);
        String error = result.getErrorMessage();
        Log.d("FERNO", "error: " + error);

        startAdvertising();
    }

    private void startAdvertising() {
        Nearby.Connections.startAdvertising(
                DixitApplication.getGoogleApiClient(),
                getSharedPreferences(PlayersDataActivity.USER_DATA, 0).getString(PlayersDataActivity.USERNAME, ""), //getUserNickname(),
                "com.ludic.nearbysolution.dixitdemoapp",//SERVICE_ID,
                DixitApplication.getConnectionLifecycleCallback(),
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
        Log.e("FERNO", "ON DESTROY START NEW GAME ACT");
        super.onDestroy();
    }
}
