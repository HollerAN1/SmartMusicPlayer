package com.smartmusic.android.smartmusicplayer;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import com.smartmusic.android.smartmusicplayer.model.AlbumInfo;
import com.smartmusic.android.smartmusicplayer.model.ArtistInfo;
import com.smartmusic.android.smartmusicplayer.model.PlaylistInfo;
import com.smartmusic.android.smartmusicplayer.model.SongInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Database service that runs as long as the
 * app is in the foreground/background.
 *
 * Created by holle on 7/29/2018.
 */

public class SPDatabaseService extends Service {


    private SongEventHandler mEventHandler = new SongEventHandler();

    // Binder given to clients
    private boolean mBound = false;
    private final IBinder mBinder = new SPDatabaseBinder();

    private static final List<SongInfo> _songs = Collections.synchronizedList(new ArrayList<SongInfo>());
    private static final List<AlbumInfo> _albums = Collections.synchronizedList(new ArrayList<AlbumInfo>());
    private static final List<ArtistInfo> _artists = Collections.synchronizedList(new ArrayList<ArtistInfo>());
    private static final List<PlaylistInfo> _playlists = Collections.synchronizedList(new ArrayList<PlaylistInfo>());


    public class SPDatabaseBinder extends Binder {
        public SPDatabaseService getService() {
            // Return this instance of SPDatabaseBinder so clients
            // can call public methods
            return SPDatabaseService.this;
        }
    } // end SPDatabaseBinder

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(getString(R.string.APP_LOGGER),
                intent.getStringExtra(getString(R.string.EXTRA_SENDER)) + " bound to SPDatabaseService");
        mBound = true;
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(getString(R.string.APP_LOGGER),
                intent.getStringExtra(getString(R.string.EXTRA_SENDER)) + " unbound from SPDatabaseService");
        mBound = false;
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(getString(R.string.APP_LOGGER),
                "SPDatabaseService started");
//        loadDatabase();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(getString(R.string.APP_LOGGER),
                "SPDatabaseService stopped");
    }

    /* ------------- Public methods clients can call ----------------- */

    public boolean isBound(){
        return this.mBound;
    }

    public ArrayList<SongInfo> getSongs(Comparator<SongInfo> sort){
        synchronized ( _songs ) {
            ArrayList<SongInfo> listCopy = new ArrayList<>();
            Iterator<SongInfo> iter = _songs.iterator();

            while(iter.hasNext()) {
                SongInfo s = iter.next();
                listCopy.add(s);
            }

            if( sort != null ) {
                Collections.sort(listCopy, sort);
            }

            return listCopy;
        }
    }


    public ArrayList<AlbumInfo> getAlbums(Comparator<AlbumInfo> sort) {
        synchronized ( _albums ) {
            ArrayList<AlbumInfo> listCopy = new ArrayList<>();
            Iterator<AlbumInfo> iter = _albums.iterator();

            while(iter.hasNext()) {
                AlbumInfo al = iter.next();
                listCopy.add(al);
            }

            if( sort != null ) {
                Collections.sort(listCopy, sort);
            }

            return listCopy;
        }
    }

    public ArrayList<ArtistInfo> getArtists(Comparator<ArtistInfo> sort) {
        synchronized ( _artists ) {
            ArrayList<ArtistInfo> listCopy = new ArrayList<>();
            Iterator<ArtistInfo> iter = _artists.iterator();

            while(iter.hasNext()) {
                ArtistInfo a = iter.next();
                listCopy.add(a);
            }

            if( sort != null ) {
                Collections.sort(listCopy, sort);
            }

            return listCopy;
        }
    }

    public ArrayList<PlaylistInfo> getPlaylists(Comparator<PlaylistInfo> sort) {
        synchronized ( _playlists ) {
            ArrayList<PlaylistInfo> listCopy = new ArrayList<>();
            Iterator<PlaylistInfo> iter = _playlists.iterator();

            while(iter.hasNext()) {
                PlaylistInfo pl = iter.next();
                listCopy.add(pl);
            }

            if( sort != null ) {
                Collections.sort(listCopy, sort);
            }

            return listCopy;
        }
    }

    public void addPlaylist( PlaylistInfo pl ) {
        synchronized ( _playlists ){
            Log.d(getString(R.string.APP_LOGGER), "Adding playlist " + pl.getName());
            _playlists.add(pl);
        }
    }

    public void removePlaylist( PlaylistInfo pl ) {
        synchronized ( _playlists ) {
            Log.d(getString(R.string.APP_LOGGER), "Removing playlist " + pl.getName());
            int i = _playlists.indexOf(pl);
            _playlists.remove(i);
        }
    }

    public int getTotalSongCount() {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI; //locates media
        String selection = MediaStore.Audio.Media.IS_MUSIC+"!=0";
        Cursor cursor = this.getContentResolver().query(uri,null,selection,null,null);
        if(cursor != null){
            Log.d(getString(R.string.APP_LOGGER), "Total song count is " + cursor.getCount());
            return cursor.getCount();
        }
        Log.d(getString(R.string.APP_LOGGER), "Unable to get song count, cursor is null");
        return 0;
    }

    public void loadDatabase(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(getString(R.string.APP_LOGGER), "SPDatabaseService is loading database");
                /*Uniform Resource Identifier (URI)
                A Uri object is usually used to tell a ContentProvider what
                we want to access by reference. It is an immutable one-to-one
                 mapping to a resource or data. The method Uri.parse creates
                  a new Uri object from a properly formatted String.
                * */
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI; //locates media
                String selection = MediaStore.Audio.Media.IS_MUSIC+"!=0";
                Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
                if(cursor != null){
                    if(cursor.moveToFirst()){
                        do{
                            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                            String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                            String track = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK));
                            String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                            String year = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.YEAR));
                            String album= cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));

                            Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
                            Uri albumArt = ContentUris.withAppendedId(albumArtUri, cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID)));


                            SongInfo s = new SongInfo(name,artist, album, url, albumArt);
                            _songs.add(s);


                            updateArtists(s);
                            updateAlbums(s);

                            mEventHandler.dispatchEvent(new SongEvent(s, cursor.getColumnCount(), SongEvent.Type.SONG_ADDED));

                            // Gives time for song loading
                            // animation to display.
                            try {
                                Thread.sleep(5);
                            } catch (InterruptedException ex) {

                            }
                        } while (cursor.moveToNext());
                    }

                    cursor.close();

                }
            }
        });

        thread.start();
    }

    private void updateArtists(SongInfo s){
        for(ArtistInfo a : _artists){
            if(a.getArtistname().equals(s.getArtistname())){
                a.addSong(s);
                return;
            }
        }
        ArtistInfo artist = new ArtistInfo(s.getArtistname());
        artist.addSong(s);
        _artists.add(artist);
    }

    private void updateAlbums(SongInfo s){
        for(AlbumInfo a : _albums){
            if(a.getAlbumName().equals(s.getAlbumname())){
                a.addSong(s);
                return;
            }
        }
        String artistname;
        if(s.getAlbumname().equals("<unknown>")){
            artistname = "<unknown>";
        } else {
            artistname = s.getArtistname();
        }
        AlbumInfo al = new AlbumInfo(artistname, s.getAlbumname(), s.getAlbumArt());
        al.addSong(s);
        _albums.add(al);
    }

    public Integer getRandomInt(){
        Random random = new Random();
        return  random.nextInt();
    }

} // end com.smartmusic.android.smartmusicplayer.SPDatabaseService
