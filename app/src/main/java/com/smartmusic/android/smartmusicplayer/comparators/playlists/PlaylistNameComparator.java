package com.smartmusic.android.smartmusicplayer.comparators.playlists;

import com.smartmusic.android.smartmusicplayer.model.PlaylistInfo;

import java.util.Comparator;

/**
 * Sorts playlists by name.
 *
 * Created by holle on 7/29/2018.
 */

public class PlaylistNameComparator implements Comparator<PlaylistInfo> {

    public PlaylistNameComparator(){}

    @Override
    public int compare(PlaylistInfo o1, PlaylistInfo o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
