package com.ludic.nearbysolution.dixitdemoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.ludic.nearbysolution.dixitdemoapp.R;

/**
 * Created by luca.fernandez on 25/08/2017.
 */

public class SplashActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(getSharedPreferences(PlayersDataActivity.USER_DATA, 0) == null){
                    askPlayersDataActivity();
                }else{
                    if(getSharedPreferences(PlayersDataActivity.USER_DATA, 0).getString(PlayersDataActivity.USERNAME, "").isEmpty()){
                        askPlayersDataActivity();
                    }else{
                        startHomeActivity();
                    }
                }
            }
        }, 1000);
    }

    public void startHomeActivity(){

        // Start home activity
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        // close splash activity
        finish();
    }
    public void askPlayersDataActivity(){

        // Start home activity
        startActivity(new Intent(SplashActivity.this, PlayersDataActivity.class));
        // close splash activity
        finish();
    }
}