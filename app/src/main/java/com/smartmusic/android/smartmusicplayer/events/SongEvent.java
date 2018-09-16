package com.smartmusic.android.smartmusicplayer.events;

public abstract class SongEvent{
    protected Type type;

    public enum Type{
        SONG_ADDED, SONG_REMOVED, SONG_STARTED, SONG_STOPPED, SONG_CHANGED, SONG_PLAY_NEXT, SONG_PLAY_PREVIOUS, SHUFFLE_ON, SHUFFLE_OFF
    }

    public Type getType(){ return this.type;}

}
