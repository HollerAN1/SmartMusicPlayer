package com.smartmusic.android.smartmusicplayer.events;

import com.smartmusic.android.smartmusicplayer.database.entities.Song;

import java.util.EventObject;

public class SongDatabaseEvent extends EventObject {

        private int songIndex;
        private Type type;
        private Song song;

        /**
         * SongDatabaseEvent constructor
         * @param source the song being changed to
         * @param songIndex the song index being changed to
         * @param type the type of song event
         */
        public SongDatabaseEvent(Song source, int songIndex, Type type){
            super(source);
            this.song = source;
            this.songIndex = songIndex;
            this.type = type;
        }

        public enum Type{
            SONG_ADDED, SONG_REMOVED
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
