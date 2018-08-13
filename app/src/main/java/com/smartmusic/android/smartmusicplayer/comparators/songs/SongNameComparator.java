package com.smartmusic.android.smartmusicplayer.comparators.songs;

import com.smartmusic.android.smartmusicplayer.model.SongInfo;

import java.util.Comparator;

/**
 * Sorts songs by song name.
 *
 * Created by holle on 7/29/2018.
 */

public class SongNameComparator implements Comparator<SongInfo> {

    public SongNameComparator(){}

    @Override
    public int compare(SongInfo o1, SongInfo o2) {
        return o1.getSongname().compareTo(o2.getSongname());
    }
}
