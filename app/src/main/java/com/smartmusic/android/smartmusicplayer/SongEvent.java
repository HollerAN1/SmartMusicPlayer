package com.smartmusic.android.smartmusicplayer;

import com.smartmusic.android.smartmusicplayer.database.entities.Song;
import java.util.EventObject;

/**
 * Created by holle on 3/14/2018.
 */

public class SongEvent extends EventObject{

    private int songIndex;
    private Type type;
    private Song song;


    /**
     * SongEvent constructor
     * @param source the song being changed to
     * @param songIndex the song index being changed to
     * @param type the type of song event
     */
    public SongEvent(Song source, int songIndex, Type type){
        super(source);
        this.song = source;
        this.songIndex = songIndex;
        this.type = type;
    }

    public enum Type{

            SONG_STARTED, SONG_STOPPED, SONG_CHANGED, SONG_PLAY_NEXT, SONG_PLAY_PREVIOUS, SHUFFLE_ON, SHUFFLE_OFF, SONG_ADDED, SONG_REMOVED;

        }

    public int getSongIndex(){
        return this.songIndex;
    }

    public Type getType(){
        return this.type;
    }

    public Song getSource(){
        return this.song;
    }
}
