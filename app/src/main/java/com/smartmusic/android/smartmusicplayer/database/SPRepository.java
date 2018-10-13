package com.smartmusic.android.smartmusicplayer.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import com.smartmusic.android.smartmusicplayer.database.entities.Album;
import com.smartmusic.android.smartmusicplayer.database.entities.Artist;
import com.smartmusic.android.smartmusicplayer.database.entities.Playlist;
import com.smartmusic.android.smartmusicplayer.database.entities.Song;

import java.util.List;

/**
 * Class that communicates with database to
 * load/store SmartPlayer media.
 */
public class SPRepository {

    private SPDatabase database;

    public SPRepository(Context context){
        this.database = RoomSQLDatabase.getDatabase(context);
    }

    // Get songs
    public LiveData<List<Song>> getAllSongsUnsorted() { return database.getAllSongsNoSort(); }
    public LiveData<List<Song>> getAllSongsNameSort() { return database.getAllSongsNameSort(); }
    public LiveData<List<Song>> getAllSongsArtistSort() { return database.getAllSongsArtistSort(); }
    public LiveData<List<Song>> getAllSongsAlbumSort() { return database.getAllSongsAlbumSort(); }

    public LiveData<List<Song>> getSongsForArtist(String artistUID){ return database.getSongsForArtist(artistUID); }
    public LiveData<List<Song>> getSongsForAlbum(String albumUID){ return database.getSongsForAlbum(albumUID); }
    public LiveData<List<Song>> getSongsForPlaylist(String playlistUID){ return database.getSongsForPlaylist(playlistUID); }

    // Get artists
    public LiveData<List<Artist>> getAllArtistsUnsorted() { return database.getAllArtistsNoSort(); }
    public LiveData<List<Artist>> getAllArtistsNameSort() { return database.getAllArtistsNameSort(); }
    public LiveData<List<Artist>> getAllArtistsSongCountSort() { return  database.getAllArtistsBySongCount(); }

    // Get albums
    public LiveData<List<Album>> getAllAlbumsUnsorted() { return database.getAllAlbumsNoSort(); }
    public LiveData<List<Album>> getAllAlbumsNameSort() { return database.getAllAlbumsNameSort(); }
    public LiveData<List<Album>> getAllAlbumsArtistSort() { return  database.getAllAlbumsArtistSort(); }
    public LiveData<List<Album>> getAllAlbumsSongCountSort() { return  database.getAllAlbumsBySongCount(); }

    // Get playlists
    public LiveData<List<Playlist>> getAllPlaylistsUnsorted(){ return database.getAllPlaylistsNoSort();}
    public LiveData<List<Playlist>> getAllPlaylistsNameSort(){ return database.getAllPlaylistsNameSort(); }
    public LiveData<List<Playlist>> getAllPlaylistsSongCountSort(){ return database.getAllPlaylistsBySongCount(); }


    public int getTotalSongCount(){ return database.getSongCount(); }
    public int getTotalArtistCount(){ return database.getArtistCount(); }
    public int getTotalAlbumCount(){ return database.getAlbumCount(); }

    public List<Song> searchSongs(String query){ return this.database.searchSongsWithQuery(query); }
    public List<Artist> searchArtists(String query){ return this.database.searchArtistsWithQuery(query); }
    public List<Album> searchAlbums(String query){ return this.database.searchAlbumsWithQuery(query); }
    public List<Playlist> searchPlaylists(String query){ return this.database.searchPlaylistsWithQuery(query); }

    // Insert objects into database
    public void insert(Song song){ new InsertAsyncTask(database).execute(song); }
    public void insert(Artist artist){ new InsertAsyncTask(database).execute(artist); }
    public void insert(Album album){ new InsertAsyncTask(database).execute(album); }
    public void insert(Playlist playlist){ new InsertAsyncTask(database).execute(playlist); }

    public void insertAllSongs(List<Song> songs){ new InsertAsyncTask(database).execute(songs); }
    public void insertAllArtists(List<Artist> artists){ new InsertAsyncTask(database).execute(artists); }
    public void insertAllAlbumsl(List<Album> albums){ new InsertAsyncTask(database).execute(albums); }
    public void insertAllPlaylists(List<Playlist> playlists){ new InsertAsyncTask(database).execute(playlists); }

    // Load objects from database
    // Note: must be called in a background thread!
    public Song getSongByName(String songName){ return database.loadSongByName(songName); }
    public Song getSongByUID(String songUID){ return database.loadSongByUID(songUID); }

    public Artist getArtistByName(String artistName){ return database.loadArtistByName(artistName); }
    public Artist getArtistByUID(String artistUID){ return database.loadArtistByUID(artistUID); }

    public Album getAlbumByCredentials(String albumName, String artistName){ return database.loadAlbumByCredentials(albumName, artistName); }
    public Album getAlbumByUID(String albumUID){ return database.loadAlbumByUID(albumUID); }

    public Playlist getPlaylistByName(String playlistName){ return database.loadPlaylistByName(playlistName); }
    public Playlist getPlaylistByUID(String playlistUID){ return database.loadPlaylistByUID(playlistUID); }


    private static class InsertAsyncTask extends AsyncTask<Object, Void, Void> {
        private SPDatabase database;

        InsertAsyncTask(SPDatabase database){
            this.database = database;
        }

        @Override
        protected Void doInBackground(Object... params) {
            for (Object param : params) {
                if (param instanceof Song) {
                    Song song = (Song) param;
                    database.storeSong(song);
                    return null;
                } else if (param instanceof Album) {
                    Album album = (Album) param;
                    database.storeAlbum(album);
                    return null;
                } else if (param instanceof Artist) {
                    Artist artist = (Artist) param;
                    database.storeArtist(artist);
                    return null;
                } else if (param instanceof Playlist) {
                    Playlist playlist = (Playlist) param;
                    database.storePlaylist(playlist);
                    return null;
                }
            }
            return null;
        }
    }
}
