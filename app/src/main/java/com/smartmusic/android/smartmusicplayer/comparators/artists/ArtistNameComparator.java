package com.smartmusic.android.smartmusicplayer.comparators.artists;

import com.smartmusic.android.smartmusicplayer.database.entities.Artist;

import java.util.Comparator;

/**
 * Sorts artists by name.
 *
 * Created by holle on 7/29/2018.
 */

public class ArtistNameComparator implements Comparator<Artist> {

    public ArtistNameComparator(){}

    @Override
    public int compare(Artist o1, Artist o2) {
        return o1.getArtistName().compareTo(o2.getArtistName());
    }
}
