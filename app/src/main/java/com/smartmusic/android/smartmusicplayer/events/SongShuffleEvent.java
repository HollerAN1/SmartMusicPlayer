package com.smartmusic.android.smartmusicplayer.events;

import com.smartmusic.android.smartmusicplayer.database.entities.Song;

public class SongShuffleEvent {
    private Type type;

    /**
     * SongShuffleEvent constructor
     * @param on if shuffle was turned on.
     */
    public SongShuffleEvent(boolean on){
        this.type = on ? Type.SHUFFLE_ON : Type.SHUFFLE_OFF;
    }

    public enum Type{
        SHUFFLE_ON, SHUFFLE_OFF
    }

    public Type getType(){
        return this.type;
    }
}
