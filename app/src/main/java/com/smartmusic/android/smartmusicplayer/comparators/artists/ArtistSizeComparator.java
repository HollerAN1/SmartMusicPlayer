package com.smartmusic.android.smartmusicplayer.comparators.artists;

import com.smartmusic.android.smartmusicplayer.model.ArtistInfo;

import java.util.Comparator;

/**
 * Sorts artists by size of songlist.
 *
 * Created by holle on 7/29/2018.
 */

public class ArtistSizeComparator implements Comparator<ArtistInfo> {

    public ArtistSizeComparator(){}

    @Override
    public int compare(ArtistInfo o1, ArtistInfo o2) {
        return o1.getSongCount() - o2.getSongCount();
    }
}
