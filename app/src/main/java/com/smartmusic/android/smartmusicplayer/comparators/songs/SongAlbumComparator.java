package com.smartmusic.android.smartmusicplayer.comparators.songs;

import com.smartmusic.android.smartmusicplayer.model.SongInfo;

import java.util.Comparator;

/**
 * Sorts songs by album name.
 *
 * Created by holle on 7/29/2018.
 */

public class SongAlbumComparator implements Comparator<SongInfo> {

    public SongAlbumComparator(){}

    @Override
    public int compare(SongInfo o1, SongInfo o2) {
        return o1.getAlbumname().compareTo(o2.getAlbumname());
    }
}
