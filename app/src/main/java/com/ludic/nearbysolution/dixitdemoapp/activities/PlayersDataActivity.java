package com.ludic.nearbysolution.dixitdemoapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ludic.nearbysolution.dixitdemoapp.R;

/**
 * Created by luca.fernandez on 25/08/2017.
 */

public class PlayersDataActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private Button saveButton;
    public static final String USER_DATA = "USER_DATA";
    public static final String USERNAME = "USERNAME";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.players_data_activity);

        usernameEditText = (EditText)findViewById(R.id.username_edit);
        saveButton = (Button)findViewById(R.id.save_user_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save user and go to main activity
                String username = usernameEditText.getText().toString();
                SharedPreferences settings = getSharedPreferences(USER_DATA, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(USERNAME, username);
                editor.commit();

                // Start home activity
                startActivity(new Intent(PlayersDataActivity.this, MainActivity.class));
                finish();


            }
        });
    }
}
