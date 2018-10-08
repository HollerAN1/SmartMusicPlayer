package com.smartmusic.android.smartmusicplayer.utils.comparators.playlists;

import com.smartmusic.android.smartmusicplayer.database.entities.Playlist;

import java.util.Comparator;

/**
 * Sorts playlists by name.
 *
 * Created by holle on 7/29/2018.
 */

public class PlaylistNameComparator implements Comparator<Playlist> {

    public PlaylistNameComparator(){}

    @Override
    public int compare(Playlist o1, Playlist o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
