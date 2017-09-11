package com.ludic.nearbysolution.dixitdemoapp.interfaces;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;

/**
 * Created by luca.fernandez on 11/09/2017.
 */

public interface DixitAppListener {
    //TODO: suddividere in pù listener

    void onConnectionInitiated(String discovererId, ConnectionInfo connectionInfo);

    void onConnectionResult(String discovererId, ConnectionResolution result);

    void onDisconnected(String discovererId);

    void onConnected(@Nullable Bundle bundle);

    void onConnectionSuspended(int i);

    void onConnectionFailed(@NonNull ConnectionResult connectionResult);

    void onPayloadReceived(String s, Payload payload);

    void onPayloadTransferUpdate(String s, PayloadTransferUpdate payloadTransferUpdate);
}
