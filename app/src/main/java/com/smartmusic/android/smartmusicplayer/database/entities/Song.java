package com.smartmusic.android.smartmusicplayer.database.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.UUID;

@Entity(tableName = "song_table")
public class Song implements Comparable<Song>, Serializable {
    @PrimaryKey
    @NonNull
    private String songUID;

    @ColumnInfo(name = "song_name")
    private String songName;

    @ColumnInfo(name = "artist_name")
    private String artistName;

    @ColumnInfo(name = "album_name")
    private String albumName;

    @ColumnInfo(name = "song_url")
    private String songUrl;

    @ColumnInfo(name = "album_art_uri")
    private String albumArtUri;

    @ColumnInfo(name = "album_uid")
    private String albumUID;

    @ColumnInfo(name = "artist_uid")
    private String artistUID;

    @Ignore
    private boolean isSelected;

    @ColumnInfo(name = "track")
    private int track;

    @ColumnInfo(name = "duration")
    private long duration;

    @ColumnInfo(name = "year")
    private String year;

    @ColumnInfo(name = "date_added")
    private String dateAdded; // Formatted as yyyyMMdd

    @ColumnInfo(name = "size")
    private long size;

    @ColumnInfo(name = "display_name")
    private String displayName;

    @Embedded
    private Stat stats;

    public Song(String songName, String artistName, String albumName, String songUrl, String albumArtUri, int track, long duration, String year, String dateAdded, long size, String displayName) {
        this.songUID = UUID.randomUUID().toString();
        this.songName = songName;
        this.artistName = artistName;
        this.albumName = albumName;
        this.songUrl = songUrl;
        this.albumArtUri = albumArtUri;
        this.isSelected = false;
        this.track = track;
        this.duration = duration;
        this.year = year;
        this.dateAdded = dateAdded;
        this.size = size;
        this.displayName = displayName;
        this.stats = new Stat();
    }

    // Getter/Setter methods
    public String getSongUID() {
        return songUID;
    }

    public int getTrack() {
        return track;
    }

    public void setTrack(int track) {
        this.track = track;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getDuration() {
        return duration;

    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setStats(Stat stats) {
        this.stats = stats;
    }

    public void setSongUID(String songUID) {
        this.songUID = songUID;
    }

    public String getSongName() {

        return songName;
    }

    public String getAlbumUID() {
        return albumUID;
    }

    public void setAlbumUID(String albumUID) {
        this.albumUID = albumUID;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Stat getStats() {
        return stats;
    }

    public String getArtistUID() {
        return artistUID;
    }

    public void setArtistUID(String artistUID) {
        this.artistUID = artistUID;
    }

    public void setSongName(String songName) {
        this.songName = songName;
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

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public String getAlbumArtUri() {
        return albumArtUri;
    }

    public void setAlbumArtUri(String albumArtUri) {
        this.albumArtUri = albumArtUri;
    }

    public Uri getAlbumArt(){
        return Uri.parse(albumArtUri);
    }

    public int compareTo(Song song) {
        if(this.getSongName() != null && song.getSongName() != null){
            return this.getSongName().compareToIgnoreCase(song.getSongName());
        }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this){return true;}
        if(!(obj instanceof Song)){return false;}
        Song song = (Song) obj;
        return song.getSongName().equals(this.getSongName()) &&
                song.getArtistName().equals(this.getArtistName()) &&
                song.getAlbumName().equals(this.getAlbumName());
    }

    @Override
    public int hashCode() {
        return this.getSongUID().hashCode();
    }

    @Override
    public String toString() {
        return "Song: " + this.songName + " by " + this.artistName;
    }
}
