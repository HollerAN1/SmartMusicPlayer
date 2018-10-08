package com.smartmusic.android.smartmusicplayer.utils.comparators.playlists;

import com.smartmusic.android.smartmusicplayer.database.entities.Playlist;

import java.util.Comparator;

/**
 * Sorts playlists by size
 *
 * Created by holle on 7/29/2018.
 */

public class PlaylistSizeComparator implements Comparator<Playlist> {

    public PlaylistSizeComparator(){}

    @Override
    public int compare(Playlist o1, Playlist o2) {
//        return o1.getSongCount() - o2.getSongCount();
        return 0;
    }
}
