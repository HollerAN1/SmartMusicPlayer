package com.smartmusic.android.smartmusicplayer.database.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity(tableName = "playlist_table")
public class Playlist implements Comparable<Playlist> {
    @PrimaryKey
    @NonNull
    private String playlistUID;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "user_created")
    private boolean userCreated;

    @ColumnInfo(name = "album_art_uri")
    private String albumArtString;

//    @ColumnInfo(name = "date_created")
//    private Date dateCreated;

    @Ignore
    private List<Song> songs;

    public Playlist(String name, String description, boolean userCreated) {
        this.playlistUID = UUID.randomUUID().toString();
        this.name = name;
        this.description = description != null ? description : "";
        this.userCreated = userCreated;
//        this.dateCreated = new Date();
    }

    // Getter/Setter methods
    public String getPlaylistUID() {
        return playlistUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlaylistUID(String playlistUID) {
        this.playlistUID = playlistUID;
    }

    public String getDescription() {
        return description;
    }

//    public Date getDateCreated() {
//        return dateCreated;
//    }

    public String getAlbumArtString() {
        return albumArtString;
    }

    public void setAlbumArtString(String albumArtString) {
        this.albumArtString = albumArtString;
    }

    public Uri getAlbumArt(){
       return Uri.parse(albumArtString);
    }

//
//    public void setDateCreated(Date dateCreated) {
//        this.dateCreated = dateCreated;
//    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public boolean isUserCreated() {
        return userCreated;
    }

    public void setUserCreated(boolean userCreated) {
        this.userCreated = userCreated;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int compareTo(Playlist playlist) {
        if(this.getName() != null &&  playlist.getName() != null){
            return this.getName().compareToIgnoreCase(playlist.getName());
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Playlist: " + this.name;
    }
}
