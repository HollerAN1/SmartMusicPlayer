package com.smartmusic.android.smartmusicplayer.database.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.smartmusic.android.smartmusicplayer.database.entities.Album;
import com.smartmusic.android.smartmusicplayer.database.entities.Artist;
import com.smartmusic.android.smartmusicplayer.database.entities.Song;

import java.util.List;

@Dao
public abstract class ArtistDao {

    // Get list of artists depending on sorting preference
    @Query("SELECT * FROM artist_table")
    public abstract List<Artist> getAllArtistsStatic(); // Will not update on db change

    @Query("SELECT * FROM artist_table")
    public abstract LiveData<List<Artist>> getAllUnsorted();

    @Query("SELECT * FROM artist_table ORDER BY artist_name")
    public abstract LiveData<List<Artist>> getAllNameSort();

    @Query("SELECT * FROM artist_table ORDER BY number_of_songs")
    public abstract LiveData<List<Artist>> getAllSongCountSort();


    // Find artist by fields
    @Query("SELECT * FROM artist_table WHERE artist_name LIKE :name")
    public abstract Artist findArtistByName(String name);

    @Query("SELECT * FROM artist_table WHERE artistUID LIKE :uid")
    public abstract Artist findArtistByUID(String uid);

    @Query("SELECT * FROM artist_table WHERE artist_name LIKE '%' || :query || '%' ORDER BY artist_name")
    public abstract List<Artist> searchArtists(String query);

    @Query("SELECT COUNT(artist_name) FROM artist_table")
    public abstract int getNumArtists();


    // Get songs for artist
    @Query("SELECT * FROM song_table WHERE artist_uid LIKE :artistUID")
    public abstract LiveData<List<Song>> getSongsForArtist(String artistUID);


    // Get albums for artist
    @Query("SELECT * FROM album_table WHERE artist_uid LIKE :artistUID")
    public abstract LiveData<List<Album>> getAlbumsForArtist(String artistUID);

    // Insert artist(s)
    // Note: Only saves artist information. Related song
    //       information requires additional save.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAll(Artist... artists);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Artist artist);


    // Delete artist(s)
    @Delete
    public abstract void delete(Artist artist);
    @Delete
    public abstract void deleteAll(List<Artist> artists);
}
