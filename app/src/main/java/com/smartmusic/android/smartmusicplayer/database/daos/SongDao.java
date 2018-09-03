package com.smartmusic.android.smartmusicplayer.database.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.smartmusic.android.smartmusicplayer.database.entities.Song;

import java.util.List;

@Dao
public abstract class SongDao {
    @Query("SELECT * FROM song_table")
    public abstract List<Song> getAllSongsStatic();

    @Query("SELECT * FROM song_table")
    public abstract LiveData<List<Song>> getAllUnsorted(); // LiveData ensures data is updated on database changes

    @Query("SELECT * FROM song_table ORDER BY song_name")
    public abstract LiveData<List<Song>> getAllNameSort();

    @Query("SELECT * FROM song_table ORDER BY artist_name")
    public abstract LiveData<List<Song>> getAllArtistSort();

    @Query("SELECT * FROM song_table ORDER BY album_name")
    public abstract LiveData<List<Song>> getAllAlbumSort();

    @Query("SELECT * FROM song_table WHERE song_name LIKE :name")
    public abstract Song findSongByName(String name);

    @Query("SELECT * FROM song_table WHERE song_name LIKE '%' || :query || '%' ORDER BY song_name")
    public abstract List<Song> searchSongs(String query);

    @Query("SELECT * FROM song_table WHERE songUID LIKE :uid")
    public abstract Song findSongByUID(String uid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAll(Song... songs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Song song);

    @Delete
    public abstract void delete(Song song);
}
