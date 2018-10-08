package com.smartmusic.android.smartmusicplayer.utils.comparators.songs;

import com.smartmusic.android.smartmusicplayer.database.entities.Song;

import java.util.Comparator;

/**
 * Sorts songs by album name.
 *
 * Created by holle on 7/29/2018.
 */

public class SongAlbumComparator implements Comparator<Song> {

    public SongAlbumComparator(){}

    @Override
    public int compare(Song o1, Song o2) {
        return o1.getAlbumName().compareTo(o2.getAlbumName());
    }
}
