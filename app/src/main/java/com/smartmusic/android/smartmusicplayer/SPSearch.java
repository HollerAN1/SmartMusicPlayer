package com.smartmusic.android.smartmusicplayer;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.View;

import com.smartmusic.android.smartmusicplayer.search.SearchResultsAdapter;
import com.smartmusic.android.smartmusicplayer.search.SearchResultsFragment;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import static com.smartmusic.android.smartmusicplayer.SPMainActivity.repository;

/**
 * Handles all search functionality of the search menu icon.
 */
public class SPSearch {

    private Activity activity;
    private FragmentManager fragManager;

    protected SPSearch(Activity activity, FragmentManager fragmentManager){
        this.activity = activity;
        this.fragManager = fragmentManager;
    }

    /**
     * Sets up the functionality for the search icon
     * located in the options menu.
     * @param menu the options menu
     */
    protected void setupSearchIcon(final Menu menu){
        final Drawable searchIcon = menu.getItem(0).getIcon();
        ((SPMainActivity)activity).setMenuIconColor(searchIcon);

        SearchManager searchManager =
                (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(activity.getComponentName()));

        searchView.setLayoutTransition(new LayoutTransition());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {return false;}

            @Override
            public boolean onQueryTextChange(String s) {
                doSearch(s);
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
    }

    /**
     * Transitions to the fragment that displays
     * the search results
     */
    protected void transitionToSearchFragment(){
        SearchResultsFragment searchFrag = (SearchResultsFragment) fragManager
                .findFragmentByTag(activity.getResources().getString(R.string.SEARCH_TAG));

        if(searchFrag == null){
            searchFrag = new SearchResultsFragment();
        }

        ((SPMainActivity)activity).transitionToFragment(searchFrag, activity.getString(R.string.SEARCH_TAG));
    }

    /**
     * Searches through the database for the given query.
     * @param query the user's search query.
     */
    protected void doSearch(final String query) {
        Fragment searchFrag = fragManager
                .findFragmentByTag(activity.getString(R.string.SEARCH_TAG));
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
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
            }
        }
    }
}
