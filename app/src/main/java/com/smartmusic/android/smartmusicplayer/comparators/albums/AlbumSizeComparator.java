package com.smartmusic.android.smartmusicplayer.comparators.albums;

import com.smartmusic.android.smartmusicplayer.model.AlbumInfo;

import java.util.Comparator;

/**
 * Sorts albums based on album size.
 *
 * Created by holle on 7/29/2018.
 */

public class AlbumSizeComparator implements Comparator<AlbumInfo> {


    public AlbumSizeComparator(){}

    @Override
    public int compare(AlbumInfo o1, AlbumInfo o2) {
        return o1.getSongs().size() - o2.getSongs().size();
    }
}
