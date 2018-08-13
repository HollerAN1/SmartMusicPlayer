package com.smartmusic.android.smartmusicplayer.comparators.songs;

import com.smartmusic.android.smartmusicplayer.model.SongInfo;

import java.util.Comparator;

/**
 * Sorts songs by artist name.
 *
 * Created by holle on 7/29/2018.
 */

public class SongArtistComparator implements Comparator<SongInfo> {

    public SongArtistComparator(){}

    @Override
    public int compare(SongInfo o1, SongInfo o2) {
        return o1.getArtistname().compareTo(o2.getArtistname());
    }
}
