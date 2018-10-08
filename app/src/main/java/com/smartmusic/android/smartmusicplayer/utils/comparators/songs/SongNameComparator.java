package com.smartmusic.android.smartmusicplayer.utils.comparators.songs;

import com.smartmusic.android.smartmusicplayer.database.entities.Song;

import java.util.Comparator;

/**
 * Sorts songs by song name.
 *
 * Created by holle on 7/29/2018.
 */

public class SongNameComparator implements Comparator<Song> {

    public SongNameComparator(){}

    @Override
    public int compare(Song o1, Song o2) {
        return o1.getSongName().compareTo(o2.getSongName());
    }
}
