package com.smartmusic.android.smartmusicplayer;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.smartmusic.android.smartmusicplayer.database.SPDatabase;
import com.smartmusic.android.smartmusicplayer.database.entities.Song;

/**
 * Created by holle on 12/14/2017.
 */

public class SplashActivity extends AppCompatActivity implements SongEventListener{

//    protected void onCreate(Bundle savedInstanceState){
//        super.onCreate(savedInstanceState);
//        Intent intent = new Intent(this, SPMainActivity.class);
//        startActivity(intent);
//        finish();
//    }

    private SongEventHandler mHandler = new SongEventHandler();

    private ProgressBar mProgress;
    private TextView mProgressDescription;
    private boolean mServiceBound = false;
    private int maxSongs = 0;
    private Handler mProcessHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler.addSongEventListener(this);

        // Show the splash screen
        setContentView(R.layout.splash_screen);
        mProgress = (ProgressBar) findViewById(R.id.splash_screen_progress_bar);
        mProgressDescription = (TextView) findViewById(R.id.splash_screen_progress_description);
    }

    private void doWork() {
        for (int progress=0; progress<100; progress+=10) {
            try {
                Thread.sleep(1000);
                mProgress.setProgress(progress);
            } catch (Exception e) {
                e.printStackTrace();
//                Timber.e(e.getMessage());
            }
        }
    }

    private void startApp() {
        Intent intent = new Intent(SplashActivity.this, SPMainActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();
        checkUserPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void checkUserPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){ // SDK 23
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},123);
                return;
            } else if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 456);
            }
        }
//        new SPRepository(this);
        initalizeDatabase();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length == 0) {
            return;
        }
        switch (requestCode){
            case 123:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    new SPRepository(this);
                    initalizeDatabase();
                }else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    checkUserPermission();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }

    }

    private void initalizeDatabase(){
        SPDatabase db = SPDatabase.getDatabase(this);
        if(SPDatabase.doesDatabaseExist(this, SPDatabase.DATABASE_NAME)){
            startApp();
        } else {
            maxSongs = SPDatabase.getTotalSongCount(this);
            mProgress.setMax(maxSongs);
            mProgress.setProgress(0);
        }
    }

    @Override
    public void onSongChangeEvent(SongEvent e) {}
    @Override
    public void onShuffleOnEvent(SongEvent e) {}
    @Override
    public void onShuffleOffEvent(SongEvent e) {}
    @Override
    public void onSongStopEvent(SongEvent e) {}

    @Override
    public void onSongAddedEvent(final SongEvent e){
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
    public void onSongRemovedEvent(SongEvent e) {

    }
}
