package com.smartmusic.android.smartmusicplayer.database.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.smartmusic.android.smartmusicplayer.database.entities.Album;
import com.smartmusic.android.smartmusicplayer.database.entities.Song;

import java.util.List;

@Dao
public abstract class AlbumDao {

    // Get list of albums depending on sorting preference
    @Query("SELECT * FROM album_table")
    public abstract List<Album> getAllAlbumsStatic();

    @Query("SELECT * FROM album_table")
    public abstract LiveData<List<Album>> getAllUnsorted();

    @Query("SELECT * FROM album_table ORDER BY album_name")
    public abstract LiveData<List<Album>> getAllNameSort();

    @Query("SELECT * FROM album_table ORDER BY artist_name")
    public abstract LiveData<List<Album>> getAllArtistSort();

    @Query("SELECT * FROM album_table ORDER BY number_of_songs")
    public abstract LiveData<List<Album>> getAllSongCountSort();


    // Find album by fields
    @Query("SELECT * FROM album_table WHERE album_name LIKE :name")
    public abstract Album findAlbumByName(String name);

    @Query("SELECT * FROM album_table WHERE albumUID LIKE :uid")
    public abstract Album findAlbumByUID(String uid);

    @Query("SELECT * FROM album_table WHERE album_name LIKE '%' || :query || '%' ORDER BY album_name")
    public abstract List<Album> searchAlbums(String query);


    // Get songs for album
    @Query("SELECT * FROM song_table WHERE album_uid LIKE :albumUID")
    public abstract List<Song> getSongsForAlbum(String albumUID);


    // Insert album(s)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAll(Album... albums);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Album album);


    // Delete album(s)
    @Delete
    public abstract void delete(Album album);
}
