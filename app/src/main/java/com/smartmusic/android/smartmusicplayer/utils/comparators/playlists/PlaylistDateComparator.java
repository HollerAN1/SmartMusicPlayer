package com.smartmusic.android.smartmusicplayer.utils.comparators.playlists;

import com.smartmusic.android.smartmusicplayer.database.entities.Playlist;

import java.util.Comparator;

/**
 * Sorts playlists by date created.
 *
 * Created by holle on 7/29/2018.
 */

public class PlaylistDateComparator implements Comparator<Playlist> {

    public PlaylistDateComparator(){}

    @Override
    public int compare(Playlist o1, Playlist o2) {
//        return o1.getDateCreated().compareTo(o2.getDateCreated());
        return 0;
    }
}
