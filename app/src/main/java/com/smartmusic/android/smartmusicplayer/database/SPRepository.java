package com.smartmusic.android.smartmusicplayer.database;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import com.smartmusic.android.smartmusicplayer.database.daos.AlbumDao;
import com.smartmusic.android.smartmusicplayer.database.daos.ArtistDao;
import com.smartmusic.android.smartmusicplayer.database.daos.PlaylistDao;
import com.smartmusic.android.smartmusicplayer.database.daos.SongDao;
import com.smartmusic.android.smartmusicplayer.database.daos.SongPlaylistJoinDao;
import com.smartmusic.android.smartmusicplayer.database.entities.Album;
import com.smartmusic.android.smartmusicplayer.database.entities.Artist;
import com.smartmusic.android.smartmusicplayer.database.entities.Playlist;
import com.smartmusic.android.smartmusicplayer.database.entities.Song;
import com.smartmusic.android.smartmusicplayer.database.entities.SongPlaylistJoin;

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
    private SongPlaylistJoinDao mSongPlaylistJoinDao;

    public SPRepository(Context context) {
        SPDatabase db = SPDatabase.getDatabase(context);
        mSongDao = db.songDao();
        mAlbumDao = db.albumDao();
        mArtistDao = db.artistDao();
        mPlaylistDao = db.playlistDao();
        mSongPlaylistJoinDao = db.songPlaylistJoinDao();
    }


    // Get all objects without relations.
    public LiveData<List<Song>> getAllSongsUnsorted() {
        return mSongDao.getAllUnsorted();
    }
    public LiveData<List<Song>> getAllSongsNameSort() { return mSongDao.getAllNameSort(); }
    public LiveData<List<Song>> getAllSongsArtistSort() { return mSongDao.getAllArtistSort(); }
    public LiveData<List<Song>> getAllSongsAlbumSort() { return mSongDao.getAllAlbumSort(); }

    public LiveData<List<Artist>> getAllArtistsUnsorted() {
        return mArtistDao.getAllUnsorted();
    }
    public LiveData<List<Artist>> getAllArtistsNameSort() { return mArtistDao.getAllNameSort(); }
    public LiveData<List<Artist>> getAllArtistsSongCountSort() { return  mArtistDao.getAllSongCountSort(); }

    public LiveData<List<Album>> getAllAlbumsUnsorted() {
        return mAlbumDao.getAllUnsorted();
    }
    public LiveData<List<Album>> getAllAlbumsNameSort() { return mAlbumDao.getAllNameSort(); }
    public LiveData<List<Album>> getAllAlbumsArtistSort() { return  mAlbumDao.getAllArtistSort(); }
    public LiveData<List<Album>> getAllAlbumsSongCountSort() { return  mAlbumDao.getAllSongCountSort(); }

    public LiveData<List<Playlist>> getAllPlaylists() {
        return mPlaylistDao.getAll();
    }

    public int getTotalSongCount(){
        List<Song> songs = mSongDao.getAllSongsStatic();
        return songs != null ? songs.size() : 0;
    }

    public int getTotalAlbumCount(){
        List<Album> albums = mAlbumDao.getAllAlbumsStatic();
        return albums != null ? albums.size() : 0;
    }

    public int getTotalArtistCount(){
        List<Artist> artists = mArtistDao.getAllArtistsStatic();
        return artists != null ? artists.size() : 0;
    }

    public Artist getArtistFull(String artistUID){
        Artist artist = mArtistDao.findArtistByUID(artistUID);
        List<Song> songs = mArtistDao.getSongsForArtist(artistUID);
        List<Album> albums = mArtistDao.getAlbumsForArtist(artistUID);
        artist.setSongs(songs);
        artist.setAlbums(albums);
        return artist;
    }

    public Album getAlbumFull(String albumUID){
        Album album = mAlbumDao.findAlbumByUID(albumUID);
        List<Song> songs = mAlbumDao.getSongsForAlbum(albumUID);
        album.setSongs(songs);
        return album;
    }

    /**
     * Adds song to playlist in database.
     *
     * Pre-condition: The song and the playlist must already exist
     * in the database!!!
     *
     * @param playlist the playlist
     * @param song the song to add to the playlist
     */
    public void addSongToPlaylist(Playlist playlist, Song song){
        mSongPlaylistJoinDao.insert(new SongPlaylistJoin(song.getSongUID(), playlist.getPlaylistUID()));
    }

    public Playlist getPlaylistFull(String playlistUID){
        Playlist playlist = mPlaylistDao.findPlaylistByUID(playlistUID);
        List<Song> songs = mSongPlaylistJoinDao.getSongsForPlaylist(playlistUID);
        playlist.setSongs(songs);
        return playlist;
    }



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
