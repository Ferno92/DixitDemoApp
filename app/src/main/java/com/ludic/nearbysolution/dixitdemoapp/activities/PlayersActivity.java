package com.ludic.nearbysolution.dixitdemoapp.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.ludic.nearbysolution.dixitdemoapp.PagerAdapterFragment;
import com.ludic.nearbysolution.dixitdemoapp.R;
import com.ludic.nearbysolution.dixitdemoapp.ZoomOutPageTransformer;
import com.ludic.nearbysolution.dixitdemoapp.adapters.ScreenSlidePagerAdapter;
import com.ludic.nearbysolution.dixitdemoapp.application.DixitApplication;
import com.ludic.nearbysolution.dixitdemoapp.interfaces.DixitAppListener;
import com.ludic.nearbysolution.dixitdemoapp.interfaces.SlideListener;

import java.io.UnsupportedEncodingException;

/**
 * Created by luca.fernandez on 28/08/2017.
 */

public class PlayersActivity extends BaseActivity implements DixitAppListener, SlideListener{

    private TextView mPayloadMessage;
    private RelativeLayout mPagerLayout;
    private ViewPager mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.players_activity);
        mPayloadMessage = (TextView) findViewById(R.id.payload_message);
        mPagerLayout = (RelativeLayout) findViewById(R.id.card_pager_layout);
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

        mPayloadMessage.setVisibility(View.GONE);
        mPagerLayout.setVisibility(View.VISIBLE);
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.card_pager);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), this);
        mPager.setAdapter(mPagerAdapter);


    }

    @Override
    public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate payloadTransferUpdate) {
        Log.w("FERNO", "payload transfering for endpoint: " + endpointId + " payloadTransferUpdate: " + payloadTransferUpdate.getBytesTransferred() + " / " + payloadTransferUpdate.getTotalBytes());

    }

    @Override
    public void sendCard(int position) {
        int cardId = mPagerAdapter.removeCard(position);
        //TODO: send card with that id
    }
}
