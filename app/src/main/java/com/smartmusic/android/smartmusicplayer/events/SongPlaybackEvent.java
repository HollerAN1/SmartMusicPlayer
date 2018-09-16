package com.smartmusic.android.smartmusicplayer.events;

import com.smartmusic.android.smartmusicplayer.database.entities.Song;

/**
 * Created by holle on 3/14/2018.
 */

public class SongPlaybackEvent extends SongEvent {
    private int songIndex;
    private Song song;


    /**
     * SongPlaybackEvent constructor
     * @param source the song being changed to
     * @param songIndex the song index being changed to
     * @param type the type of song event
     */
    public SongPlaybackEvent(Song source, int songIndex, Type type){
        this.song = source;
        this.songIndex = songIndex;
        this.type = type;
    }

    public int getSongIndex(){
        return this.songIndex;
    }

    public Song getSource(){
        return this.song;
    }
}
