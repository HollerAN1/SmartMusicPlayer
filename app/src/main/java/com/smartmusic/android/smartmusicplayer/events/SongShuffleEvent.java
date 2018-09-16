package com.smartmusic.android.smartmusicplayer.events;

public class SongShuffleEvent extends SongEvent {

    /**
     * SongShuffleEvent constructor
     * @param on if shuffle was turned on.
     */
    public SongShuffleEvent(boolean on){
        this.type = on ? Type.SHUFFLE_ON : Type.SHUFFLE_OFF;
    }
}
