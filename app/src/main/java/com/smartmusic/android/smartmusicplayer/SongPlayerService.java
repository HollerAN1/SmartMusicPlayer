package com.smartmusic.android.smartmusicplayer;

import android.arch.lifecycle.LifecycleService;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.smartmusic.android.smartmusicplayer.database.entities.Song;
import com.smartmusic.android.smartmusicplayer.events.SongEvent;

import java.util.List;
import java.util.Random;

/**
 * A Service that handles the lifecycle
 * of the media player, shuffle functionality
 * and switching between songs.
 *
 * Created by holle on 7/1/2018.
 */

public class SongPlayerService
        extends LifecycleService
        implements MediaPlayer.OnPreparedListener,
                   MediaPlayer.OnErrorListener,
                   MediaPlayer.OnCompletionListener
{

    private Handler myHandler = new Handler();
    private Song songModel;
    private static MediaPlayer mediaPlayer;
    private Random random = new Random();


    // Binder given to clients
    private boolean mBound = false;
    private final IBinder mBinder = new SongPlayerBinder();

    //The list of songs to play from
    private List<Song> songs;
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
        super.onBind(intent);
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
        Log.i(getString(R.string.APP_LOGGER), "Initializing media player");
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
    public void setSongList(List<Song> songList){
        Log.i(getString(R.string.APP_LOGGER), "SongPlayer song list updated");
        this.songs = songList;
    }

    /**
     * Changes the shuffle state of the player.
     * @param on whether or not shuffle is on
     */
    public void setShuffle(boolean on){
        Log.i(getString(R.string.APP_LOGGER), "Shuffle " + ( on ? "on" : "off"));
        this.shuffleOn = on;
        SPMainActivity.getSongEventHandler().dispatchEvent(getSongShuffleEvent(on));
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
    public void playSong(final Song s){
        Log.i(getString(R.string.APP_LOGGER), "Playing " + s.toString());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (mediaPlayer.isPlaying()){
                        stop();
                    }

                    mediaPlayer.setDataSource(s.getSongUrl());
                    songModel = s;

                    if( songs != null && songs.contains(s) ){
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
        Log.i(getString(R.string.APP_LOGGER), "SmartPlayer paused.");
        mediaPlayer.pause();
    }

    public void resume(){
        Log.i(getString(R.string.APP_LOGGER), "SmartPlayer resumed.");
        mediaPlayer.start();
    }

    /**
     * Stops the current song and
     * sends out an event informing
     * the app of this action.
     */
    public void stop(){
        Log.i(getString(R.string.APP_LOGGER), "SmartPlayer stopped.");
        mediaPlayer.stop();
        mediaPlayer.reset();
        SPMainActivity.getSongEventHandler().dispatchEvent(getSongStopEvent(songModel));

        songModel = null;
    }

    public Song getCurrentSong(){
        return this.songModel;
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
        Log.i(getString(R.string.APP_LOGGER), "Preparing next song.");
        mediaPlayer.stop();
        mediaPlayer.reset();

        if( !shuffleOn ) {
            songModel = songs.get((pos != songs.size() - 1)
                    ? (pos + 1) // Get song after
                    : 0); // Loop around to beginning of list
        } else {
            songModel = getRandomSong();
        }

        if(songModel != null){
            playSong(songModel);
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
        Log.i(getString(R.string.APP_LOGGER), "Preparing previous song.");
        mediaPlayer.stop();
        mediaPlayer.reset();

        if( !shuffleOn ) {
            songModel = songs.get((pos != 0)
                    ? (pos - 1)  // Get song before
                    : songs.size() - 1); // Loop around to end of list
        } else {
            songModel = getRandomSong();
        }

        if(songModel != null){
            playSong(songModel);
        } else {
            stop();
        }
    }

    /**
     * Shuffle functionality. Gets a random song
     * from the list.
     * @return the random song.
     */
    private Song getRandomSong(){
        // TODO: Implement a weighted random algorithm to more likely
        // choose those songs the user most listens to.
        Song randSong = songs.get(random.nextInt(songs.size() - 1));
        return randSong;
    }

    /**
     * Builds the event for when the mediaPlayer has
     * changed the song.
     * @param info the song the media player has changed to
     * @return An event that describes the song change.
     */
    private SongEvent getSongChangeEvent(Song info){
        SongEvent event = new SongEvent(info, songs.indexOf(info), SongEvent.Type.SONG_CHANGED);
        return event;
    }

    /**
     * Builds the event for when the mediaPlayer has
     * stopped the current song.
     * @param info The song that was stopped
     * @return An event that describes the stopped song.
     */
    private SongEvent getSongStopEvent(Song info) {
        SongEvent event = new SongEvent(info, songs.indexOf(info), SongEvent.Type.SONG_STOPPED);
        return event;
    }

    private SongEvent getSongShuffleEvent(boolean shuffleOn){
        // TODO: Create a unique shuffle event class.
        SongEvent songEvent = new SongEvent(null, 0, shuffleOn
                                                                    ? SongEvent.Type.SHUFFLE_ON
                                                                    : SongEvent.Type.SHUFFLE_OFF);
        return songEvent;

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
        if(songs != null) {
            SPMainActivity.getSongEventHandler().dispatchEvent(getSongChangeEvent(songModel));
            mediaPlayer.start();
        }
    }
} // end SongPlayerService
