package com.smartmusic.android.smartmusicplayer.events;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles processing all song events.
 * Created by holle on 3/14/2018.
 */

public class SongEventHandler {

    private static final List<SongPlaybackEventListener> songPlaybackEventListenerList = new ArrayList<>();
    private static final List<SongShuffleEventListener> songShuffleEventListenerList = new ArrayList<>();
    private static final List<SongDatabaseChangedListener> songDatabaseChangedListenerList = new ArrayList<>();

    public SongEventHandler(){}

    public void addSongPlaybackEventListener(SongPlaybackEventListener songEventListener){
        songPlaybackEventListenerList.add(songEventListener);
    }

    public void addSongShuffleEventListener(SongShuffleEventListener listener){
        songShuffleEventListenerList.add(listener);
    }

    public void removeSongShuffleEventListener(SongShuffleEventListener listener){
        songShuffleEventListenerList.remove(listener);
    }

    public void addSongDatabaseChangedListener(SongDatabaseChangedListener listener){
        songDatabaseChangedListenerList.add(listener);
    }

    public void removeSongDatabaseChangedListener(SongDatabaseChangedListener listener){
        songDatabaseChangedListenerList.remove(listener);
    }

    public void removeSongPlaybackEventListener(SongPlaybackEventListener songEventListener){
        songPlaybackEventListenerList.remove(songEventListener);
    }

    public void dispatchEvent(SongPlaybackEvent songPlaybackEvent){
        switch(songPlaybackEvent.getType()){
            case SONG_CHANGED:
                for(SongPlaybackEventListener listener : songPlaybackEventListenerList){
                    listener.onSongChangeEvent(songPlaybackEvent);
                }
                break;
            case SONG_STOPPED:
                for(SongPlaybackEventListener listener : songPlaybackEventListenerList){
                    listener.onSongStopEvent(songPlaybackEvent);
                }
                break;
        }
    }

    public void dispatchEvent(SongDatabaseEvent event){
        switch (event.getType()){
            case SONG_ADDED:
                for(SongDatabaseChangedListener listener : songDatabaseChangedListenerList) {
                    listener.onSongAddedEvent(event);
                }
                break;
            case SONG_REMOVED:
                for(SongDatabaseChangedListener listener : songDatabaseChangedListenerList) {
                    listener.onSongRemovedEvent(event);
                }
                break;
        }
    }

    public void dispatchEvent(SongShuffleEvent event){
        switch (event.getType()){
            case SHUFFLE_ON:
                for(SongShuffleEventListener listener : songShuffleEventListenerList){
                    listener.onShuffleOnEvent(event);
                }
                break;
            case SHUFFLE_OFF:
                for(SongShuffleEventListener listener : songShuffleEventListenerList) {
                    listener.onShuffleOffEvent(event);
                }
                break;
        }
    }
}
