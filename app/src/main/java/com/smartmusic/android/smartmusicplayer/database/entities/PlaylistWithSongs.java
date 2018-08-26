package com.smartmusic.android.smartmusicplayer.database.entities;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

public class PlaylistWithSongs {
    @Embedded
    public Playlist playlist;

//    @Relation(parentColumn = "id", entityColumn = "song_name", entity = Song.class)
//    public List<Song> _songs;

    // Getter/setter methods

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

//    public List<Song> get_songs() {
//        return _songs;
//    }
//
//    public void set_songs(List<Song> _songs) {
//        this._songs = _songs;
//    }
}
