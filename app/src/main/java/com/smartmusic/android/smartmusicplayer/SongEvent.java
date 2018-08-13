package com.smartmusic.android.smartmusicplayer;

import com.smartmusic.android.smartmusicplayer.model.SongInfo;

import java.util.EventObject;

/**
 * Created by holle on 3/14/2018.
 */

public class SongEvent extends EventObject{

    private int songIndex;
    private Type type;
    private SongInfo song;


    /**
     * SongEvent constructor
     * @param source the song being changed to
     * @param songIndex the song index being changed to
     * @param type the type of song event
     */
    public SongEvent(SongInfo source, int songIndex, Type type){
        super(source);
        this.song = source;
        this.songIndex = songIndex;
        this.type = type;
    }

    public enum Type{

            SONG_STARTED, SONG_STOPPED, SONG_CHANGED, SONG_PLAY_NEXT, SONG_PLAY_PREVIOUS, SHUFFLE_ON, SHUFFLE_OFF;

        }

    public int getSongIndex(){
        return this.songIndex;
    }

    public Type getType(){
        return this.type;
    }

    public SongInfo getSource(){
        return this.song;
    }
}
