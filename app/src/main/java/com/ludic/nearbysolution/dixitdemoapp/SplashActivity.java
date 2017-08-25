package com.ludic.nearbysolution.dixitdemoapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import static com.ludic.nearbysolution.dixitdemoapp.PlayersDataActivity.USERNAME;
import static com.ludic.nearbysolution.dixitdemoapp.PlayersDataActivity.USER_DATA;

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

                if(getSharedPreferences(USER_DATA, 0) == null){
                    askPlayersDataActivity();
                }else{
                    if(getSharedPreferences(USER_DATA, 0).getString(USERNAME, "").isEmpty()){
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