package com.smartmusic.android.smartmusicplayer.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * A playlist object.
 *
 * Created by holle on 7/29/2018.
 */

public class PlaylistInfo {

//    private ArrayList<SongInfo> _songs = new ArrayList<>();
    private List<SongInfo> _songs = Collections.synchronizedList(new ArrayList<SongInfo>());

    // Comparator describing how songs in playlist will be sorted
    private Comparator<SongInfo> sort;

    private String name;
    private String description;
    private Date dateCreated;


    public PlaylistInfo(String name, String description, ArrayList<SongInfo> songs, Comparator<SongInfo> sort) {
        this.name = name;
        this.description = description != null ? description : "";
        if( songs != null && songs.size() != 0) {
            this._songs = songs;
        }
        if(sort != null) {
            this.sort = sort;
        }

        this.dateCreated = new Date();
    }


    public void addSong( SongInfo s ){
        synchronized ( _songs ) {
            _songs.add(s);
        }
    }

    public void addSongList( ArrayList<SongInfo> sList ) {
        for( SongInfo s : sList ) {
            addSong(s);
        }
    }

    public boolean removeSong( SongInfo s ){
        synchronized ( _songs ) {
           return  _songs.remove(s);
        }
    }

    public ArrayList<SongInfo> getSongs() {
        synchronized ( _songs ) {
            ArrayList<SongInfo> listCopy = new ArrayList<>();
            Iterator<SongInfo> iter = _songs.iterator();

            while(iter.hasNext()) {
                SongInfo s = iter.next();
                listCopy.add(s);
            }

            if( sort != null ) {
                Collections.sort(listCopy, sort);
            }

            return listCopy;
        }
    }

    public int getSongCount(){
        synchronized ( _songs ) {
            return _songs.size();
        }
    }

    public String getName(){
        return this.name;
    }

    public String getDescription(){
        return this.description;
    }

    public Date getDateCreated(){
        return this.dateCreated;
    }

}
