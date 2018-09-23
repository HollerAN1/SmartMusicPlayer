package com.smartmusic.android.smartmusicplayer;

import android.app.Activity;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartmusic.android.smartmusicplayer.database.entities.Song;
import com.smartmusic.android.smartmusicplayer.events.SongPlaybackEvent;
import com.smartmusic.android.smartmusicplayer.events.SongPlaybackEventListener;
import com.smartmusic.android.smartmusicplayer.library.Library;
import com.smartmusic.android.smartmusicplayer.nowplaying.NowPlaying;
import com.smartmusic.android.smartmusicplayer.playlists.Playlists;
import com.smartmusic.android.smartmusicplayer.settings.Settings;
import com.squareup.picasso.Picasso;

import static com.smartmusic.android.smartmusicplayer.SPMainActivity.getSongEventHandler;

public class SPNavigationDrawer implements NavigationView.OnNavigationItemSelectedListener, SongPlaybackEventListener{

    private TextView songName;
    private TextView songArtist;
    private ImageView songAlbumArt;

    private Activity activity;
    private Resources res;
    private FragmentManager fragManager;

    private DrawerLayout mDrawerLayout;

    /**
     * Creates a Navigation Drawer object that handles
     * all navigation drawer functionality
     * @param activity the parent activity
     * @param fragManager a support fragment manager
     */
    protected SPNavigationDrawer(Activity activity, FragmentManager fragManager){
        this.activity = activity;
        this.res = activity.getResources();
        this.fragManager = fragManager;
    }

    /**
     * Sets up all items associated with the
     * navigation drawer.
     */
    protected void init(){
        /*Link Navigation Header Items*/
        songName =               activity.findViewById(R.id.navigation_header_songName);
        songArtist =             activity.findViewById(R.id.navigation_header_artistName);
        songAlbumArt =           activity.findViewById(R.id.navigation_album_art);

        /*Setup Navigation Drawer*/
        mDrawerLayout =         activity.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mToggle = new ActionBarDrawerToggle(activity, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);

        /*Synchronize the indicator with the state of the linked DrawerLayout after onRestoreInstanceState has occurred.*/
        mToggle.syncState();

        NavigationView navigationView = activity.findViewById(R.id.drawer_navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSongEventHandler().addSongPlaybackEventListener(this);
    }

    public void removeSongEventHandler(){
        getSongEventHandler().removeSongPlaybackEventListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mDrawerLayout.closeDrawer(Gravity.START);
        Fragment transitionFrag = null; // The fragment to transition to
        String transitionTag = null; // The tag to reference the fragment

        switch (item.getItemId()) {
            //------------------------------------LIBRARY-----------------------------------------//
            case R.id.library:
                Library libraryFrag = (Library) fragManager.findFragmentByTag(res.getString(R.string.LIBRARY_TAG));

                if(libraryFrag == null) {
                    libraryFrag = new Library();
                }

                transitionFrag = libraryFrag;
                transitionTag = res.getString(R.string.LIBRARY_TAG);

                break;
            //-------------------------------------NOW_PLAYING-------------------------------------//
            case R.id.now_playing:
                NowPlaying nowPlayingFrag = (NowPlaying) fragManager.findFragmentByTag(res.getString(R.string.NOW_PLAYING_TAG));

                if(nowPlayingFrag == null) {
                    nowPlayingFrag = new NowPlaying();
                }

                transitionFrag = nowPlayingFrag;
                transitionTag = res.getString(R.string.NOW_PLAYING_TAG);

                break;
            //-------------------------------------PLAYLISTS--------------------------------------//
            case R.id.playlists:
                Playlists playlistsFrag = (Playlists) fragManager.findFragmentByTag(res.getString(R.string.PLAYLISTS_TAG));

                if(playlistsFrag == null) {
                    playlistsFrag = new Playlists();
                }

                transitionFrag = playlistsFrag;
                transitionTag = res.getString(R.string.PLAYLISTS_TAG);

                break;
            //-------------------------------------SETTINGS---------------------------------------//
            case R.id.settings:
                Settings settingsFrag = (Settings) fragManager.findFragmentByTag(res.getString(R.string.SETTINGS_TAG));

                if(settingsFrag == null){
                    settingsFrag = new Settings();
                }

                transitionFrag = settingsFrag;
                transitionTag = res.getString(R.string.SETTINGS_TAG);

                break;
        }

        fragManager
                .beginTransaction()
                .replace(R.id.fragment_container, transitionFrag, transitionTag)
                .addToBackStack(null)
                .commit();

        return true;
    }

    @Override
    public void onSongChangeEvent(SongPlaybackEvent e) {

        Song tmpSong = e.getSource();

        if(tmpSong == null) { return; }

        if(songName == null){
            songName = activity.findViewById(R.id.navigation_header_songName);
        }
        if(songArtist == null){
            songArtist = activity.findViewById(R.id.navigation_header_artistName);
        }

        if(songAlbumArt == null){
            songAlbumArt = activity.findViewById(R.id.navigation_album_art);
        }

        Picasso.with(activity)
                .load(tmpSong.getAlbumArt())
                .placeholder(R.drawable.temp_album_art)
                .error(R.drawable.temp_album_art)
                .into(songAlbumArt);

        songArtist.setText(tmpSong.getArtistName());
        songName.setText(tmpSong.getSongName());
    }

    @Override
    public void onSongStopEvent(SongPlaybackEvent e) {
        // Reset navigation header
        // to original state.

        if(songName == null){
            songName = activity.findViewById(R.id.navigation_header_songName);
        }
        if(songArtist == null){
            songArtist = activity.findViewById(R.id.navigation_header_artistName);
        }

        if(songAlbumArt == null){
            songAlbumArt = activity.findViewById(R.id.navigation_album_art);
        }

        songAlbumArt.setImageResource(R.drawable.temp_album_art);

        songArtist.setText(R.string.select_song_default);
        songName.setText("");
    }
}
