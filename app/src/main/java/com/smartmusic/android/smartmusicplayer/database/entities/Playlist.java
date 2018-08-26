package com.smartmusic.android.smartmusicplayer.database.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;
import java.util.UUID;

@Entity(tableName = "playlist_table")
public class Playlist implements Comparable<Playlist> {
    @PrimaryKey
    @NonNull
    private String uid;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "user_created")
    private boolean userCreated;

//    @ColumnInfo(name = "date_created")
//    private Date dateCreated;

    public Playlist(String name, String description, boolean userCreated) {
        this.uid = UUID.randomUUID().toString();
        this.name = name;
        this.description = description != null ? description : "";
        this.userCreated = userCreated;
//        this.dateCreated = new Date();
    }

    // Getter/Setter methods
    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDescription() {
        return description;
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

//    public Date getDateCreated() {
//        return dateCreated;
//    }
//
//    public void setDateCreated(Date dateCreated) {
//        this.dateCreated = dateCreated;
//    }

    @Override
    public int compareTo(Playlist playlist) {
        if(this.getName() != null &&  playlist.getName() != null){
            return this.getName().compareToIgnoreCase(playlist.getName());
        }
        return 0;
    }
}
