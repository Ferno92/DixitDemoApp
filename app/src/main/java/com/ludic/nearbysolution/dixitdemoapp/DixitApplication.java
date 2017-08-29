package com.ludic.nearbysolution.dixitdemoapp;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

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
import com.google.android.gms.nearby.connection.Strategy;

import java.util.HashMap;

import static com.ludic.nearbysolution.dixitdemoapp.PlayersDataActivity.USERNAME;
import static com.ludic.nearbysolution.dixitdemoapp.PlayersDataActivity.USER_DATA;

/**
 * Created by luca.fernandez on 28/08/2017.
 */

public class DixitApplication extends Application {
    private static GoogleApiClient mGoogleApiClient;
    private static Context context;
    private static DixitAppListener mListener;
    private boolean type = false;

    private enum ConnectionType {
        Advertiser,
        Discoverer
    }

    ;
    private static GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            if(mListener != null){
                mListener.onConnected(bundle);
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
            if(mListener != null){
                mListener.onConnectionSuspended(i);
            }
        }
    };

    private static GoogleApiClient.OnConnectionFailedListener connectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }
    };

    private static ConnectionLifecycleCallback mConnectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(String discovererId, ConnectionInfo connectionInfo) {
            if(mListener != null){
                mListener.onConnectionInitiated(discovererId, connectionInfo);
            }

        }

        @Override
        public void onConnectionResult(String discovererId, ConnectionResolution result) {
            if(mListener != null){
                mListener.onConnectionResult(discovererId, result);
            }
        }

        @Override
        public void onDisconnected(String s) {
            if(mListener != null){
                mListener.onDisconnected(s);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        context = this.getApplicationContext();
        if (mGoogleApiClient == null && !getSharedPreferences(USER_DATA, 0).getString(USERNAME, "").isEmpty()) {
            initGoogleApiClient();
        } else if (getSharedPreferences(USER_DATA, 0).getString(USERNAME, "").isEmpty()) {
            //do nothing
        }
    }

    protected static void initGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(connectionFailedListener)
                .addApi(Nearby.CONNECTIONS_API)
                .setAccountName(context.getSharedPreferences(USER_DATA, 0).getString(USERNAME, ""))
                .build();
    }

    protected static GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    protected static ConnectionLifecycleCallback getConnectionLifecycleCallback(){
        return mConnectionLifecycleCallback;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            //Nearby.Connections.stopAdvertising(mGoogleApiClient); is advertising or discovering, eject?
            mGoogleApiClient.disconnect();
        }
    }

    protected static void setDixitListener(DixitAppListener listener){
        mListener = listener;
    }


    public interface DixitAppListener {
        void onConnectionInitiated(String discovererId, ConnectionInfo connectionInfo);

        void onConnectionResult(String discovererId, ConnectionResolution result);

        void onDisconnected(String discovererId);

        void onConnected(@Nullable Bundle bundle);

        void onConnectionSuspended(int i);

        void onConnectionFailed(@NonNull ConnectionResult connectionResult);
    }

}
