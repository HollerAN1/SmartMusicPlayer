package com.smartmusic.android.smartmusicplayer.database.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.smartmusic.android.smartmusicplayer.database.entities.Playlist;
import com.smartmusic.android.smartmusicplayer.database.entities.Song;

import java.util.List;

@Dao
public abstract class PlaylistDao {
//    @Query("SELECT * FROM playlist_table")
//    public abstract LiveData<List<PlaylistWithSongs>> getAllWithSongs(); // LiveData ensures data is updated on database changes

    @Query("SELECT * FROM playlist_table")
    public abstract LiveData<List<Playlist>> getAll();

    @Query("SELECT * FROM playlist_table")
    public abstract  List<Playlist> getAllUnchanging();

    @Query("SELECT * FROM playlist_table WHERE name LIKE :name")
    public abstract Playlist findPlaylistByName(String name);

    @Query("SELECT * FROM playlist_table WHERE playlistUID LIKE :uid")
    public abstract Playlist findPlaylistByUID(String uid);

    @Query("SELECT * FROM playlist_table WHERE name LIKE '%' || :query || '%' ORDER BY name")
    public abstract List<Playlist> searchPlaylists(String query);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAll(Playlist... playlists);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Playlist playlist);

    @Delete
    public abstract void delete(Playlist playlist);
    @Delete
    public abstract void deleteAll(List<Playlist> playlists);
}
