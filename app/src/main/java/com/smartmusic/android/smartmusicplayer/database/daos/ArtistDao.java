package com.smartmusic.android.smartmusicplayer.database.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.smartmusic.android.smartmusicplayer.database.entities.Artist;
import com.smartmusic.android.smartmusicplayer.database.entities.Song;

import java.util.List;

@Dao
public abstract class ArtistDao {
//    @Query("SELECT * FROM artist_table")
//    public abstract LiveData<List<ArtistWithSongs>> getAllWithSongs(); // LiveData ensures data is updated on database changes

    @Query("SELECT * FROM artist_table")
    public abstract LiveData<List<Artist>> getAll();

    @Query("SELECT * FROM artist_table")
    public abstract List<Artist> getAllUnchanging();

    @Query("SELECT * FROM artist_table WHERE artist_name LIKE :name")
    public abstract Artist findArtistByName(String name);

    @Query("SELECT * FROM artist_table WHERE uid LIKE :uid")
    public abstract Artist findArtistByUID(String uid);

    @Query("SELECT * FROM song_table WHERE artist_uid LIKE :artistUID")
    public abstract LiveData<List<Song>> getSongsForArtist(String artistUID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAll(Artist... artists);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Artist artist);

    @Delete
    public abstract void delete(Artist artist);
}
