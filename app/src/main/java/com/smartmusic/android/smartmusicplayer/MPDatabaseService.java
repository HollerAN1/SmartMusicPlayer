package com.smartmusic.android.smartmusicplayer;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;

import com.smartmusic.android.smartmusicplayer.model.AlbumInfo;
import com.smartmusic.android.smartmusicplayer.model.ArtistInfo;
import com.smartmusic.android.smartmusicplayer.model.PlaylistInfo;
import com.smartmusic.android.smartmusicplayer.model.SongInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Database service that runs as long as the
 * app is in the foreground/background.
 *
 * Created by holle on 7/29/2018.
 */

public class MPDatabaseService extends Service {

    // Binder given to clients
    private final IBinder mBinder = new MPDatabaseBinder();

    private static final List<SongInfo> _songs = Collections.synchronizedList(new ArrayList<SongInfo>());
    private static final List<AlbumInfo> _albums = Collections.synchronizedList(new ArrayList<AlbumInfo>());
    private static final List<ArtistInfo> _artists = Collections.synchronizedList(new ArrayList<ArtistInfo>());
    private static final List<PlaylistInfo> _playlists = Collections.synchronizedList(new ArrayList<PlaylistInfo>());


    public class MPDatabaseBinder extends Binder {
        public MPDatabaseService getService() {
            // Return this instance of MPDatabaseBinder so clients
            // can call public methods
            return MPDatabaseService.this;
        }
    } // end MPDatabaseBinder

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }



    /* ------------- Public methods clients can call ----------------- */

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
            _playlists.add(pl);
        }
    }

    public void removePlaylist( PlaylistInfo pl ) {
        synchronized ( _playlists ) {
            int i = _playlists.indexOf(pl);
            _playlists.remove(i);
        }
    }

    private void loadDatabase(){
        /*Uniform Resource Identifier (URI)
        A Uri object is usually used to tell a ContentProvider what
        we want to access by reference. It is an immutable one-to-one
         mapping to a resource or data. The method Uri.parse creates
          a new Uri object from a properly formatted String.
        * */
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI; //locates media
        String selection = MediaStore.Audio.Media.IS_MUSIC+"!=0";
        Cursor cursor = this.getContentResolver().query(uri,null,selection,null,null);
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

                }while (cursor.moveToNext());
            }

            cursor.close();

        }
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

} // end com.smartmusic.android.smartmusicplayer.MPDatabaseService
