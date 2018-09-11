package com.smartmusic.android.smartmusicplayer.events;

import com.smartmusic.android.smartmusicplayer.events.SongEvent;

import java.util.EventListener;

public interface SongShuffleEventListener extends EventListener {
    void onShuffleOnEvent(SongEvent e);
    void onShuffleOffEvent(SongEvent e);
}
