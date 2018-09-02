package com.smartmusic.android.smartmusicplayer.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.smartmusic.android.smartmusicplayer.SongEvent;
import com.smartmusic.android.smartmusicplayer.SongEventHandler;
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
import java.util.List;

@Database(entities = {Song.class, Playlist.class, Artist.class, Album.class, Stat.class, SongPlaylistJoin.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class SPDatabase extends RoomDatabase {
    public abstract SongDao songDao();
    public abstract PlaylistDao playlistDao();
    public abstract ArtistDao artistDao();
    public abstract AlbumDao albumDao();
    public abstract SongPlaylistJoinDao songPlaylistJoinDao();

    public static final String DATABASE_NAME = "SmartPlayerDatabase";
    private static SPDatabase INSTANCE; // singleton to prevent having multiple instances of the database opened at the same time.

    private static SongEventHandler mEventHandler = new SongEventHandler();

    /**
     * Returns the instance of the database. Ensures that
     * there is only ever one instance of the database.
     * @param ctx the current context
     * @return the database instance.
     */
    public static SPDatabase getDatabase(final Context ctx) {
        if (INSTANCE == null) {
            synchronized (SPDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(ctx.getApplicationContext(),
                            SPDatabase.class, DATABASE_NAME)
                            .build();
                    if(!doesDatabaseExist(ctx, DATABASE_NAME)) {
                        new PopulateDbAsync(INSTANCE, ctx).execute();
                    }
                }
            }
        }
        return INSTANCE;
    }

    public static SPDatabase getTestDatabase(final Context ctx) {
        if (INSTANCE == null) {
            synchronized (SPDatabase.class) {
                INSTANCE = Room.inMemoryDatabaseBuilder(ctx.getApplicationContext(),
                        SPDatabase.class)
                        .build();
            }
        }
        return INSTANCE;
    }

    /**
     * Takes the database file name and determines whether the file
     * exists.
     * @param context the current context
     * @param dbName the database file name
     * @return true if it exists, false otherwise
     */
    public static boolean doesDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    /**
     * Parses the device's local storage to return the number
     * of songs.
     * @param context the current context
     * @return the number of songs found in the media store
     */
    public static int getTotalSongCount(Context context) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI; //locates media
        String selection = MediaStore.Audio.Media.IS_MUSIC+"!=0";
        Cursor cursor = context.getContentResolver().query(uri,null,selection,null,null);
        if(cursor != null){
            return cursor.getCount();
        }
        return 0;
    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
        SPDatabase db;
        Context context;

        PopulateDbAsync(SPDatabase db, Context ctx) {
            this.db = db;
            this.context = ctx;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            loadSongsFromDevice();
            addAlbumsToArtists();
            return null;
        }

        /**
         * Parses the media store and loads each song
         * into the database.
         */
        private void loadSongsFromDevice(){
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI; //locates media
            String selection = MediaStore.Audio.Media.IS_MUSIC+"!=0";
            Cursor cursor = context.getContentResolver().query(uri,null,selection,null,null);
            if(cursor != null){
                if(cursor.moveToFirst()){
                    do{
                        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                        String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        int track = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK));
                        int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                        String year = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.YEAR));
                        String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
//                        long dateAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED));
                        String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
//
//                        String dateString = SPUtils.yearMonthDayFormat.format(new Date(dateAdded)).toString();

                        Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
                        Uri albumArt = ContentUris.withAppendedId(albumArtUri, cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID)));


                        Song s = new Song(name,artist, album, url, albumArt.toString(), track, duration, year, null, size, displayName);

                        addSongToArtist(s);
                        addSongToAlbum(s);

                        db.songDao().insert(s);

                        mEventHandler.dispatchEvent(new SongEvent(s, cursor.getColumnCount(), SongEvent.Type.SONG_ADDED));

                    } while (cursor.moveToNext());
                }

                cursor.close();

            }
        } // end loadFromDevice()

        /**
         * Given a song, checks the database to see if
         * the album exists. If not, it will be created and
         * added. The song is then marked with UID of the album.
         * @param s a Song object
         */
        private void addSongToAlbum(Song s){
            Album existingAlbum = db.albumDao().findAlbumByName(s.getAlbumName());
            if( existingAlbum != null ){
                s.setAlbumUID(existingAlbum.getAlbumUID());
                existingAlbum.setNumSongs(existingAlbum.getNumSongs() + 1);
                db.albumDao().insert(existingAlbum);
                return;
            }

            Album al = new Album(s.getArtistName(), s.getAlbumName(), s.getAlbumArt().toString());
            s.setAlbumUID(al.getAlbumUID());
            al.setNumSongs(al.getNumSongs() + 1);
            db.albumDao().insert(al);
            return;
        }


        /**
         * Given a song, checks the database to see if the
         * artist already exists. If not, it will be created and
         * added. The song is then marked with the UID of the artist.
         * @param s a Song object
         */
        private void addSongToArtist(Song s){
            Artist existingArtist = db.artistDao().findArtistByName(s.getArtistName());
            if( existingArtist != null ){
                s.setArtistUID(existingArtist.getArtistUID());
                existingArtist.setNumSongs(existingArtist.getNumSongs() + 1);
                db.artistDao().insert(existingArtist);
                return;
            }

            Artist artist = new Artist(s.getArtistName());
            s.setArtistUID(artist.getArtistUID());
            artist.setNumSongs(artist.getNumSongs() + 1);
            db.artistDao().insert(artist);
            return;
        }

        private void addAlbumsToArtists(){
            List<Album> allAlbums = db.albumDao().getAllAlbumsStatic();
            for(Album album : allAlbums){
                Artist artist = db.artistDao().findArtistByName(album.getArtistName());
                artist.setNumAlbums(artist.getNumAlbums() + 1);
                db.artistDao().insert(artist);
            }
        }
    } // end AsyncTask
}
