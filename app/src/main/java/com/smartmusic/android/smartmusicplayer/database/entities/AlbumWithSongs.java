package com.smartmusic.android.smartmusicplayer.database.entities;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

public class AlbumWithSongs {
    @Embedded
    public Album album;

    @Relation(parentColumn = "uid", entityColumn = "album_uid", entity = Song.class)
    public List<Song> _songs;


    // Getter/setter methods
    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public List<Song> get_songs() {
        return _songs;
    }

    public void set_songs(List<Song> _songs) {
        this._songs = _songs;
    }
}
