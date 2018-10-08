package com.smartmusic.android.smartmusicplayer.utils.comparators.albums;

import com.smartmusic.android.smartmusicplayer.database.entities.Album;

import java.util.Comparator;

/**
 * Sorts albums based on album size.
 *
 * Created by holle on 7/29/2018.
 */

public class AlbumSizeComparator implements Comparator<Album> {


    public AlbumSizeComparator(){}

    @Override
    public int compare(Album o1, Album o2) {
//        return o1.getSongs().size() - o2.getSongs().size();
        return 0;
    }
}
