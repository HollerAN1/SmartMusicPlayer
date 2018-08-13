package com.smartmusic.android.smartmusicplayer.comparators.playlists;

import com.smartmusic.android.smartmusicplayer.model.PlaylistInfo;

import java.util.Comparator;

/**
 * Sorts playlists by size
 *
 * Created by holle on 7/29/2018.
 */

public class PlaylistSizeComparator implements Comparator<PlaylistInfo> {

    public PlaylistSizeComparator(){}

    @Override
    public int compare(PlaylistInfo o1, PlaylistInfo o2) {
        return o1.getSongCount() - o2.getSongCount();
    }
}
