package com.smartmusic.android.smartmusicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.smartmusic.android.smartmusicplayer.model.SongInfo;

import java.util.ArrayList;
import java.util.Random;

/**
 * A Service that handles the lifecycle
 * of the media player, shuffle functionality
 * and switching between songs.
 *
 * Created by holle on 7/1/2018.
 */

public class SongPlayerService
        extends Service
        implements MediaPlayer.OnPreparedListener,
                   MediaPlayer.OnErrorListener,
                   MediaPlayer.OnCompletionListener
{

    private Handler myHandler = new Handler();
    private SongInfo songInfo;
    private static MediaPlayer mediaPlayer;
    private Random random = new Random();


    // Binder given to clients
    private boolean mBound = false;
    private final IBinder mBinder = new SongPlayerBinder();

    //The list of songs to play from
    private ArrayList<SongInfo> songs;
    //The position of the current song
    private int pos = 0;
    private boolean shuffleOn = false;

    public SongPlayerService(){
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(getString(R.string.APP_LOGGER),
                "SongPlayerService started");
        mediaPlayer = new MediaPlayer();
        initPlayer();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(getString(R.string.APP_LOGGER),
                "SongPlayerService stopped");
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(getString(R.string.APP_LOGGER),
                intent.getStringExtra(getString(R.string.EXTRA_SENDER)) + " bound to SongPlayerService");
        mBound = true;
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(getString(R.string.APP_LOGGER),
                intent.getStringExtra(getString(R.string.EXTRA_SENDER)) + " unbound from SongPlayerService");
        mBound = false;
        return super.onUnbind(intent);
    }

    public class SongPlayerBinder extends Binder {
        public SongPlayerService getService() {
            // Return this instance of SPDatabaseBinder so clients
            // can call public methods
            return SongPlayerService.this;
        }
    } // end SPDatabaseBinder

    public boolean isBound(){
        return this.mBound;
    }

    /*---------------------- Methods client can call --------------------------*/

    /**
     * Sets the initial properties for
     * the media player.
     */
    private void initPlayer(){
        mediaPlayer.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }

    /**
     * Sets the list of songs the mediaPlayer will
     * play from. It is up to the app to notify the
     * media player of list changes.
     * @param songList the list of songs.
     */
    public void setSongList(ArrayList<SongInfo> songList){
        this.songs = songList;
    }

    /**
     * Changes the shuffle state of the player.
     * @param on whether or not shuffle is on
     */
    public void setShuffle(boolean on){
        this.shuffleOn = on;
    }

    public boolean isShuffleOn(){
        return this.shuffleOn;
    }
    /**
     * Plays the given song. Searches through
     * the song list to determine the position
     * of the song in the list.
     * @param s song to play.
     */
    public void playSong(final SongInfo s){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (mediaPlayer.isPlaying()){
                        stop();
                    }

                    mediaPlayer.setDataSource(s.getSongUrl());
                    songInfo = s;

                    if( songs.contains(s) ){
                        pos = songs.indexOf(s);
                    } else {
                        // If this is the case we will still play the
                        // song but the logger will inform us that we
                        // have not updated the songs list to accurately depict
                        // the current state of the list visible to the user.
                        pos = 0;
                        Log.e(getString(R.string.APP_LOGGER), "MediaPlayer is in an inconsistent state");
                    }

                    mediaPlayer.prepareAsync();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        myHandler.postDelayed(runnable, 100);
    }


    public void pause(){
        mediaPlayer.pause();
    }

    public void resume(){
        mediaPlayer.start();
    }

    /**
     * Stops the current song and
     * sends out an event informing
     * the app of this action.
     */
    public void stop(){
        mediaPlayer.stop();
        mediaPlayer.reset();
        SPMainActivity.getSongEventHandler().dispatchEvent(getSongStopEvent(songInfo));

        songInfo = null;
    }

    public SongInfo getCurrentSong(){
        return this.songInfo;
    }

    public MediaPlayer getMediaPlayer(){
        return this.mediaPlayer;
    }

    public boolean isSongPlaying(){
        return ( mediaPlayer != null ) && mediaPlayer.isPlaying();
    }

    public boolean isMediaPlayerSet(){
        return mediaPlayer != null;
    }

    /**
     * Selects and plays the next song
     * in the list.
     *
     * If shuffle is on, plays a random song.
     */
    public void playNextSong(){
        mediaPlayer.stop();
        mediaPlayer.reset();

        if( !shuffleOn ) {
            songInfo = songs.get((pos != songs.size() - 1)
                    ? (pos + 1) // Get song after
                    : 0); // Loop around to beginning of list
        } else {
            songInfo = getRandomSong();
        }

        if(songInfo != null){
            playSong(songInfo);
        } else {
            stop();
        }
    }

    /**
     * Selects and plays the previous song
     * in the list.
     *
     * If shuffle is on, plays a random song.
     */
    public void playPreviousSong(){
        mediaPlayer.stop();
        mediaPlayer.reset();

        if( !shuffleOn ) {
            songInfo = songs.get((pos != 0)
                    ? (pos - 1)  // Get song before
                    : songs.size() - 1); // Loop around to end of list
        } else {
            songInfo = getRandomSong();
        }

        if(songInfo != null){
            playSong(songInfo);
        } else {
            stop();
        }
    }

    /**
     * Shuffle functionality. Gets a random song
     * from the list.
     * @return the random song.
     */
    private SongInfo getRandomSong(){
        // TODO: Implement a weighted random algorithm to more likely
        // choose those songs the user most listens to.
        SongInfo randSong = songs.get(random.nextInt(songs.size() - 1));
        return randSong;
    }

    /**
     * Builds the event for when the mediaPlayer has
     * changed the song.
     * @param info the song the media player has changed to
     * @return An event that describes the song change.
     */
    private SongEvent getSongChangeEvent(SongInfo info){
        SongEvent event = new SongEvent(info, songs.indexOf(info), SongEvent.Type.SONG_CHANGED);
        return event;
    }

    /**
     * Builds the event for when the mediaPlayer has
     * stopped the current song.
     * @param info The song that was stopped
     * @return An event that describes the stopped song.
     */
    private SongEvent getSongStopEvent(SongInfo info) {
        SongEvent event = new SongEvent(info, songs.indexOf(info), SongEvent.Type.SONG_STOPPED);
        return event;
    }

    /*------------------------------- END methods client can call --------------------------------*/


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
//        playNextSong();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        SPMainActivity.getSongEventHandler().dispatchEvent(getSongChangeEvent(songInfo));
        mediaPlayer.start();
    }
} // end SongPlayerService
