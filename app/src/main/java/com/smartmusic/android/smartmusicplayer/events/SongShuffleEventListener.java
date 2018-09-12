package com.smartmusic.android.smartmusicplayer.events;

import java.util.EventListener;

public interface SongShuffleEventListener extends EventListener {
    void onShuffleOnEvent(SongShuffleEvent e);
    void onShuffleOffEvent(SongShuffleEvent e);
}
