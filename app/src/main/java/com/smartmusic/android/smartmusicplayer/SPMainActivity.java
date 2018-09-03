package com.smartmusic.android.smartmusicplayer;

import android.animation.LayoutTransition;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.smartmusic.android.smartmusicplayer.database.SPRepository;
import com.smartmusic.android.smartmusicplayer.database.entities.Album;
import com.smartmusic.android.smartmusicplayer.database.entities.Artist;
import com.smartmusic.android.smartmusicplayer.database.entities.Song;
import com.smartmusic.android.smartmusicplayer.library.Library;
import com.smartmusic.android.smartmusicplayer.nowplaying.NowPlaying;
import com.smartmusic.android.smartmusicplayer.playlists.Playlists;
import com.smartmusic.android.smartmusicplayer.search.SearchResultsAdapter;
import com.smartmusic.android.smartmusicplayer.search.SearchResultsFragment;
import com.smartmusic.android.smartmusicplayer.settings.Settings;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class SPMainActivity
        extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SongEventListener{


    /* Local copy of database lists */
    public static ArrayList<Song> _songs = new ArrayList<>();
    public static ArrayList<Artist> _artists = new ArrayList<>();
    public static ArrayList<Album> _albums = new ArrayList<>();

    private static SongEventHandler songEventHandler = new SongEventHandler();

    /* SongPlayerService */
    public static SongPlayerService mPlayerService;
    private Intent musicIntent;
    private boolean mMusicBound = false;

    public static SPRepository repository;


    /* Service connection for SongPlayerService */
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(getString(R.string.APP_LOGGER), SPMainActivity.this.getLocalClassName() + " connected to SongPlayerService");
            SongPlayerService.SongPlayerBinder binder = (SongPlayerService.SongPlayerBinder) service;
            //get service
            mPlayerService = binder.getService();

            mMusicBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(getString(R.string.APP_LOGGER), SPMainActivity.this.getLocalClassName() + " disconnected from SongPlayerService");
            mMusicBound = false;
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

        handleIntent(getIntent());

        repository = new SPRepository(this);

        getSupportActionBar().setTitle(R.string.LIBRARY);

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

            transaction
                    .add(R.id.fragment_container, library, getResources().getString(R.string.LIBRARY_TAG))
                    .addToBackStack(null)
                    .commit();
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
        // Bind to SongPlayerService
//        if( musicIntent == null ){
            musicIntent = new Intent(this, SongPlayerService.class);
            musicIntent.putExtra(getString(R.string.EXTRA_SENDER), this.getLocalClassName());
            startService(musicIntent);
            bindService(musicIntent, musicConnection, Context.BIND_AUTO_CREATE);
//        }
    }

    private void unbindServices(){
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


    public static ArrayList<Song> getSongs(){
        return _songs;
    }

    public static ArrayList<Album> getAlbums(){
        return _albums;
    }

    public static ArrayList<Artist> getArtists(){
        return _artists;
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);

        /* ------------------- Search icon functionality ----------------------- */
        final Drawable searchIcon = menu.getItem(0).getIcon();
        setMenuIconColor(searchIcon);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setLayoutTransition(new LayoutTransition());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                doMySearch(s);
                return false;
            }
        });

        // Close the keyboard and SearchView at same time when the back button is pressed
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean queryTextFocused) {
                if (!queryTextFocused) {
                    MenuItemCompat.collapseActionView(menu.getItem(0));
                }
            }
        });

        return true;

    }


    private void setMenuIconColor(Drawable icon){
        icon.mutate();
        icon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.search:
                transitionToSearchFragment();
                return true;
        }
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //Navigation Drawer items
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }

    private void transitionToSearchFragment(){
        SearchResultsFragment searchFrag = (SearchResultsFragment) getSupportFragmentManager()
                .findFragmentByTag(getResources().getString(R.string.SEARCH_TAG));

        if(searchFrag == null){
            searchFrag = new SearchResultsFragment();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, searchFrag, getResources().getString(R.string.SEARCH_TAG))
                .addToBackStack(null)
                .commit();

    }

    private void doMySearch(final String query) {
        Fragment searchFrag = getSupportFragmentManager()
                .findFragmentByTag(getString(R.string.SEARCH_TAG));
        if(searchFrag != null) {
            final StickyListHeadersListView list = searchFrag
                    .getView().findViewById(R.id.search_results_list_view);
            if (list != null) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        final SearchResultsAdapter adapter = (SearchResultsAdapter) list.getAdapter();
                        adapter.setSongsResult(repository.searchSongs(query));
                        adapter.setArtistsResult(repository.searchArtists(query));
                        adapter.setAlbumsResult(repository.searchAlbums(query));
                        adapter.setPlaylistsResult(repository.searchPlaylists(query));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
            }
        }


//        SQLiteHelper sqLiteHelper = ((SPMainActivity)getApplication()).getDbHelper();
//        Cursor cursor = sqLiteHelper.getReadableDatabase().rawQuery("SELECT " + DatabaseConstants.COL_LANG_ID + ", " +
//                DatabaseConstants.COL_LANG_NAME + " FROM " + DatabaseConstants.TABLE_LANG +
//                " WHERE upper(" + DatabaseConstants.COL_LANG_NAME + ") like '%" + query.toUpperCase() + "%'", null);
//        setListAdapter(new SimpleCursorAdapter(this, R.layout.container_list_item_view, cursor,
//                new String[] {DatabaseConstants.COL_LANG_NAME }, new int[]{R.id.list_item}));
    }

    @Override
    public void onSongChangeEvent(SongEvent e) {

        Song tmpSong = e.getSource();

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

        navArtist.setText(tmpSong.getArtistName());
        navName.setText(tmpSong.getSongName());
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
