package com.smartmusic.android.smartmusicplayer.utils.comparators.songs;

import com.smartmusic.android.smartmusicplayer.database.entities.Song;

import java.util.Comparator;

/**
 * Sorts songs by artist name.
 *
 * Created by holle on 7/29/2018.
 */

public class SongArtistComparator implements Comparator<Song> {

    public SongArtistComparator(){}

    @Override
    public int compare(Song o1, Song o2) {
        return o1.getArtistName().compareTo(o2.getArtistName());
    }
}
