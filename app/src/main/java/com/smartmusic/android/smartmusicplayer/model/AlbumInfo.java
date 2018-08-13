package com.smartmusic.android.smartmusicplayer.model;

import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by holle on 3/11/2018.
 */

public class AlbumInfo implements Comparable<AlbumInfo>{
    /*Descriptive song fields*/
    private ArrayList<SongInfo> albumSongs = new ArrayList<>();
    private String artistName;
    private String albumName;

    private Uri albumArt;
    private Boolean selected = false;

    /*Empty Constructor*/
    public AlbumInfo() {
    }

    /*Regular Constructor*/
    public AlbumInfo(String artistname, String albumname, Uri albumArt) {
        this.artistName = artistname;
        this.albumArt = albumArt;
        this.albumName = albumname;

    }

    /**
     * Getter and setter methods
     */
    public String getArtistname() {
        return artistName;
    }

    public void setArtistname(String artistname) {
        artistName = artistname;
    }

    public void setAlbumArt(Uri i){
        this.albumArt = i;
    }

    public String getAlbumName(){
        return this.albumName;
    }

    public void setAlbumName(String albumname){
        this.albumName = albumname;
    }

    public Uri getAlbumArt(){
        return this.albumArt;
    }

    public void addSong(SongInfo s){
        this.albumSongs.add(s);
    }

    public void clearSongs(){
        this.albumSongs.clear();
    }

    public ArrayList<SongInfo> getSongs(){
        return this.albumSongs;
    }


    public Boolean getSelected(){ return this.selected;}
    public void setSelected(Boolean bool){ this.selected = bool;}

    public int compareTo(AlbumInfo album) {
        if(this.getAlbumName() != null && album.getAlbumName() != null){
            return this.getAlbumName().compareToIgnoreCase(album.getAlbumName());
        }
        return 0;
    }
}
