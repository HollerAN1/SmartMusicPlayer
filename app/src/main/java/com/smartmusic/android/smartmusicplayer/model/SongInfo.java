package com.smartmusic.android.smartmusicplayer.model;

/**
 * Created by holle on 7/13/2017.
 */


import android.net.Uri;
import android.widget.Button;

import java.io.Serializable;
import java.net.URI;

/**
 * Creates the instance of the song
 */
public class SongInfo implements Comparable<SongInfo>, Serializable {

    /*Descriptive song fields*/
    private String Songname;
    private String Artistname;
    private String Albumname;
    private String SongUrl;
    private Button button;
    private URI AlbumArt;
    private Boolean selected = false;

    private boolean favorited = false;

    /*Empty Constructor*/
    public SongInfo() {
    }

    /*Regular Constructor*/
    public SongInfo(String songname, String artistname, String albumname, String songUrl, Uri albumArt) {
        Songname = songname;
        Artistname = artistname;
        SongUrl = songUrl;
        try {
            AlbumArt = new URI(albumArt.toString());
        } catch (Exception e){
            e.printStackTrace();
        }
        Albumname = albumname;

    }

    /**
     * Getter and setter methods
     */
    public String getSongname() {
        return Songname;
    }

    public void setSongname(String songname) {
        Songname = songname;
    }

    public String getArtistname() {
        return Artistname;
    }

    public void setArtistname(String artistname) {
        Artistname = artistname;
    }

    public String getSongUrl() {
        return SongUrl;
    }

    public void setSongUrl(String songUrl) {
        SongUrl = songUrl;
    }

    public void setButton(Button b){
        this.button = b;
    }
    public Button getButton(){
        return this.button;
    }


//    public void setAlbumArt(Uri i){
//        this.AlbumArt = i;
//    }

    public Uri getAlbumArt(){
        return Uri.parse(this.AlbumArt.toString());
    }

    public String getAlbumname() {
        return Albumname;
    }

    public void setAlbumname(String albumname) {
        Albumname = albumname;
    }

    public Boolean getSelected(){ return this.selected;}
    public void setSelected(Boolean bool){ this.selected = bool;}

    public int compareTo(SongInfo song) {
        if(this.getSongname() != null && song.getSongname() != null){
            return this.getSongname().compareToIgnoreCase(song.getSongname());
        }
        return 0;
    }

    public void toggleFavorite(){
        if(favorited){
            favorited = false;
        } else {
            favorited = true;
        }
    }

    public boolean isFavorited(){
        return this.favorited;
    }
}
