package com.smartmusic.android.smartmusicplayer.comparators.albums;

import com.smartmusic.android.smartmusicplayer.database.entities.Album;

import java.util.Comparator;

/**
 * Sorts albums by artist name.
 *
 * Created by holle on 7/29/2018.
 */

public class AlbumArtistComparator implements Comparator<Album> {

    public AlbumArtistComparator(){}

    @Override
    public int compare(Album o1, Album o2) {
        return o1.getArtistName().compareTo(o2.getArtistName());
    }
}
