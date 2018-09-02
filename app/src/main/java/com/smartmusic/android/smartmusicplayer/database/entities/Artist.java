package com.smartmusic.android.smartmusicplayer.database.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.UUID;

@Entity(tableName = "artist_table")
public class Artist implements Comparable<Artist> {
    @PrimaryKey
    @NonNull
    private String artistUID;

    @ColumnInfo(name = "artist_name")
    private String artistName;

    @ColumnInfo(name = "number_of_songs")
    private int numSongs;

    @ColumnInfo(name = "number_of_albums")
    private int numAlbums;

    @Ignore
    private List<Song> songs;

    @Ignore List<Album> albums;

    public Artist(String artistName) {
        this.artistUID = UUID.randomUUID().toString();
        this.artistName = artistName;
        this.numSongs = 0;
        this.numAlbums = 0;
    }

    // Getter/setter methods
    public String getArtistUID() {
        return artistUID;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setArtistUID(String artistUID) {
        this.artistUID = artistUID;
    }

    public int getNumSongs() {
        return numSongs;
    }

    public void setNumSongs(int numSongs) {
        this.numSongs = numSongs;
    }

    public int getNumAlbums() {
        return numAlbums;
    }

    public void setNumAlbums(int numAlbums) {
        this.numAlbums = numAlbums;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public int compareTo(Artist artist) {
        if(this.getArtistName() != null &&  artist.getArtistName() != null){
            return this.getArtistName().compareToIgnoreCase(artist.getArtistName());
        }
        return 0;
    }
}
