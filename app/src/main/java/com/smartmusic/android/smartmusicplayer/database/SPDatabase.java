package com.smartmusic.android.smartmusicplayer.database;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.smartmusic.android.smartmusicplayer.database.entities.Album;
import com.smartmusic.android.smartmusicplayer.database.entities.Artist;
import com.smartmusic.android.smartmusicplayer.database.entities.Playlist;
import com.smartmusic.android.smartmusicplayer.database.entities.Song;

import java.util.List;

/**
 * A class that abstracts the layer between the
 * database and the application.
 */
public interface SPDatabase {

    // store/delete/load songs
     void storeSong(Song song);
     void storeSongs(List<Song> songs);
     void deleteSong(Song song);
     void deleteSongs(List<Song> songs);
     void deleteAllSongs();
     Song loadSongByName(String songName);
     Song loadSongByUID(String songUID);

    // store/delete/load artists
     void storeArtist(Artist artist);
     void storeArtists(List<Artist> artists);
     void deleteArtist(Artist artist);
     void deleteArtists(List<Artist> artists);
     void deleteAllArtists();
     Artist loadArtistByName(String artistName);
     Artist loadArtistByUID(String artistUID);

    // store/delete/load albums
     void storeAlbum(Album album);
     void storeAlbums(List<Album> albums);
     void deleteAlbum(Album album);
     void deleteAlbums(List<Album> albums);
     void deleteAllAlbums();
     Album loadAlbumByCredentials(String albumName, String albumArtist);
     Album loadAlbumByUID(String albumUID);

    // store/delete/load playlists
     void storePlaylist(Playlist playlist);
     void storePlaylists(List<Playlist> playlists);
     void deletePlaylist(Playlist playlist);
     void deletePlaylists(List<Playlist> playlists);
     void deleteAllPlaylists();
     Playlist loadPlaylistByName(String playlistName);
     Playlist loadPlaylistByUID(String playlistName);


     void addAlbumToArtist(Album album, Artist artist);
     void addSongToArtist(Artist artist, Song song);
     void addSongToAlbum(Album album, Song song);
     void addSongToPlaylist(Playlist playlist, Song song);

     void removeSongFromArtist(Artist artist, Song song);
     void removeSongFromAlbum(Album album, Song song);
     void removeSongFromPlaylist(Playlist playlist, Song song);

    // Search with queries
     List<Song> searchSongsWithQuery(String query);
     List<Artist> searchArtistsWithQuery(String query);
     List<Album> searchAlbumsWithQuery(String query);
     List<Playlist> searchPlaylistsWithQuery(String query);


     int getSongCount();                                        // Get number of songs in database
     int getAlbumCount();                                       // Get number of albums in database
     int getArtistCount();                                      // Get number of artists in database
     int getPlaylistCount();                                    // Get number of playlists in database

    // Get songs with various sorts.
     LiveData<List<Song>> getAllSongsNoSort();                  // No sort implemented
     LiveData<List<Song>> getAllSongsNameSort();                // Songs sorted by song name
     LiveData<List<Song>> getAllSongsArtistSort();              // Songs sorted by artist name
     LiveData<List<Song>> getAllSongsAlbumSort();               // Songs sorted by album name

    // Get artists with various sorts.
     LiveData<List<Artist>> getAllArtistsNoSort();              // No sort implemented
     LiveData<List<Artist>> getAllArtistsNameSort();            // Artists sorted by artist name
     LiveData<List<Artist>> getAllArtistsBySongCount();         // Artists sorted by number of songs

    // Get albums with various sorts.
     LiveData<List<Album>> getAllAlbumsNoSort();                // No sort implemented.
     LiveData<List<Album>> getAllAlbumsNameSort();              // Albums sorted by album name
     LiveData<List<Album>> getAllAlbumsArtistSort();            // Albums sorted by artist name
     LiveData<List<Album>> getAllAlbumsBySongCount();           // Albums sorted by number of songs

    // Get playlists with various sorts.
     LiveData<List<Playlist>> getAllPlaylistsNoSort();          // No sort implemented.
     LiveData<List<Playlist>> getAllPlaylistsNameSort();        // Playlists sorted by playlist name
     LiveData<List<Playlist>> getAllPlaylistsBySongCount();     // Playlists sorted by number of songs


     LiveData<List<Song>> getSongsForAlbum(String albumUID);
     LiveData<List<Song>> getSongsForArtist(String artistUID);
     LiveData<List<Song>> getSongsForPlaylist(String playlistUID);
}
