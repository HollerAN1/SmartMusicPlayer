package com.smartmusic.android.smartmusicplayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by holle on 3/14/2018.
 */

public class SongEventHandler {

    private static final List<SongEventListener> songEventListenerList = new ArrayList<>();

    public SongEventHandler(){}

    public void addSongEventListener(SongEventListener songEventListener){
        songEventListenerList.add(songEventListener);
    }

    public void removeSongEventListener(SongEventListener songEventListener){
        songEventListenerList.remove(songEventListener);
    }

    public void dispatchEvent(SongEvent songEvent){
        switch(songEvent.getType()){
            case SONG_CHANGED:
                for(SongEventListener listener : songEventListenerList){
                    listener.onSongChangeEvent(songEvent);
                }
                break;
            case SONG_STOPPED:
                for(SongEventListener listener : songEventListenerList){
                    listener.onSongStopEvent(songEvent);
                }
            case SHUFFLE_ON:
                for(SongEventListener listener : songEventListenerList){
                    listener.onShuffleOnEvent(songEvent);
                }
                break;
            case SHUFFLE_OFF:
                for(SongEventListener listener : songEventListenerList) {
                    listener.onShuffleOffEvent(songEvent);
                }
                break;
            case SONG_ADDED:
                for(SongEventListener listener : songEventListenerList) {
                    listener.onSongAddedEvent(songEvent);
                }
                break;
            case SONG_REMOVED:
                for(SongEventListener listener : songEventListenerList) {
                    listener.onSongRemovedEvent(songEvent);
                }
                break;
//            case SONG_STOPPED:
//                for(SongEventListener listener : songEventListenerList){
//                    listener.onSongStopEvent(songEvent);
//                }
//                break;
//            case SONG_STARTED:
//                for(SongEventListener listener : songEventListenerList){
//                    listener.onSongStartEvent(songEvent);
//                }
//                break;
//            case SONG_PLAY_NEXT:
//                for(SongEventListener listener : songEventListenerList){
//                    listener.onNextSongEvent(songEvent);
//                }
//                break;
//            case SONG_PLAY_PREVIOUS:
//                for(SongEventListener listener : songEventListenerList){
//                    listener.onPreviousSongEvent(songEvent);
//                }
//                break;
        }
    }
}
