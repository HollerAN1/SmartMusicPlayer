package com.smartmusic.android.smartmusicplayer.utils.comparators.artists;

import com.smartmusic.android.smartmusicplayer.database.entities.Artist;

import java.util.Comparator;

/**
 * Sorts artists by size of songlist.
 *
 * Created by holle on 7/29/2018.
 */

public class ArtistSizeComparator implements Comparator<Artist> {

    public ArtistSizeComparator(){}

    @Override
    public int compare(Artist o1, Artist o2) {
//        return o1.getSongCount() - o2.getSongCount();
        return 0;
    }
}
