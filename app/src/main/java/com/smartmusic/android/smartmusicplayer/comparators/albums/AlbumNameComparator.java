package com.smartmusic.android.smartmusicplayer.comparators.albums;

import com.smartmusic.android.smartmusicplayer.model.AlbumInfo;

import java.util.Comparator;

/**
 * Sorts by album name.
 *
 * Created by holle on 7/29/2018.
 */

public class AlbumNameComparator implements Comparator<AlbumInfo> {

    public AlbumNameComparator(){}

    @Override
    public int compare(AlbumInfo o1, AlbumInfo o2) {
        return o1.getAlbumName().compareTo(o2.getAlbumName());
    }
}
