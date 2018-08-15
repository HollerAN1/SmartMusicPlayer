package com.smartmusic.android.smartmusicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.smartmusic.android.smartmusicplayer.model.SongInfo;

import java.util.concurrent.TimeoutException;

import timber.log.Timber;

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
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SPDatabaseService.SPDatabaseBinder mBinder = (SPDatabaseService.SPDatabaseBinder) service;
            mService = mBinder.getService();
            mServiceBound = true;

            startLoadingDatabase();
        }
    };

    private SongEventHandler mHandler = new SongEventHandler();

    private ProgressBar mProgress;
    private TextView mProgressDescription;
    private boolean mServiceBound = false;
    private SPDatabaseService mService;
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

        // Start lengthy operation in a background thread
//        new Thread(new Runnable() {
//            public void run() {
//                doWork();
//                startApp();
//                finish();
//            }
//        }).start();
//        mProgress.setMax(100);
//        mProgress.setProgress(0);
    }

    private void doWork() {
        for (int progress=0; progress<100; progress+=10) {
            try {
                Thread.sleep(1000);
                mProgress.setProgress(progress);
            } catch (Exception e) {
                e.printStackTrace();
                Timber.e(e.getMessage());
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

        // Bind to SPDatabaseService
        Intent intent = new Intent(this, SPDatabaseService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mServiceBound) {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }
    }

    private void createHandler(){

    }

    private void startLoadingDatabase(){
        maxSongs = mService.getTotalSongCount();
        mProgress.setMax(maxSongs);
        mProgress.setProgress(0);

        mService.loadDatabase();
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
                SongInfo s = e.getSource();
                mProgress.incrementProgressBy(1);

                if(s != null) {
                    mProgressDescription.setText(s.getSongname());
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
