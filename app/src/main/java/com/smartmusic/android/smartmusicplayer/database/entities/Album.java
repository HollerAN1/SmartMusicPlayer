package com.smartmusic.android.smartmusicplayer.database.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.UUID;

@Entity(tableName = "album_table")
public class Album implements Comparable<Album> {
    @PrimaryKey
    @NonNull
    private String uid;

    @ColumnInfo(name = "artist_name")
    private String artistName;

    @ColumnInfo(name = "album_name")
    private String albumName;

    @ColumnInfo(name = "album_art_uri")
    private String albumArtString;

    public Album(String artistName, String albumName, String albumArtString) {
        this.uid = UUID.randomUUID().toString();
        this.artistName = artistName;
        this.albumArtString = albumArtString;
        this.albumName = albumName;
    }

    // Getter/setter methods
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getAlbumArtString() {
        return albumArtString;
    }

    public void setAlbumArtString(String albumArtString) {
        this.albumArtString = albumArtString;
    }

    public Uri getAlbumArt(){
        return Uri.parse(albumArtString);
    }

    public int compareTo(Album album) {
        if(this.getAlbumName() != null && album.getAlbumName() != null){
            return this.getAlbumName().compareToIgnoreCase(album.getAlbumName());
        }
        return 0;
    }
}
