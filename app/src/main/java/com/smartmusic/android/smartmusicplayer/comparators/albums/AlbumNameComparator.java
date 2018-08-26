package com.smartmusic.android.smartmusicplayer.comparators.albums;

import com.smartmusic.android.smartmusicplayer.database.entities.Album;

import java.util.Comparator;

/**
 * Sorts by album name.
 *
 * Created by holle on 7/29/2018.
 */

public class AlbumNameComparator implements Comparator<Album> {

    public AlbumNameComparator(){}

    @Override
    public int compare(Album o1, Album o2) {
        return o1.getAlbumName().compareTo(o2.getAlbumName());
    }
}
