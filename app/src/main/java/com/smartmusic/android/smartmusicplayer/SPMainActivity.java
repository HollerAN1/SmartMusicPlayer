package com.smartmusic.android.smartmusicplayer;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.smartmusic.android.smartmusicplayer.database.RoomSQLDatabase;
import com.smartmusic.android.smartmusicplayer.database.SPRepository;
import com.smartmusic.android.smartmusicplayer.events.SongEventHandler;
import com.smartmusic.android.smartmusicplayer.library.Library;

public class SPMainActivity
        extends AppCompatActivity {


    private static SongEventHandler songEventHandler = new SongEventHandler();                      // Handles song events
    public static SongPlayerService mPlayerService;                                                 // Handles song playback
    public static SPRepository repository;                                                          // Database
    private SPNavigationDrawer navDrawer;                                                           // Navigation Drawer
    private SPSearch search;                                                                        // Options menu search icon
    private SPOnBackPressedListener backPressedListener;

    /* Service connection for SongPlayerService */
    private ServiceConnection musicConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(getString(R.string.APP_LOGGER), SPMainActivity.this.getLocalClassName() + " connected to SongPlayerService");
            SongPlayerService.SongPlayerBinder binder = (SongPlayerService.SongPlayerBinder) service;
            mPlayerService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(getString(R.string.APP_LOGGER), SPMainActivity.this.getLocalClassName() + " disconnected from SongPlayerService");
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smmain);

        handleIntent(getIntent());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        repository = new SPRepository(RoomSQLDatabase.getDatabase(this));

        // Setup navigation drawer
        navDrawer = new SPNavigationDrawer(this, getSupportFragmentManager());

        search = new SPSearch(this, getSupportFragmentManager());

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
            transitionToFragment(library, getString(R.string.LIBRARY_TAG));
        }
    }
    /*-------------------------------------- ON CREATE METHOD ENDS -----------------------------------------*/

    public void setOnBackPressedListener(SPOnBackPressedListener backPressedListener){
        this.backPressedListener = backPressedListener;
    }

    @Override
    public void onBackPressed() {
        if(backPressedListener != null){
            backPressedListener.pressedBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        navDrawer.removeSongEventHandler();
    }


    private void bindServices(){
        Log.i(getString(R.string.APP_LOGGER), this.getLocalClassName() + " is binding services");
        // Bind to SongPlayerService
        Intent musicIntent = new Intent(this, SongPlayerService.class);
        musicIntent.putExtra(getString(R.string.EXTRA_SENDER), this.getLocalClassName());
        startService(musicIntent);
        bindService(musicIntent, musicConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindServices(){
        unbindService(musicConnection);
    }

    /**
     * Returns handler that dispatches all song events
     * @return SongEventHandler the handler
     */
    public static SongEventHandler getSongEventHandler(){
        return songEventHandler;
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        search.setupSearchIcon(menu);
        return true;
    }

    /**
     * Sets the color of a menu icon
     * @param icon the drawable for the menu icon.
     */
    protected void setMenuIconColor(Drawable icon){
        icon.mutate();
        icon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.search:
                search.transitionToSearchFragment();
                return true;
        }
        return navDrawer.onNavOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    /**
     * Handles intents passed to Activity.
     * @param intent the intent to handle
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            search.doSearch(query);
        }
    }

    public void setActionBarTitle(int title){
        getSupportActionBar().setTitle(title);
    }

    /**
     * Replaces the fragment_container with the given fragment
     * distinguished by the given tag.
     * @param frag the fragment
     * @param tag the fragment tag
     */
    protected void transitionToFragment(Fragment frag, String tag){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, frag, tag)
                .addToBackStack(null)
                .commit();
    }
}
