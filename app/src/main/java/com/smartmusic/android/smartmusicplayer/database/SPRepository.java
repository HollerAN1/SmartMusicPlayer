package com.smartmusic.android.smartmusicplayer.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.content.Context;
import android.os.AsyncTask;

import com.smartmusic.android.smartmusicplayer.database.daos.AlbumDao;
import com.smartmusic.android.smartmusicplayer.database.daos.ArtistDao;
import com.smartmusic.android.smartmusicplayer.database.daos.PlaylistDao;
import com.smartmusic.android.smartmusicplayer.database.daos.SongDao;
import com.smartmusic.android.smartmusicplayer.database.entities.Album;
import com.smartmusic.android.smartmusicplayer.database.entities.AlbumWithSongs;
import com.smartmusic.android.smartmusicplayer.database.entities.Artist;
import com.smartmusic.android.smartmusicplayer.database.entities.ArtistWithSongs;
import com.smartmusic.android.smartmusicplayer.database.entities.Playlist;
import com.smartmusic.android.smartmusicplayer.database.entities.PlaylistWithSongs;
import com.smartmusic.android.smartmusicplayer.database.entities.Song;

import java.util.List;

/**
 * A Repository is a class that abstracts access to multiple data sources.
 * The Repository is not part of the Architecture Components libraries,
 * but is a suggested best practice for code separation and architecture.
 * A Repository class handles data operations.
 * It provides a clean API to the rest of the app for app data.
 */
public class SPRepository {
    // Daos
    private SongDao mSongDao;
    private AlbumDao mAlbumDao;
    private ArtistDao mArtistDao;
    private PlaylistDao mPlaylistDao;

    public SPRepository(Context context) {
        SPDatabase db = SPDatabase.getDatabase(context);
        mSongDao = db.songDao();
        mAlbumDao = db.albumDao();
        mArtistDao = db.artistDao();
        mPlaylistDao = db.playlistDao();
    }


    // Get all objects.
    public LiveData<List<Song>> getAllSongsUnsorted() {
        return mSongDao.getAllUnsorted();
    }

    public LiveData<List<Artist>> getAllArtists() {
        return mArtistDao.getAll();
    }

    public LiveData<List<Album>> getAllAlbums() {
        return mAlbumDao.getAll();
    }

    public LiveData<List<Playlist>> getAllPlaylists() {
        return mPlaylistDao.getAll();
    }

    public LiveData<List<Song>> getAllSongsNameSort() { return mSongDao.getAllNameSort(); }
    public LiveData<List<Song>> getAllSongsArtistSort() { return mSongDao.getAllArtistSort(); }
    public LiveData<List<Song>> getAllSongsAlbumSort() { return mSongDao.getAllAlbumSort(); }

    // Insert new objects.
    public void insert (Song song) {
        new insertAsyncTask(mSongDao).execute(song);
    }

    public void insert(Album album) {
        new insertAsyncTask(mAlbumDao).execute(album);
    }

    public void insert(Artist artist) {
        new insertAsyncTask(mArtistDao).execute(artist);
    }

    public void insert(Playlist playlist) {
        new insertAsyncTask(mPlaylistDao).execute(playlist);
    }



    private static class insertAsyncTask extends AsyncTask<Object, Void, Void> {

        private Object mAsyncTaskDao;

        insertAsyncTask(Object dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Object... params) {
            if( params[0] instanceof Song ){
                SongDao songDao = (SongDao)mAsyncTaskDao;
                Song song = (Song)params[0];
                songDao.insert(song);
                return null;
            } else if ( params[0] instanceof Album ) {
                AlbumDao albumDao = (AlbumDao)mAsyncTaskDao;
                Album album = (Album)params[0];
                albumDao.insert(album);
                return null;
            } else if ( params[0] instanceof Artist ) {
                ArtistDao artistDao = (ArtistDao)mAsyncTaskDao;
                Artist artist = (Artist)params[0];
                artistDao.insert(artist);
                return null;
            } else if ( params[0] instanceof Playlist ) {
                PlaylistDao playlistDao = (PlaylistDao)mAsyncTaskDao;
                Playlist playlist = (Playlist)params[0];
                playlistDao.insert(playlist);
                return null;
            } else {
                return null;
            }
        }
    }
}
