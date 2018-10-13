package com.smartmusic.android.smartmusicplayer.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.smartmusic.android.smartmusicplayer.events.SongDatabaseEvent;
import com.smartmusic.android.smartmusicplayer.events.SongEvent;
import com.smartmusic.android.smartmusicplayer.events.SongEventHandler;
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
import com.smartmusic.android.smartmusicplayer.database.entities.Stat;

import java.io.File;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.List;

@Database(entities = {Song.class, Playlist.class, Artist.class, Album.class, Stat.class, SongPlaylistJoin.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class RoomSQLDatabase
        extends RoomDatabase
        implements SPDatabase
{
    // public DAOs for testing purposes
    // and are required by Room API
    //
    // ONLY SPDatabase override methods should access these!
    public abstract SongDao songDao();
    public abstract PlaylistDao playlistDao();
    public abstract ArtistDao artistDao();
    public abstract AlbumDao albumDao();
    public abstract SongPlaylistJoinDao songPlaylistJoinDao();

    private static final String DEFAULT_UNKNOWN_STR = "<Unknown";
    private static final String DATABASE_NAME = "SmartPlayerDatabase3";
    private static RoomSQLDatabase INSTANCE; // singleton to prevent having multiple instances of the database opened at the same time.

    private static SongEventHandler mEventHandler = new SongEventHandler();

    /**
     * Returns the instance of the database. Ensures that
     * there is only ever one instance of the database.
     * @param ctx the current context
     * @return the database instance.
     */
    public static SPDatabase getDatabase(final Context ctx) {
        if (INSTANCE == null) {
            synchronized (RoomSQLDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(ctx.getApplicationContext(),
                            RoomSQLDatabase.class, DATABASE_NAME)
                            .build();
                    new PopulateDbAsyncTask(INSTANCE, ctx.getContentResolver()).execute();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Returns a temporary database.
     * @param ctx the current context
     * @return the database instance
     */
    public static RoomSQLDatabase getTestDatabase(final Context ctx) {
        if (INSTANCE == null) {
            synchronized (RoomSQLDatabase.class) {
                INSTANCE = Room.inMemoryDatabaseBuilder(ctx.getApplicationContext(),
                        RoomSQLDatabase.class)
                        .build();
            }
        }
        return INSTANCE;
    }

    /**
     * Takes the database file name and determines whether the file
     * exists.
     * @param context the current context
     * @return true if it exists, false otherwise
     */
    public static boolean doesDatabaseExist(Context context) {
        File dbFile = context.getDatabasePath(DATABASE_NAME);
        return dbFile.exists();
    }

    /**
     * Parses the device's local storage to return the number
     * of songs.
     * @param contentResolver the application content resolver
     * @return the number of songs found in the media store
     */
    public static int getNumberOfSongsStoredOnDevice(ContentResolver contentResolver) {
        int songCount = 0;
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI; //locates media
        String selection = MediaStore.Audio.Media.IS_MUSIC+"!=0";
        Cursor cursor = contentResolver.query(uri,null,selection,null,null);
        if(cursor != null){
            songCount = cursor.getCount();
        }
        cursor.close();     // free cursor
        return songCount;
    }

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Boolean> {
        // TODO: Refactor the startup process of loading songs and recognizing changes in db
        RoomSQLDatabase db;
        ContentResolver contentResolver;

        PopulateDbAsyncTask(RoomSQLDatabase db, ContentResolver contentResolver) {
            this.db = db;
            this.contentResolver = contentResolver;
        }

        @Override
        protected Boolean doInBackground(final Void... params) {
            boolean successfulLoad = false;
            int songsInDatabase = db.getSongCount();
            if(getNumberOfSongsStoredOnDevice(contentResolver) != songsInDatabase) {
                // Wipe database of song info
                db.deleteAllSongs();
                db.deleteAllArtists();
                db.deleteAllAlbums();

                // Perform a reload of database
                successfulLoad = loadSongsFromDeviceIntoDB();
            }
            return successfulLoad;
        }

        @Override
        protected void onPostExecute(Boolean successfulLoad) {
            super.onPostExecute(successfulLoad);
            // TODO: If false show error message.
        }

        /**
         * Parses the media store and loads each song
         * into the database.
         * @return true if loaded successfully, false if there was an error
         */
        private boolean loadSongsFromDeviceIntoDB(){
            boolean error = false;
            Cursor mediaCursor = contentResolver
                                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                        null,
                                        MediaStore.Audio.Media.IS_MUSIC + "!=0",
                                        null,
                                        null);
//            Cursor albumCursor = contentResolver
//                                .query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
//                                        new String[] {MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
//                                MediaStore.Audio.Albums._ID+ "=?",
//                                        null,
//                                        null);
            if ( (mediaCursor != null) ) {
                if ( (mediaCursor.moveToFirst()) ) {
                    do {
                        try {
                            String name = mediaCursor.getString(mediaCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                            String artist = mediaCursor.getString(mediaCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                            String url = mediaCursor.getString(mediaCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                            int track = mediaCursor.getInt(mediaCursor.getColumnIndex(MediaStore.Audio.Media.TRACK));
                            int duration = mediaCursor.getInt(mediaCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                            String year = mediaCursor.getString(mediaCursor.getColumnIndex(MediaStore.Audio.Media.YEAR));
                            String album = mediaCursor.getString(mediaCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
//                        long dateAdded = mediaCursor.getLong(mediaCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED));
                            String displayName = mediaCursor.getString(mediaCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                            long size = mediaCursor.getLong(mediaCursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
//                            String path = albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
//
//                        String dateString = SPUtils.yearMonthDayFormat.format(new Date(dateAdded)).toString();

                            Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
                            Uri albumArt
                                    = ContentUris
                                    .withAppendedId( albumArtUri,
                                                     mediaCursor.getInt(mediaCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID)));


                            Song s = new Song(name,
                                    artist,
                                    album,
                                    url,
                                    albumArt.toString(),
                                    track, duration, year,
                                    null,
                                    size, displayName);

                            updateDatabaseForSong(s);

                            mEventHandler.dispatchEvent(new SongDatabaseEvent(s, SongEvent.Type.SONG_ADDED));
                        } catch (Exception e){
                            e.printStackTrace();
                            error = true;
                        }

                    } while ( (mediaCursor.moveToNext()) );
                }

//                albumCursor.close();
                mediaCursor.close();

            } else {
                error = true;
            }
            return error;
        } // end loadFromDevice()

        private void updateDatabaseForSong(Song s){
            Artist artist = updateOrInitializeArtistWith(s);
            Album album = updateOrInitializeAlbumWith(s);
            db.addAlbumToArtist(album, artist);

            db.storeSong(s);
        }

        /**
         * Given a song, checks the database to see if
         * the album exists. If not, it will be created and
         * added. The song is then marked with UID of the album.
         * @param s a Song object
         */
        private Album updateOrInitializeAlbumWith(Song s){
            Album existingAlbum = db.loadAlbumByCredentials(!(s.getAlbumName().equals(""))
                                                            ? s.getAlbumName()
                                                            : DEFAULT_UNKNOWN_STR,
                                                            !(s.getArtistName().equals(""))
                                                            ? s.getArtistName()
                                                            : DEFAULT_UNKNOWN_STR);
            if(existingAlbum != null){
                // Update existing album
                db.addSongToAlbum(existingAlbum, s);
                return existingAlbum;
            } else {
                // Create new album
                String albumName = DEFAULT_UNKNOWN_STR;
                if (!(s.getAlbumName().isEmpty()) && !(s.getAlbumName().equals(""))) {
                    albumName = s.getAlbumName();
                }
                Album newAlbum = new Album(albumName, s.getArtistName(), s.getAlbumArtUri());
                db.addSongToAlbum(newAlbum, s);
                return newAlbum;
            }
        }


        /**
         * Given a song, checks the database to see if the
         * artist already exists. If not, it will be created and
         * added. The song is then marked with the UID of the artist.
         * @param s a Song object
         */
        private Artist updateOrInitializeArtistWith(Song s){
            Artist existingArtist = db.loadArtistByName( !(s.getArtistName().equals(""))
                                                        ? s.getArtistName()
                                                        : DEFAULT_UNKNOWN_STR);
            if( existingArtist != null ){
                // Update existing artist
                db.addSongToArtist(existingArtist, s);
                return existingArtist;
            } else {
                // Create new artist
                String artistName = DEFAULT_UNKNOWN_STR;
                if (!(s.getArtistName().isEmpty()) && !(s.getArtistName().equals(""))) {
                    artistName = s.getArtistName();
                }

                Artist newArtist = new Artist(artistName);
                db.addSongToArtist(newArtist, s);
                return newArtist;
            }
        }
    } // end PopulateDbAsyncTask


    @Override
    public void storeSong(Song song) {
        songDao().insert(song);
    }

    @Override
    public void storeSongs(List<Song> songs) {
        for(Song song : songs){
            storeSong(song);
        }
    }

    @Override
    public Song loadSongByName(String songName) {
        return songDao().findSongByName(songName);
    }

    @Override
    public Song loadSongByUID(String songUID) {
        return songDao().findSongByUID(songUID);
    }

    @Override
    public void storeArtist(Artist artist) {
        artistDao().insert(artist);
    }

    @Override
    public void storeArtists(List<Artist> artists) {
        for(Artist artist : artists){
            storeArtist(artist);
        }
    }

    @Override
    public Artist loadArtistByName(String artistName) {
        return artistDao().findArtistByName(artistName);
    }

    @Override
    public Artist loadArtistByUID(String artistUID) {
        return artistDao().findArtistByUID(artistUID);
    }

    @Override
    public void storeAlbum(Album album) {
        albumDao().insert(album);
    }

    @Override
    public void storeAlbums(List<Album> albums) {
        for(Album album : albums){
            storeAlbum(album);
        }
    }

    @Override
    public Album loadAlbumByCredentials(String albumName, String albumArtist) {
        return albumDao().findAlbumByCredentials(albumName, albumArtist);
    }

    @Override
    public Album loadAlbumByUID(String albumUID) {
        return albumDao().findAlbumByUID(albumUID);
    }

    @Override
    public void storePlaylist(Playlist playlist) {
        playlistDao().insert(playlist);
    }

    @Override
    public void storePlaylists(List<Playlist> playlists) {
        for(Playlist playlist : playlists){
            storePlaylist(playlist);
        }
    }

    @Override
    public Playlist loadPlaylistByName(String playlistName) {
        return playlistDao().findPlaylistByName(playlistName);
    }

    @Override
    public Playlist loadPlaylistByUID(String playlistUID) {
        return playlistDao().findPlaylistByUID(playlistUID);
    }

    @Override
    public void addAlbumToArtist(Album album, Artist artist) {
        artist.setNumAlbums(artist.getNumAlbums() + 1);
        album.setArtistName(artist.getArtistName());
        album.setArtistUid(artist.getArtistUID());
        storeArtist(artist);
        storeAlbum(album);
    }

    @Override
    public void addSongToArtist(final Artist artist, final Song song) {
        song.setArtistUID(artist.getArtistUID());
        artist.setNumSongs(artist.getNumSongs() + 1);
        storeSong(song);
        storeArtist(artist);
    }

    @Override
    public void addSongToAlbum(final Album album, final Song song) {
        song.setAlbumUID(album.getAlbumUID());
        album.setNumSongs(album.getNumSongs() + 1);
        storeSong(song);
        storeAlbum(album);
    }

    @Override
    public void addSongToPlaylist(final Playlist playlist, Song song) {

    }

    @Override
    public List<Song> searchSongsWithQuery(String query) {
        if(!query.equals("")) {
            return songDao().searchSongs(query);
        }
        return new ArrayList<>();
    }

    @Override
    public List<Artist> searchArtistsWithQuery(String query) {
        if(!query.equals("")) {
            return artistDao().searchArtists(query);
        }
        return new ArrayList<>();
    }

    @Override
    public List<Album> searchAlbumsWithQuery(String query) {
        if(!query.equals("")) {
            return albumDao().searchAlbums(query);
        }
        return new ArrayList<>();
    }

    @Override
    public List<Playlist> searchPlaylistsWithQuery(String query) {
        if(!query.equals("")) {
            return playlistDao().searchPlaylists(query);
        }
        return new ArrayList<>();
    }

    @Override
    public int getSongCount() {
        return songDao().getNumSongs();
    }

    @Override
    public int getAlbumCount() {
        return albumDao().getNumAlbums();
    }

    @Override
    public int getArtistCount() {
        return artistDao().getNumArtists();
    }

    @Override
    public int getPlaylistCount() {
        return playlistDao().getNumPlaylists();
    }

    @Override
    public LiveData<List<Song>> getAllSongsNoSort() {
        return songDao().getAllUnsorted();
    }

    @Override
    public LiveData<List<Song>> getAllSongsNameSort() {
        return songDao().getAllNameSort();
    }

    @Override
    public LiveData<List<Song>> getAllSongsArtistSort() {
        return songDao().getAllArtistSort();
    }

    @Override
    public LiveData<List<Song>> getAllSongsAlbumSort() {
        return songDao().getAllAlbumSort();
    }

    @Override
    public LiveData<List<Artist>> getAllArtistsNoSort() {
        return artistDao().getAllUnsorted();
    }

    @Override
    public LiveData<List<Artist>> getAllArtistsNameSort() {
        return artistDao().getAllNameSort();
    }

    @Override
    public LiveData<List<Artist>> getAllArtistsBySongCount() {
        return artistDao().getAllSongCountSort();
    }

    @Override
    public LiveData<List<Album>> getAllAlbumsNoSort() {
        return albumDao().getAllUnsorted();
    }

    @Override
    public LiveData<List<Album>> getAllAlbumsNameSort() {
        return albumDao().getAllNameSort();
    }

    @Override
    public LiveData<List<Album>> getAllAlbumsArtistSort() {
        return albumDao().getAllArtistSort();
    }

    @Override
    public LiveData<List<Album>> getAllAlbumsBySongCount() {
        return albumDao().getAllSongCountSort();
    }

    @Override
    public LiveData<List<Playlist>> getAllPlaylistsNoSort() {
        return playlistDao().getAll();
    }

    @Override
    public LiveData<List<Playlist>> getAllPlaylistsNameSort() {
        return null;
    }

    @Override
    public LiveData<List<Playlist>> getAllPlaylistsBySongCount() {
        return null;
    }

    @Override
    public LiveData<List<Song>> getSongsForAlbum(String albumUID) {
        return albumDao().getSongsForAlbum(albumUID);
    }

    @Override
    public LiveData<List<Song>> getSongsForArtist(String artistUID) {
        return artistDao().getSongsForArtist(artistUID);
    }

    @Override
    public LiveData<List<Song>> getSongsForPlaylist(String playlistUID) {
        return songPlaylistJoinDao().getSongsForPlaylist(playlistUID);
    }

    @Override
    public void deleteSong(Song song) {
        songDao().delete(song);
    }

    @Override
    public void deleteSongs(List<Song> songs) {
        songDao().deleteAll(songs);
    }

    @Override
    public void deleteArtist(Artist artist) {
        artistDao().delete(artist);
    }

    @Override
    public void deleteArtists(List<Artist> artists) {
        artistDao().deleteAll(artists);
    }

    @Override
    public void deleteAlbum(Album album) {
        albumDao().delete(album);
    }

    @Override
    public void deleteAlbums(List<Album> albums) {
        albumDao().deleteAll(albums);
    }

    @Override
    public void deletePlaylist(Playlist playlist) {
        playlistDao().delete(playlist);
    }

    @Override
    public void deletePlaylists(List<Playlist> playlists) {
        playlistDao().deleteAll(playlists);
    }

    @Override
    public void removeSongFromArtist(Artist artist, Song song) {
        song.setArtistUID(null);
        storeSong(song);
    }

    @Override
    public void removeSongFromAlbum(Album album, Song song) {
        song.setAlbumUID(null);
        storeSong(song);
    }

    @Override
    public void removeSongFromPlaylist(Playlist playlist, Song song) {
        // TODO: implement
    }

    @Override
    public void deleteAllSongs() {
        songDao().deleteAllSongs();
    }

    @Override
    public void deleteAllArtists() {
        artistDao().deleteAllArtists();
    }

    @Override
    public void deleteAllAlbums() {
        albumDao().deleteAllAlbums();
    }

    @Override
    public void deleteAllPlaylists() {
        playlistDao().deleteAllPlaylists();
    }
}
