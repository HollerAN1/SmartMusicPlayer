package com.smartmusic.android.smartmusicplayer.comparators.albums;

import com.smartmusic.android.smartmusicplayer.model.AlbumInfo;

import java.util.Comparator;

/**
 * Sorts albums by artist name.
 *
 * Created by holle on 7/29/2018.
 */

public class AlbumArtistComparator implements Comparator<AlbumInfo> {

    public AlbumArtistComparator(){}

    @Override
    public int compare(AlbumInfo o1, AlbumInfo o2) {
        return o1.getArtistname().compareTo(o2.getArtistname());
    }
}
