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

    public void dispatchEvent(SongEvent songEvent){
        switch(songEvent.getType()){
            case SONG_CHANGED:
                for(SongPlaybackEventListener listener : songPlaybackEventListenerList){
                    listener.onSongChangeEvent(songEvent);
                }
                break;
            case SONG_STOPPED:
                for(SongPlaybackEventListener listener : songPlaybackEventListenerList){
                    listener.onSongStopEvent(songEvent);
                }
                break;
            case SHUFFLE_ON:
                for(SongShuffleEventListener listener : songShuffleEventListenerList){
                    listener.onShuffleOnEvent(songEvent);
                }
                break;
            case SHUFFLE_OFF:
                for(SongShuffleEventListener listener : songShuffleEventListenerList) {
                    listener.onShuffleOffEvent(songEvent);
                }
                break;
            case SONG_ADDED:
                for(SongDatabaseChangedListener listener : songDatabaseChangedListenerList) {
                    listener.onSongAddedEvent(songEvent);
                }
                break;
            case SONG_REMOVED:
                for(SongDatabaseChangedListener listener : songDatabaseChangedListenerList) {
                    listener.onSongRemovedEvent(songEvent);
                }
                break;
        }
    }
}
