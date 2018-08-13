package com.smartmusic.android.smartmusicplayer;

import android.media.MediaPlayer;
import android.os.Handler;

import com.smartmusic.android.smartmusicplayer.model.SongInfo;

import java.io.IOException;

/**
 * Created by holle on 7/1/2018.
 */

public class SongPlayer {

    private Handler myHandler = new Handler();
    private SongInfo songInfo;
    private static MediaPlayer mediaPlayer = new MediaPlayer();

    public SongPlayer(){

    }


    public void playSong(final SongInfo s){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (mediaPlayer.isPlaying()){
                        stop();
                    }

                    mediaPlayer.setDataSource(s.getSongUrl());
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnCompletionListener(getOnCompletionListener());
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            songInfo = s;
                            SPMainActivity.getSongEventHandler().dispatchEvent(getSongChangeEvent(s));
                            mp.start();
                        }
                    });
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

    private MediaPlayer.OnCompletionListener getOnCompletionListener()
    {
        MediaPlayer.OnCompletionListener listener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
                mp.reset();

                if(SPMainActivity.getNextSong() != null) {
                    try {
                        songInfo = SPMainActivity.getNextSong();
                        mp.setDataSource(songInfo.getSongUrl());
                        mp.prepareAsync();
                        mp.setOnCompletionListener(this);

                        SPMainActivity.getSongEventHandler().dispatchEvent(
                                getSongChangeEvent(songInfo));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    SPMainActivity.getSongEventHandler().dispatchEvent(getSongStopEvent(songInfo));
                }
            }
        };

        return listener;
    }

    public void playNextSong(){
        mediaPlayer.stop();
        mediaPlayer.reset();

        songInfo = SPMainActivity.getNextSong();

        if(songInfo != null){
            playSong(songInfo);
        }
    }

    public void playPreviousSong(){
        mediaPlayer.stop();
        mediaPlayer.reset();

        songInfo = SPMainActivity.getPreviousSong();

        if(songInfo != null){
            playSong(songInfo);
        }
    }


    private SongEvent getSongChangeEvent(SongInfo info){
        SongEvent event = new SongEvent(info, SPMainActivity._songs.indexOf(info), SongEvent.Type.SONG_CHANGED);
        return event;
    }

    private SongEvent getSongStopEvent(SongInfo info) {
        SongEvent event = new SongEvent(info, SPMainActivity._songs.indexOf(info), SongEvent.Type.SONG_STOPPED);
        return event;
    }

}
