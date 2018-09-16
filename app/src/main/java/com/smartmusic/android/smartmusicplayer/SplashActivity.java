package com.smartmusic.android.smartmusicplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.smartmusic.android.smartmusicplayer.database.SPDatabase;
import com.smartmusic.android.smartmusicplayer.database.entities.Song;
import com.smartmusic.android.smartmusicplayer.events.SongDatabaseChangedListener;
import com.smartmusic.android.smartmusicplayer.events.SongDatabaseEvent;
import com.smartmusic.android.smartmusicplayer.events.SongPlaybackEvent;
import com.smartmusic.android.smartmusicplayer.events.SongEventHandler;

/**
 * The activity that controls the splash screen
 * presented to the user on initial startup.
 *
 * Created by holle on 12/14/2017.
 */

public class SplashActivity extends AppCompatActivity implements SongDatabaseChangedListener {

    private SongEventHandler mHandler = new SongEventHandler();

    private ProgressBar mProgress;
    private TextView mProgressDescription;
    private int maxSongs = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler.addSongDatabaseChangedListener(this);

        // Show the splash screen
        setContentView(R.layout.splash_screen);
        mProgress =                 findViewById(R.id.splash_screen_progress_bar);
        mProgressDescription =      findViewById(R.id.splash_screen_progress_description);
    }

    private void startApp() {
        Intent intent = new Intent(SplashActivity.this, SPMainActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();
        checkUserPermissions();
    }


    /**
     * Checks to make sure the app has all necessary permissions
     * otherwise, prompts the user for those permissions.
     */
    private void checkUserPermissions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){ // SDK 23
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                        100);
            }
            return;
        }

        initializeDatabase();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length == 0) {
            return;
        }
        switch (requestCode){
            case 100:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    initializeDatabase();
                }else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    startApp();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }

    }

    /**
     * Checks if the song database exists
     * if so, it starts the app. Otherwise
     * it goes through the process of loading
     * song into the database.
     */
    private void initializeDatabase(){
        SPDatabase.getDatabase(this); // initializes database
        if(SPDatabase.doesDatabaseExist(this, SPDatabase.DATABASE_NAME)){
            startApp();
        } else {
            maxSongs = SPDatabase.getTotalSongCount(this);
            mProgress.setMax(maxSongs);
            mProgress.setProgress(0);
        }
    }

    @Override
    public void onSongAddedEvent(final SongDatabaseEvent e){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Song s = e.getSource();
                mProgress.incrementProgressBy(1);

                if(s != null) {
                    mProgressDescription.setText(s.getSongName());
                }

                if( mProgress.getProgress() == maxSongs ) {
                    startApp();
                    finish();
                }
            }
        });
    }

    @Override
    public void onSongRemovedEvent(SongDatabaseEvent e) {
        // Song will never be removed on startup.
    }
}
