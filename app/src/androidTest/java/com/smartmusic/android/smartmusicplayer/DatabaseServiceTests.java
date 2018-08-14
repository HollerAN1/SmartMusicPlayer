package com.smartmusic.android.smartmusicplayer;

/**
 * Instrumentation Tests for DatabaseService
 * Created by holle on 8/6/2018.
 */

import android.content.Intent;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ServiceTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.smartmusic.android.smartmusicplayer.comparators.playlists.PlaylistDateComparator;
import com.smartmusic.android.smartmusicplayer.comparators.playlists.PlaylistNameComparator;
import com.smartmusic.android.smartmusicplayer.comparators.playlists.PlaylistSizeComparator;
import com.smartmusic.android.smartmusicplayer.comparators.songs.SongNameComparator;
import com.smartmusic.android.smartmusicplayer.model.PlaylistInfo;
import com.smartmusic.android.smartmusicplayer.model.SongInfo;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.any;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class DatabaseServiceTests {

    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();
    public SPDatabaseService mService;

    /*Strings for testing*/
    private static final String playlist1Name = "New Playlist1";
    private static final String playlist1Desc = "Testing with only one playlist input";
    private static final String playlist2Name = "A Second Playlist";
    private static final String playlist2Desc = "Adding more playlists";
    private static final String playlist3Name = "A Third Playlist";
    private static final String playlist3Desc = "Another playlist";

    private static final String nameSort1 = "A is for Apple";
    private static final String nameSort2 = "D is for Dog";
    private static final String nameSort3 = "Z is for Zebra";


    @org.junit.Before
    public void setUp() throws TimeoutException{
        testWithBoundService();
    }


    /*------------------------ START PLAYLIST TESTS ----------------------------*/
    @Test
    public void test_getPlaylists_after_initalization(){
        PlaylistDateComparator dateComparator = new PlaylistDateComparator();
        ArrayList<PlaylistInfo> playlists = mService.getPlaylists(dateComparator);

        Assert.assertNotNull(playlists);
        Assert.assertEquals(playlists.size(), 0);
    }

    @Test
    public void test_getPlaylists_with_one_entry(){
        PlaylistDateComparator dateComparator = new PlaylistDateComparator();

        PlaylistInfo playlist
                = new PlaylistInfo(playlist1Name, // Name
                playlist1Desc, // Description
                new ArrayList<SongInfo>(), // Songs
                new SongNameComparator()); // Song Comparator

        mService.addPlaylist(playlist);
        ArrayList<PlaylistInfo> playlists = mService.getPlaylists(dateComparator);

        Assert.assertNotNull(playlists);
        Assert.assertEquals(playlists.size(), 1);

        PlaylistInfo firstPlaylist = playlists.get(0);

        Assert.assertEquals(firstPlaylist.getName(), playlist1Name);
        Assert.assertEquals(firstPlaylist.getDescription(), playlist1Desc);
    }

    @Test
    public void test_playlist_sort_by_date() throws InterruptedException{
        PlaylistDateComparator dateComparator = new PlaylistDateComparator();

        PlaylistInfo playlist1
                = new PlaylistInfo(playlist1Name, // Name
                playlist1Desc, // Description
                new ArrayList<SongInfo>(), // Songs
                new SongNameComparator()); // Song Comparator

        Thread.sleep(1000); // Ensures created at different times

        PlaylistInfo playlist2
                = new PlaylistInfo(playlist2Name, // Name
                playlist2Desc, // Description
                new ArrayList<SongInfo>(), // Songs
                new SongNameComparator()); // Song Comparator

        Thread.sleep(1000); // Ensures created at different times

        PlaylistInfo playlist3
                = new PlaylistInfo(playlist3Name, // Name
                playlist3Desc, // Description
                new ArrayList<SongInfo>(), // Songs
                new SongNameComparator()); // Song Comparator

        Thread.sleep(1000); // Ensures created at different times

        mService.addPlaylist(playlist1);
        mService.addPlaylist(playlist2);
        mService.addPlaylist(playlist3);

        ArrayList<PlaylistInfo> playlists = mService.getPlaylists(dateComparator);
        Assert.assertEquals(playlists.size(), 3);

        PlaylistInfo firstPlaylist = playlists.get(0);
        PlaylistInfo secondPlaylist = playlists.get(1);
        PlaylistInfo thirdPlaylist = playlists.get(2);

        Assert.assertEquals(firstPlaylist, playlist1);
        Assert.assertEquals(secondPlaylist, playlist2);
        Assert.assertEquals(thirdPlaylist, playlist3);

        Assert.assertTrue(firstPlaylist.getDateCreated().before(secondPlaylist.getDateCreated()));
        Assert.assertTrue(secondPlaylist.getDateCreated().before(thirdPlaylist.getDateCreated()));
        Assert.assertTrue(firstPlaylist.getDateCreated().before(thirdPlaylist.getDateCreated()));
    }

    @Test
    public void test_playlist_sort_by_name(){
        PlaylistNameComparator nameComparator = new PlaylistNameComparator();

        PlaylistInfo playlist1
                = new PlaylistInfo(nameSort3, // Name
                playlist1Desc, // Description
                new ArrayList<SongInfo>(), // Songs
                new SongNameComparator()); // Song Comparator

        PlaylistInfo playlist2
                = new PlaylistInfo(nameSort1, // Name
                playlist2Desc, // Description
                new ArrayList<SongInfo>(), // Songs
                new SongNameComparator()); // Song Comparator

        PlaylistInfo playlist3
                = new PlaylistInfo(nameSort2, // Name
                playlist3Desc, // Description
                new ArrayList<SongInfo>(), // Songs
                new SongNameComparator()); // Song Comparator

        mService.addPlaylist(playlist1);
        mService.addPlaylist(playlist2);
        mService.addPlaylist(playlist3);

        ArrayList<PlaylistInfo> playlists = mService.getPlaylists(nameComparator);
        Assert.assertEquals(playlists.size(), 3);

        PlaylistInfo firstPlaylist = playlists.get(0);
        PlaylistInfo secondPlaylist = playlists.get(1);
        PlaylistInfo thirdPlaylist = playlists.get(2);

        Assert.assertEquals(firstPlaylist, playlist2);
        Assert.assertEquals(secondPlaylist, playlist3);
        Assert.assertEquals(thirdPlaylist, playlist1);
    }

    @Test
    public void test_playlist_sort_by_size(){
        PlaylistSizeComparator sizeComparator = new PlaylistSizeComparator();

        PlaylistInfo playlist1
                = new PlaylistInfo(playlist1Name, // Name
                playlist1Desc, // Description
                new ArrayList<SongInfo>(), // Songs
                new SongNameComparator()); // Song Comparator

        PlaylistInfo playlist2
                = new PlaylistInfo(playlist2Name, // Name
                playlist2Desc, // Description
                new ArrayList<SongInfo>(), // Songs
                new SongNameComparator()); // Song Comparator

        PlaylistInfo playlist3
                = new PlaylistInfo(playlist3Name, // Name
                playlist3Desc, // Description
                new ArrayList<SongInfo>(), // Songs
                new SongNameComparator()); // Song Comparator


        SongInfo dummySong
                = new SongInfo("DummySong",
                "DummyArtist",
                "No Album",
                "", null);

        // 2 songs
        playlist1.addSong(dummySong);
        playlist1.addSong(dummySong);

        // 1 song
        playlist2.addSong(dummySong);


        // 5 songs
        playlist3.addSong(dummySong);
        playlist3.addSong(dummySong);
        playlist3.addSong(dummySong);
        playlist3.addSong(dummySong);
        playlist3.addSong(dummySong);

        mService.addPlaylist(playlist1);
        mService.addPlaylist(playlist2);
        mService.addPlaylist(playlist3);

        ArrayList<PlaylistInfo> playlists = mService.getPlaylists(sizeComparator);
        Assert.assertEquals(playlists.size(), 3);

        PlaylistInfo firstPlaylist = playlists.get(0);
        PlaylistInfo secondPlaylist = playlists.get(1);
        PlaylistInfo thirdPlaylist = playlists.get(2);

        Assert.assertEquals(firstPlaylist, playlist2);
        Assert.assertEquals(secondPlaylist, playlist1);
        Assert.assertEquals(thirdPlaylist, playlist3);
    }

    @Test
    public void test_removePlaylist(){
        PlaylistSizeComparator sizeComparator = new PlaylistSizeComparator();

        PlaylistInfo playlist1
                = new PlaylistInfo(playlist1Name, // Name
                playlist1Desc, // Description
                new ArrayList<SongInfo>(), // Songs
                new SongNameComparator()); // Song Comparator

        PlaylistInfo playlist2
                = new PlaylistInfo(playlist2Name, // Name
                playlist2Desc, // Description
                new ArrayList<SongInfo>(), // Songs
                new SongNameComparator()); // Song Comparator

        PlaylistInfo playlist3
                = new PlaylistInfo(playlist3Name, // Name
                playlist3Desc, // Description
                new ArrayList<SongInfo>(), // Songs
                new SongNameComparator()); // Song Comparator

        mService.addPlaylist(playlist1);
        mService.addPlaylist(playlist2);
        mService.addPlaylist(playlist3);

        mService.removePlaylist(playlist2);

        ArrayList<PlaylistInfo> playlists = mService.getPlaylists(sizeComparator);
        Assert.assertEquals(playlists.size(), 2);

        PlaylistInfo firstPlaylist = playlists.get(0);
        PlaylistInfo secondPlaylist = playlists.get(1);

        Assert.assertEquals(firstPlaylist, playlist1);
        Assert.assertEquals(secondPlaylist, playlist3);
    }
    /*------------------------ END PLAYLIST TESTS ----------------------------*/


    @Test
    public void testWithStartedService() {
        try {
            mServiceRule.startService(
                    new Intent(InstrumentationRegistry.getTargetContext(), SPDatabaseService.class));
        } catch (TimeoutException e){

        }
        //do something
    }

    @Test
    public void testWithBoundService() throws TimeoutException {
        // Create the service Intent.
        Intent serviceIntent =
                new Intent(InstrumentationRegistry.getTargetContext(),
                        SPDatabaseService.class);

        // Data can be passed to the service via the Intent.
//        serviceIntent.putExtra(LocalService.SEED_KEY, 42L);

        // Bind the service and grab a reference to the binder.
        IBinder binder = mServiceRule.bindService(serviceIntent);

        // Get the reference to the service, or you can call
        // public methods on the binder directly.
        mService =
                ((SPDatabaseService.SPDatabaseBinder) binder).getService();

        // Verify that the service is working correctly.
        assertThat(mService.getRandomInt(), is(any(Integer.class)));
    }
}
