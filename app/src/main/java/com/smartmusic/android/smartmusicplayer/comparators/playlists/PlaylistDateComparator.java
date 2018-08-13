package com.smartmusic.android.smartmusicplayer.comparators.playlists;

import com.smartmusic.android.smartmusicplayer.model.PlaylistInfo;

import java.util.Comparator;

/**
 * Sorts playlists by date created.
 *
 * Created by holle on 7/29/2018.
 */

public class PlaylistDateComparator implements Comparator<PlaylistInfo> {

    PlaylistDateComparator(){}

    @Override
    public int compare(PlaylistInfo o1, PlaylistInfo o2) {
        return o1.getDateCreated().compareTo(o2.getDateCreated());
    }
}
