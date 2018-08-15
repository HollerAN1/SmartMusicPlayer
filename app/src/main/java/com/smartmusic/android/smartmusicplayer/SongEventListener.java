package com.smartmusic.android.smartmusicplayer;

import java.util.EventListener;

/**
 * Created by holle on 3/14/2018.
 */

public interface SongEventListener extends EventListener{

//    public void onSongStartEvent(SongEvent e);
//
//    public void onSongStopEvent(SongEvent e);

    public void onSongChangeEvent(SongEvent e);
    public void onShuffleOnEvent(SongEvent e);
    public void onShuffleOffEvent(SongEvent e);
    public void onSongStopEvent(SongEvent e);
    public void onSongAddedEvent(SongEvent e);
    public void onSongRemovedEvent(SongEvent e);
//
//    public void onNextSongEvent(SongEvent e);
//
//    public void onPreviousSongEvent(SongEvent e);
}
