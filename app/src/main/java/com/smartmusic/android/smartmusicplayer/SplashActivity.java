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
    private boolean mServiceBound = false;
    private int maxSongs = 0;
    private Handler mProcessHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler.addSongDatabaseChangedListener(this);

        // Show the splash screen
        setContentView(R.layout.splash_screen);
        mProgress = (ProgressBar) findViewById(R.id.splash_screen_progress_bar);
        mProgressDescription = (TextView) findViewById(R.id.splash_screen_progress_description);
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
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        getResources().getInteger(R.integer.PERMISSION_READ_EXTERNAL_STORAGE_REQUEST));
//                return;
            }
//            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    != PackageManager.PERMISSION_GRANTED){
//                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 456);
//            }
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                        getResources().getInteger(R.integer.PERMISSION_RECORD_AUDIO_REQUEST));
            }
        }

        initializeDatabase();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length == 0) {
            return;
        }
        switch (requestCode){
            case R.integer.PERMISSION_RECORD_AUDIO_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    new SPRepository(this);
                    initializeDatabase();
                }else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    checkUserPermissions();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }

    }

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

    }
}
