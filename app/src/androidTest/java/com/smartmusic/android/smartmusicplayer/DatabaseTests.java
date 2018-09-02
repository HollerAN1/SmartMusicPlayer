package com.smartmusic.android.smartmusicplayer;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.smartmusic.android.smartmusicplayer.database.SPDatabase;
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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(AndroidJUnit4.class)
public class DatabaseTests {

    /* Database to test with */
    private SPDatabase mDatabase;

    private final String LOGGER_TAG = "DatabaseTestsLogger";

    /* Daos */
    private SongDao mSongDao;
    private ArtistDao mArtistDao;
    private AlbumDao mAlbumDao;
    private PlaylistDao mPlaylistDao;
    private SongPlaylistJoinDao mSongPlaylistJoinDao;

    /* Strings */
    private static final String SONG1_NAME = "Song 1";
    private static final String SONG2_NAME = "Song 2";
    private static final String SONG3_NAME = "Song 3";
    private static final String SONG4_NAME = "Song 4";

    private static final String ARTIST1_NAME = "Artist 1";
    private static final String ARTIST2_NAME = "Artist 2";
    private static final String ARTIST3_NAME = "Artist 3";

    private static final String ALBUM1_NAME = "Album 1";
    private static final String ALBUM2_NAME = "Album 2";
    private static final String ALBUM3_NAME = "Album 3";

    private static final String PLAYLIST1_NAME = "Playlist 1";
    private static final String PLAYLIST2_NAME = "Playlist 2";
    private static final String PLAYLIST3_NAME = "Playlist 3";

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        mDatabase = SPDatabase.getTestDatabase(context); // In-memory instance
        mSongDao = mDatabase.songDao();
        mAlbumDao = mDatabase.albumDao();
        mArtistDao = mDatabase.artistDao();
        mPlaylistDao = mDatabase.playlistDao();
        mSongPlaylistJoinDao = mDatabase.songPlaylistJoinDao();
    }

    @After
    public void closeDb() throws IOException {
//        mDatabase.close();
    }

    @Test
    public void addAndReadSong(){
        Song song = TestUtil.createBlankSong(SONG1_NAME);
        mSongDao.insert(song);
        Song returnedSong = mSongDao.findSongByName(SONG1_NAME);
        assertThat(returnedSong.getSongName(), is(song.getSongName()));
    }

    @Test
    public void addSongToArtist(){
        Song song = TestUtil.createBlankSong(SONG2_NAME);
        Artist artist = TestUtil.createBlankArtist(ARTIST1_NAME);
        TestUtil.addSongToArtist(artist, song);
        mArtistDao.insert(artist);
        mSongDao.insert(song);

        // Checking for artist in database
        Artist returnedArtist = mArtistDao.findArtistByName(ARTIST1_NAME);
        assertNotNull("Artist not found in database.", returnedArtist);

        // Checking for song in artist list
        List<Song> songs = mArtistDao.getSongsForArtist(returnedArtist.getArtistUID());
        assert(songs.size() == 1);
        Song returnedSong = songs.get(0);

        assertThat("Song names do not match.",returnedSong.getSongName(), equalTo(SONG2_NAME));

        // Checking for song in song_table
        Song returnedSong2 = mSongDao.findSongByName(SONG2_NAME);
        assertNotNull("Song not created in song table.", returnedSong2);

        assertThat("Artist UID does not match.", returnedSong2.getArtistUID(), equalTo(artist.getArtistUID()));
    }

    @Test
    public void addSongToPlaylist(){
        Song song = TestUtil.createBlankSong(SONG2_NAME);
        Playlist playlist = TestUtil.createBlankPlaylist(PLAYLIST1_NAME);
        mSongDao.insert(song);
        mPlaylistDao.insert(playlist);
        mSongPlaylistJoinDao.insert(new SongPlaylistJoin(song.getSongUID(), playlist.getPlaylistUID()));


        List<Song> songs = mSongPlaylistJoinDao.getSongsForPlaylist(playlist.getPlaylistUID());
        assert(songs.size() == 1);

        Song playlistSong = songs.get(0);

        assertThat("Song names do not match.", song.getSongName(), equalTo(playlistSong.getSongName()));
    }

    private static class TestUtil {

        protected static Song createBlankSong(String songName){
            Song newSong = new Song(songName,
                    null,
                    null,
                    null,
                    null,
                    0,
                    0,
                    null,
                    null,
                    0,
                    null);

            return  newSong;
        }

        protected static Artist createBlankArtist(String artistName){
            Artist artist = new Artist(artistName);
            return artist;
        }

        protected static Album createBlankAlbum(String albumName){
            Album album = new Album(albumName,
                    null,
                    null);

            return album;
        }

        protected static Playlist createBlankPlaylist(String playlistName){
            Playlist playlist = new Playlist(playlistName,
                    null,
                    false);

            return playlist;
        }

        protected static void addSongToArtist(Artist artist, Song song){
            song.setArtistName(artist.getArtistName());
            song.setArtistUID(artist.getArtistUID());
        }

        protected static void addSongToAlbum(Album album, Song song){
            song.setAlbumName(album.getAlbumName());
            song.setAlbumUID(album.getAlbumUID());
        }

        protected static void addArtistToAlbum(Album album, Artist artist){
            album.setArtistName(artist.getArtistName());
            album.setArtistUid(artist.getArtistUID());
        }
    }
}
