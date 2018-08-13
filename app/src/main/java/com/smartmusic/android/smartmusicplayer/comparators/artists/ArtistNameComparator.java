package com.smartmusic.android.smartmusicplayer.comparators.artists;

import com.smartmusic.android.smartmusicplayer.model.ArtistInfo;

import java.util.Comparator;

/**
 * Sorts artists by name.
 *
 * Created by holle on 7/29/2018.
 */

public class ArtistNameComparator implements Comparator<ArtistInfo> {

    public ArtistNameComparator(){}

    @Override
    public int compare(ArtistInfo o1, ArtistInfo o2) {
        return o1.getArtistname().compareTo(o2.getArtistname());
    }
}
