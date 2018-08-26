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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.any;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class DatabaseTests {

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


}
