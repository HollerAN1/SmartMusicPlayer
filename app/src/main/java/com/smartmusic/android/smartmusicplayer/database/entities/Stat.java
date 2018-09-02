package com.smartmusic.android.smartmusicplayer.database.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.UUID;

@Entity(tableName = "stat_table")
public class Stat {
    @PrimaryKey
    @NonNull
    public String statUID;

    @ColumnInfo(name = "is_favorited")
    private boolean isFavorited;

    @ColumnInfo(name = "times_played")
    private int timesPlayed;

    @ColumnInfo(name = "times_skipped")
    private int timesSkipped;

    @ColumnInfo(name = "times_manually_played")
    private int timesManuallyPlayed;

//    @ColumnInfo(name = "date_added")
//    private Date dateAdded;

    public Stat(){
        this.statUID = UUID.randomUUID().toString();
        this.timesManuallyPlayed = 0;
        this.timesSkipped = 0;
        this.timesPlayed = 0;
        this.isFavorited = false;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    public void setFavorited(boolean favorited) {
        isFavorited = favorited;
    }

    public int getTimesPlayed() {
        return timesPlayed;
    }

    public void setTimesPlayed(int timesPlayed) {
        this.timesPlayed = timesPlayed;
    }

    public int getTimesSkipped() {
        return timesSkipped;
    }

    public void setTimesSkipped(int timesSkipped) {
        this.timesSkipped = timesSkipped;
    }

    public int getTimesManuallyPlayed() {
        return timesManuallyPlayed;
    }

    public void setTimesManuallyPlayed(int timesManuallyPlayed) {
        this.timesManuallyPlayed = timesManuallyPlayed;
    }

//    public Date getDateAdded() {
//        return dateAdded;
//    }
//
//    public void setDateAdded(Date dateAdded) {
//        this.dateAdded = dateAdded;
//    }
}
