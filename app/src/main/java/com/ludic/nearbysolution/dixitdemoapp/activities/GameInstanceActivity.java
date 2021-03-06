package com.ludic.nearbysolution.dixitdemoapp.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.ludic.nearbysolution.dixitdemoapp.adapters.ImageAdapter;
import com.ludic.nearbysolution.dixitdemoapp.R;
import com.ludic.nearbysolution.dixitdemoapp.application.DixitApplication;
import com.ludic.nearbysolution.dixitdemoapp.interfaces.DixitAppListener;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luca.fernandez on 30/08/2017.
 */

public class GameInstanceActivity extends BaseActivity implements DixitAppListener {

    private List<String> playersIdList = new ArrayList<>();
    private ListView mPlayersListView;
    private SimpleAdapter playersAdapter;
    private ImageView mDeckImage;
    private LinearLayout mDeckLayout;
    private GridView mGridView;
    private boolean mCardSent = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_instance_activity);
        DixitApplication.setDixitListener(this);
        for (int i = 0; i < DixitApplication.getConnectedUsers().size(); i++) {
            playersIdList.add(DixitApplication.getConnectedUsers().get(i).get("id"));
        }

        // Create the item mapping
        String[] from = new String[]{"id", "name"};
        int[] to = new int[]{R.id.endpoint_id, R.id.endpoint_name};
        playersAdapter = new SimpleAdapter(this, DixitApplication.getConnectedUsers(), R.layout.device_list_item, from, to);

        mPlayersListView = (ListView) findViewById(R.id.players_list);
        mPlayersListView.setAdapter(playersAdapter);

        mDeckLayout = (LinearLayout)findViewById(R.id.deck_layout);

        //gridview
        mGridView = (GridView) findViewById(R.id.gridview);
        mGridView.setAdapter(new ImageAdapter(this));

        //deck
        mDeckImage = (ImageView)findViewById(R.id.deck);
        mDeckImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!mCardSent) {
                    mCardSent = true;
                    String text = "Hi, players! Welcome to Dixit App";
                    byte[] bytes = new byte[0];
                    try {
                        bytes = text.getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        Log.e("FERNO", "Exception: " + e.getMessage());
                    }
                    Nearby.Connections.sendPayload(DixitApplication.getGoogleApiClient(), playersIdList, Payload.fromBytes(bytes));
                    mDeckLayout.setVisibility(View.GONE);
                    mGridView.setVisibility(View.VISIBLE);


                }
            }
        });
    }

    @Override
    public void onConnectionInitiated(String discovererId, ConnectionInfo connectionInfo) {

    }

    @Override
    public void onConnectionResult(String discovererId, ConnectionResolution result) {

    }

    @Override
    public void onDisconnected(String discovererId) {
        Log.d("FERNO", "onDisconnected");

        for(int i = 0; i < DixitApplication.getConnectedUsers().size(); i++){
            if(DixitApplication.getConnectedUsers().get(i).get("id").equals(discovererId)){
                Log.d("FERNO", "disconnected item: " + DixitApplication.getConnectedUsers().get(i).get("name"));
                mPlayersListView.getChildAt(i).setEnabled(false);
                mPlayersListView.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                //TODO: create custom adapter to disable list item when disconnected
            }
        }


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPayloadReceived(String endpointId, Payload payload) {
        Log.w("FERNO", "payload received from endpoint: " + endpointId);

    }

    @Override
    public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate payloadTransferUpdate) {
        Log.w("FERNO", "payload transfering for endpoint: " + endpointId + " payloadTransferUpdate: " + payloadTransferUpdate.getBytesTransferred() + " / " + payloadTransferUpdate.getTotalBytes());

    }
}
