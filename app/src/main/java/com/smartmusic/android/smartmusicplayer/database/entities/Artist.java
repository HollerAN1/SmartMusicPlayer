package com.smartmusic.android.smartmusicplayer.database.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.UUID;

@Entity(tableName = "artist_table")
public class Artist implements Comparable<Artist> {
    @PrimaryKey
    @NonNull
    private String uid;

    @ColumnInfo(name = "artist_name")
    private String artistName;

    public Artist(String artistName) {
        this.uid = UUID.randomUUID().toString();
        this.artistName = artistName;
    }

    // Getter/setter methods
    public String getUid() {
        return uid;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int compareTo(Artist artist) {
        if(this.getArtistName() != null &&  artist.getArtistName() != null){
            return this.getArtistName().compareToIgnoreCase(artist.getArtistName());
        }
        return 0;
    }
}
