package com.smartmusic.android.smartmusicplayer.library;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smartmusic.android.smartmusicplayer.SPFragment;
import com.smartmusic.android.smartmusicplayer.nowplaying.NowPlayingBar;
import com.smartmusic.android.smartmusicplayer.SPMainActivity;
import com.smartmusic.android.smartmusicplayer.R;

/**
 * Base class for entire "Library" view that is presented on
 * startup and when the user selects "Library" from the navigation
 * drawer. Contains a view pager to display Songs, Artists and Albums
 * fragments.
 */
public class Library extends SPFragment {

    private View mainView = null;
    private NowPlayingBar nowPlayingBar;
    private ViewPager viewPager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);

        if( mainView == null ) {
            // Inflate the layout for this fragment
            this.mainView = inflater.inflate(R.layout.fragment_library, container, false);
            setupTabs();
            this.nowPlayingBar = new NowPlayingBar(getContext(), mainView, getActivity().getSupportFragmentManager());
        }

        return mainView;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        nowPlayingBar.removeSongEventListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        SPMainActivity parentActivity = ((SPMainActivity)getActivity());
        if( parentActivity != null ){
            parentActivity.setActionBarTitle(R.string.LIBRARY);
        }
        if(SPMainActivity.mPlayerService != null) {
            nowPlayingBar.update(SPMainActivity.mPlayerService.getCurrentSong());
        }
    }

    /**
     * Sets up the tab view at the top
     * of the library fragment and adds
     * the adapter.
     */
    private void setupTabs(){
        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        LibraryPagerAdapter libraryPagerAdapter =
                new LibraryPagerAdapter(
                        getFragmentManager(), getContext());

        viewPager = mainView.findViewById(R.id.library_view_pager);
        TabLayout tabLayout = mainView.findViewById(R.id.library_tab_layout);
        tabLayout.setupWithViewPager(viewPager, true);
        viewPager.setAdapter(libraryPagerAdapter);
        tabLayout.setSelectedTabIndicatorHeight(0); // Removes tab indicator

        /* Set drawables for tab backgrounds */
        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.tab_background_left));
        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(1).setBackground(getResources().getDrawable(R.drawable.tab_background_middle));
        ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(2).setBackground(getResources().getDrawable(R.drawable.tab_background_right));
    }

    @Override
    public void pressedBack() {
        super.pressedBack();
        if(viewPager.getCurrentItem() != 0){
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }
}



