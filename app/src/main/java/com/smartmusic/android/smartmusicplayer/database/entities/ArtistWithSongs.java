package com.smartmusic.android.smartmusicplayer.database.entities;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

public class ArtistWithSongs {
    @Embedded
    public Artist artist;

    @Relation(parentColumn = "uid", entityColumn = "artist_uid", entity = Song.class)
    public List<Song> _songs;

    // Getter/setter methods

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public List<Song> get_songs() {
        return _songs;
    }

    public void set_songs(List<Song> _songs) {
        this._songs = _songs;
    }
}
