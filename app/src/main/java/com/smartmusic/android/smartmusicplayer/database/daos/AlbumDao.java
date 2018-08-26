package com.smartmusic.android.smartmusicplayer.database.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.smartmusic.android.smartmusicplayer.database.entities.Album;
import com.smartmusic.android.smartmusicplayer.database.entities.AlbumWithSongs;
import com.smartmusic.android.smartmusicplayer.database.entities.Song;

import java.util.List;

@Dao
public abstract class AlbumDao {
    @Query("SELECT * FROM album_table")
    public abstract LiveData<List<AlbumWithSongs>> getAllWithSongs(); // LiveData ensures data is updated on database changes

    @Query("SELECT * FROM album_table")
    public abstract LiveData<List<Album>> getAll();

    @Query("SELECT * FROM album_table")
    public abstract List<Album> getAllUnchanging();

    @Query("SELECT * FROM album_table WHERE album_name LIKE :name")
    public abstract Album findAlbumByName(String name);

    @Query("SELECT * FROM album_table WHERE uid LIKE :uid")
    public abstract Album findAlbumByUID(String uid);

    @Query("SELECT * FROM song_table WHERE album_uid LIKE :albumUID")
    public abstract LiveData<List<Song>> getSongsForAlbum(String albumUID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAll(Album... albums);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Album album);

    @Delete
    public abstract void delete(Album album);

    @Query("SELECT * FROM song_table WHERE album_uid LIKE :albumUID")
    public abstract LiveData<List<Song>> getAllSongsForAlbum(String albumUID);


//    public void insertSongForAlbum(Album album, Song song){
//        song.setAlbumUID(album.getUid());
//        insert(song);
//    }

//    public void insertSongsForAlbum(Album album, List<Song> songs){
//        for(Song song : songs) {
//            insertSongForAlbum(album, song);
//        }
//    }

//    public void removeSongsFromAlbum(Album album, List<Song> songs){
//        for(Song song : songs){
//            removeSongFromAlbum(album, song);
//        }
//    }

//    public void removeSongFromAlbum(Album album, Song song){
//        song.setAlbumUID("");
//        insert(song);
//    }
}
