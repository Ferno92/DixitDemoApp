package com.ludic.nearbysolution.dixitdemoapp.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.ludic.nearbysolution.dixitdemoapp.R;
import com.ludic.nearbysolution.dixitdemoapp.application.DixitApplication;
import com.ludic.nearbysolution.dixitdemoapp.interfaces.DixitAppListener;

import java.io.UnsupportedEncodingException;

/**
 * Created by luca.fernandez on 28/08/2017.
 */

public class PlayersActivity extends BaseActivity implements DixitAppListener {

    private TextView mPayloadMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.players_activity);
        mPayloadMessage = (TextView)findViewById(R.id.payload_message);
        DixitApplication.setDixitListener(this);
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
        byte[] payloadBytes = payload.asBytes();
        String text = "";
        try {
            text = new String(payloadBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e("FERNO", "Exception: " + e.getMessage());
        }
        mPayloadMessage.setText(text);


    }

    @Override
    public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate payloadTransferUpdate) {
        Log.w("FERNO", "payload transfering for endpoint: " + endpointId + " payloadTransferUpdate: " + payloadTransferUpdate.getBytesTransferred() + " / " + payloadTransferUpdate.getTotalBytes());

    }
}
