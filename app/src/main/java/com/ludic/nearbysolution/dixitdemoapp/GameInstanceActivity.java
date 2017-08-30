package com.ludic.nearbysolution.dixitdemoapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by luca.fernandez on 30/08/2017.
 */

public class GameInstanceActivity extends  BaseActivity implements DixitApplication.DixitAppListener {

    private List<String> playersIdList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_instance_activity);
        DixitApplication.setDixitListener(this);
        List<HashMap<String, String>> playersList = DixitApplication.getConnectedUsers();
        for(int i = 0; i < playersList.size(); i++){
            playersIdList.add(playersList.get(i).get("id"));
        }

        Button sendPayload = (Button)findViewById(R.id.send_payload);
        sendPayload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "Hi, players! Welcome to Dixit App";
                byte[] bytes = new byte[0];
                try {
                    bytes = text.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.e("FERNO", "Exception: " + e.getMessage());
                }
                Nearby.Connections.sendPayload(DixitApplication.getGoogleApiClient() ,playersIdList, Payload.fromBytes(bytes));
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
