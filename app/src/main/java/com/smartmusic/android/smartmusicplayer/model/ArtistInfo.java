package com.smartmusic.android.smartmusicplayer.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by holle on 3/11/2018.
 */

public class ArtistInfo implements Comparable<ArtistInfo>{
    /*Descriptive song fields*/
    private String Artistname;
    private List<SongInfo> _artistSongs = new ArrayList<>();

    /*Empty Constructor*/
    public ArtistInfo() {
    }

    /*Regular Constructor*/
    public ArtistInfo(String artistname) {
        Artistname = artistname;
    }

    /**
     * Getter and setter methods
     */
    public String getArtistname() {
        return Artistname;
    }

    public void setArtistname(String artistname) {
        Artistname = artistname;
    }

    public void clearSongs(){
        _artistSongs.clear();
    }

    public void addSong(SongInfo i){
        _artistSongs.add(i);
    }

    public void removeSong(SongInfo i){
        if(_artistSongs.contains(i)) {
            _artistSongs.remove(_artistSongs.indexOf(i));
        }
    }

    public int getSongCount(){
        return _artistSongs.size();
    }

    public SongInfo getSong(int index){
        return _artistSongs.get(index);
    }


    public int compareTo(ArtistInfo artist) {
        if(this.getArtistname() != null &&  artist.getArtistname() != null){
            return this.getArtistname().compareToIgnoreCase(artist.getArtistname());
        }
        return 0;
    }

}
