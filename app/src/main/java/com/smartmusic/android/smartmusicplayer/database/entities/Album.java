package com.smartmusic.android.smartmusicplayer.database.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.UUID;

@Entity(tableName = "album_table")
public class Album implements Comparable<Album> {
    @PrimaryKey
    @NonNull
    private String albumUID;

    @ColumnInfo(name = "album_name")
    private String albumName;

    @ColumnInfo(name = "artist_name")
    private String artistName;

    @ColumnInfo(name = "artist_uid")
    private String artistUid;

    @ColumnInfo(name = "album_art_uri")
    private String albumArtString;

    @ColumnInfo(name = "number_of_songs")
    private int numSongs;

    @Ignore
    private List<Song> songs;

    public Album(String albumName, String artistName, String albumArtString) {
        this.albumUID = UUID.randomUUID().toString();
        this.artistName = artistName;
        this.albumArtString = albumArtString;
        this.albumName = albumName;
        this.numSongs = 0;
    }

    // Getter/setter methods
    public String getAlbumUID() {
        return albumUID;
    }

    public void setAlbumUID(String albumUID) {
        this.albumUID = albumUID;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getArtistUid() {
        return artistUid;
    }

    public void setArtistUid(String artistUid) {
        this.artistUid = artistUid;
    }

    public String getAlbumArtString() {
        return albumArtString;
    }

    public void setAlbumArtString(String albumArtString) {
        this.albumArtString = albumArtString;
    }

    public Uri getAlbumArt(){
        return Uri.parse(albumArtString);
    }

    public int getNumSongs() {
        return numSongs;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public void setNumSongs(int numSongs) {
        this.numSongs = numSongs;

    }

    public int compareTo(Album album) {
        if(this.getAlbumName() != null && album.getAlbumName() != null){
            return this.getAlbumName().compareToIgnoreCase(album.getAlbumName());
        }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this){return true;}
        if(!(obj instanceof Album)){return false;}
        Album album = (Album) obj;
        return  album.getAlbumName().equals(this.getAlbumName())
                && album.getArtistName().equals(this.getArtistName());
    }

    @Override
    public int hashCode() {
        return this.getAlbumUID().hashCode();
    }

    @Override
    public String toString() {
        return "Album: " + this.albumName + " by " + this.artistName;
    }
}
