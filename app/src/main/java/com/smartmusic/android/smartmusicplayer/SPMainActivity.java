package com.smartmusic.android.smartmusicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartmusic.android.smartmusicplayer.comparators.songs.SongNameComparator;
import com.smartmusic.android.smartmusicplayer.library.Library;
import com.smartmusic.android.smartmusicplayer.model.AlbumInfo;
import com.smartmusic.android.smartmusicplayer.model.ArtistInfo;
import com.smartmusic.android.smartmusicplayer.model.SongInfo;
import com.smartmusic.android.smartmusicplayer.nowplaying.NowPlaying;
import com.smartmusic.android.smartmusicplayer.playlists.Playlists;
import com.smartmusic.android.smartmusicplayer.settings.Settings;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SPMainActivity
        extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SongEventListener{


    /* Local copy of database lists */
    public static ArrayList<SongInfo> _songs = new ArrayList<>();
    public static ArrayList<ArtistInfo> _artists = new ArrayList<>();
    public static ArrayList<AlbumInfo> _albums = new ArrayList<>();

    private static SongEventHandler songEventHandler = new SongEventHandler();

    /*
      A Handler allows you to send and process Message and Runnable
      objects associated with a thread's MessageQueue. Each Handler
      instance is associated with a single thread and that thread's
      message queue. When you create a new Handler, it is bound to
      the thread / message queue of the thread that is creating it
      -- from that point on, it will deliver messages and runnables
      to that message queue and execute them as they come out of the
      message queue.

       There are two main uses for a Handler: (1) to schedule messages
       and runnables to be executed as some point in the future; and
       (2) to enqueue an action to be performed on a different thread
       than your own.
     */
    private Handler myHandler = new Handler();



    /* SongPlayerService */
    public static SongPlayerService mPlayerService;
    private Intent musicIntent;
    private boolean mMusicBound = false;

    /* SPDatabase Service */
    public static SPDatabaseService mDatabaseService;
    private Intent databaseIntent;
    private boolean mDatabaseBound = false;


    /* Service connection for SongPlayerService */
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(getString(R.string.APP_LOGGER), SPMainActivity.this.getLocalClassName() + " connected to SongPlayerService");
            SongPlayerService.SongPlayerBinder binder = (SongPlayerService.SongPlayerBinder) service;
            //get service
            mPlayerService = binder.getService();
            //pass list
//            mPlayerService.setSongList(_songs);
            mMusicBound = true;

            if( mDatabaseService != null ){
                mPlayerService.setSongList(mDatabaseService.getSongs(new SongNameComparator()));
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(getString(R.string.APP_LOGGER), SPMainActivity.this.getLocalClassName() + " disconnected from SongPlayerService");
            mMusicBound = false;
        }
    };

    /* Service connection for SPDatabaseService */
    private ServiceConnection mDatabaseConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(getString(R.string.APP_LOGGER), SPMainActivity.this.getLocalClassName() + " connected to SPDatabaseService");
            // We've bound to SPDatabaseService, cast the IBinder and get Service instance
            SPDatabaseService.SPDatabaseBinder binder = (SPDatabaseService.SPDatabaseBinder) service;
            mDatabaseService = binder.getService();
            mDatabaseBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(getString(R.string.APP_LOGGER), SPMainActivity.this.getLocalClassName() + " disconnected from SPDatabaseService");
            mDatabaseBound = false;
        }
    };


    /* ----------- UI Fields ------------ */
    LinearLayout mFragmentContainer;

    /*Fragments*/
    FragmentManager fragmentManager;
    FragmentTransaction transaction;

    /* Navigation Header */
    private TextView navName = null;
    private TextView navArtist = null;
    private ImageView navAlbumArt = null;

    /* Navigation Drawer */
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smmain);

        songEventHandler.addSongEventListener(this);

        mFragmentContainer = (LinearLayout)findViewById(R.id.fragment_container);


        /*Links Navigation Header*/
        navName = (TextView)findViewById(R.id.navigation_header_songName);
        navArtist = (TextView)findViewById(R.id.navigation_header_artistName);
        navAlbumArt = (ImageView)findViewById(R.id.navigation_album_art);

        /*------------------------ Setup Navigation Drawer---------------------------*/
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);

        /*Synchronize the indicator with the state of the linked DrawerLayout after onRestoreInstanceState has occurred.*/
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = (NavigationView) findViewById(R.id.drawer_navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        /*----------------------------------------------------------------------*/


        /*Initialize fragment manager*/
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        this.fragmentManager = fragmentManager;
        this.transaction = transaction;


        // Check that the activity is using the layout version with
        // the fragment_container
        if(findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            /*Instantiates library as first fragment*/
            Library library = new Library();

            transaction.add(R.id.fragment_container, library, getResources().getString(R.string.LIBRARY_TAG));
            transaction.commit();
        }
    }
    /*-------------------------------------- ON CREATE METHOD ENDS -----------------------------------------*/

    @Override
    protected void onStart() {
        super.onStart();
//        if( mDatabaseService == null && mPlayerService == null) {
//            bindServices();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindServices();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindServices();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        unbindServices();/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getSongEventHandler().removeSongEventListener(this);
    }


    private void bindServices(){
        Log.i(getString(R.string.APP_LOGGER), this.getLocalClassName() + " is binding services");
        // Bind to SPDatabaseService
        if( databaseIntent == null ) {
            databaseIntent = new Intent(this, SPDatabaseService.class);
            databaseIntent.putExtra(getString(R.string.EXTRA_SENDER), this.getLocalClassName());
            bindService(databaseIntent, mDatabaseConnection, Context.BIND_AUTO_CREATE);
//            startService(databaseIntent); // Service started in SplashActivity
        }

        // Bind to SongPlayerService
//        if( musicIntent == null ){
            musicIntent = new Intent(this, SongPlayerService.class);
            musicIntent.putExtra(getString(R.string.EXTRA_SENDER), this.getLocalClassName());
            startService(musicIntent);
            bindService(musicIntent, musicConnection, Context.BIND_AUTO_CREATE);
//            startService(musicIntent);
//        }
    }

    private void unbindServices(){
        unbindService(mDatabaseConnection);
        mDatabaseBound = false;

        unbindService(musicConnection);
        mMusicBound = false;
    }

    /**
     * Returns handler that disptaches all song events
     * @return SongEventHandler the handler
     */
    public static SongEventHandler getSongEventHandler(){
        return songEventHandler;
    }


    public static ArrayList<SongInfo> getSongs(){
        return _songs;
    }

    public static ArrayList<AlbumInfo> getAlbums(){
        return _albums;
    }

    public static ArrayList<ArtistInfo> getArtists(){
        return _artists;
    }


    //Create 3 dot options menu in corner
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.navigation_menu, menu);
//        return true;
//
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Causes 'hamburger' button to open menu
        //This hook is called whenever an item in your options menu is selected.
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //Navigation Drawer items
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


//        LinearLayout mainLayout = (LinearLayout)findViewById(R.id.fragment_container);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        switch (item.getItemId()) {
            //------------------------------------LIBRARY-----------------------------------------//
            case R.id.library:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                Library libraryFrag = (Library) getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.LIBRARY_TAG));

                if(libraryFrag == null) {
                    libraryFrag = new Library();
                }

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, libraryFrag, getResources().getString(R.string.LIBRARY_TAG))
                        .addToBackStack(null)
                        .commit();

                return true;
            //-------------------------------------NOW_PLAYING-------------------------------------//
            case R.id.now_playing:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                NowPlaying nowPlayingFrag = (NowPlaying) getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.NOW_PLAYING_TAG));

                if(nowPlayingFrag == null) {
                    nowPlayingFrag = new NowPlaying();
                }

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, nowPlayingFrag, getResources().getString(R.string.NOW_PLAYING_TAG))
                        .addToBackStack(null)
                        .commit();

                return true;
            //-------------------------------------PLAYLISTS--------------------------------------//
            case R.id.playlists:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                Playlists playlistsFrag = (Playlists) getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.PLAYLISTS_TAG));

                if(playlistsFrag == null) {
                    playlistsFrag = new Playlists();
                }

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, playlistsFrag, getResources().getString(R.string.PLAYLISTS_TAG))
                        .addToBackStack(null)
                        .commit();

                return true;
            //-------------------------------------SETTINGS---------------------------------------//
            case R.id.settings:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                Settings settingsFrag = (Settings) getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.SETTINGS_TAG));

                if(settingsFrag == null){
                    settingsFrag = new Settings();
                }

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, settingsFrag, getResources().getString(R.string.SETTINGS_TAG))
                        .addToBackStack(null)
                        .commit();


                return true;
        }
        return true;
    }

    @Override
    public void onSongChangeEvent(SongEvent e) {

        SongInfo tmpSong = e.getSource();

        if(tmpSong == null) { return; }

        // Update the navigation header
//            navAlbumArt.setImageURI(currentSong.getAlbumArt());

        if(navName == null){
            navName = (TextView)findViewById(R.id.navigation_header_songName);
        }
        if(navArtist == null){
            navArtist = (TextView)findViewById(R.id.navigation_header_artistName);
        }

        if(navAlbumArt == null){
            navAlbumArt = (ImageView)findViewById(R.id.navigation_album_art);
        }

        Picasso.with(this)
                .load(tmpSong.getAlbumArt())
                .placeholder(R.drawable.temp_album_art)
                .error(R.drawable.temp_album_art)
                .into(navAlbumArt);

        navArtist.setText(tmpSong.getArtistname());
        navName.setText(tmpSong.getSongname());
    }

    @Override
    public void onSongStopEvent(SongEvent e) {
        // Reset navigation header
        // to original state.

        if(navName == null){
            navName = (TextView)findViewById(R.id.navigation_header_songName);
        }
        if(navArtist == null){
            navArtist = (TextView)findViewById(R.id.navigation_header_artistName);
        }

        if(navAlbumArt == null){
            navAlbumArt = (ImageView)findViewById(R.id.navigation_album_art);
        }

        navAlbumArt.setImageResource(R.drawable.temp_album_art);

        navArtist.setText("Select a song.");
        navName.setText("");
    }

    @Override
    public void onSongAddedEvent(SongEvent e) {

    }

    @Override
    public void onSongRemovedEvent(SongEvent e) {

    }

    @Override
    public void onShuffleOnEvent(SongEvent e) {}

    @Override
    public void onShuffleOffEvent(SongEvent e) {}
}
