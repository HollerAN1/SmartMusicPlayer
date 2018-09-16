package com.smartmusic.android.smartmusicplayer.events;

import com.smartmusic.android.smartmusicplayer.database.entities.Song;

public class SongDatabaseEvent extends SongEvent {
        private Song song;

        /**
         * SongDatabaseEvent constructor
         * @param source the song being changed to
         * @param type the type of song event
         */
        public SongDatabaseEvent(Song source, Type type){
            this.song = source;
            this.type = type;
        }

        public Song getSource(){
            return this.song;
        }


}
