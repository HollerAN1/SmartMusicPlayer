package com.smartmusic.android.smartmusicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by holle on 12/14/2017.
 */

public class SplashActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, SPMainActivity.class);
        startActivity(intent);
        finish();
    }
}
