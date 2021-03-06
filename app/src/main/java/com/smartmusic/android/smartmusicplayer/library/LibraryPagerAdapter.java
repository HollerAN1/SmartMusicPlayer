package com.smartmusic.android.smartmusicplayer.library;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.smartmusic.android.smartmusicplayer.R;

/**
 * Handles switching between different fragments
 * {Songs, Artists, Albums} in Library.
 *
 * Created by holle on 3/7/2018.
 */

public class LibraryPagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private static final int PAGE_COUNT = 3;
    private static String tabTitles[];

    public LibraryPagerAdapter(FragmentManager fm, Context context){
        super(fm);
        this.context = context;
        tabTitles = new String[] {
                context.getString(R.string.pager_title_songs),
                context.getString(R.string.pager_title_artists),
                context.getString(R.string.pager_title_albums)};
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return new SongListFragment();
            case 1:
                return new ArtistListFragment();
            case 2:
                return new AlbumListFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
