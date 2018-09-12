package com.smartmusic.android.smartmusicplayer.library;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartmusic.android.smartmusicplayer.NowPlayingBar;
import com.smartmusic.android.smartmusicplayer.SPMainActivity;
import com.smartmusic.android.smartmusicplayer.nowplaying.NowPlaying;
import com.smartmusic.android.smartmusicplayer.R;

/**
 * Base class for entire "Library" view that is presented on
 * startup and when the user selects "Library" from the navigation
 * drawer. Contains a view pager to display Songs, Artists and Albums.
 */
public class Library extends Fragment {

    View mainView = null;

    private LibraryPagerAdapter mlibraryPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    private NowPlayingBar nowPlayingBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.LIBRARY));
        setRetainInstance(true);

        if( mainView == null ) {
            // Inflate the layout for this fragment
            this.mainView = inflater.inflate(R.layout.fragment_library, container, false);

            setupTabs();

            this.nowPlayingBar = new NowPlayingBar(getContext(), mainView);
            this.nowPlayingBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    transitionToNowPlaying();
                }
            });
        }

        return mainView;
    }

    private void transitionToNowPlaying(){
        final TextView smallName = (TextView)getView().findViewById(R.id.now_playing_small_songName);
        final TextView smallArtist = (TextView)getView().findViewById(R.id.now_playing_small_artistName);
        final ImageView smallAlbumArt = (ImageView)getView().findViewById(R.id.image_album_art);
        final ImageView smallPlayButton = (FloatingActionButton)getView().findViewById(R.id.now_playing_small_play_button);

        NowPlaying nowPlayingFrag = (NowPlaying)getActivity().getSupportFragmentManager().findFragmentByTag(getString(R.string.NOW_PLAYING_TAG));

        //Make sure Now Playing is instantiated
        if(nowPlayingFrag == null){
            nowPlayingFrag = new NowPlaying();
        }

        ViewCompat.setTransitionName(smallName, getString(R.string.song_title_transition_name));
        ViewCompat.setTransitionName(smallArtist, getString(R.string.artist_name_transition_name));
        ViewCompat.setTransitionName(smallPlayButton, getString(R.string.play_button_transition_name));
        ViewCompat.setTransitionName(smallAlbumArt, getString(R.string.album_art_transition_name));

        // Fade out the old fragment
//                Fade exitFade = new Fade();
//                exitFade.setDuration(3000);
//                setExitTransition(exitFade);

        TransitionSet enterTransitionSet = new TransitionSet();
        enterTransitionSet.addTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        enterTransitionSet.setDuration(1000);
//                enterTransitionSet.setStartDelay(FADE_DEFAULT_TIME);
        nowPlayingFrag.setSharedElementEnterTransition(enterTransitionSet);
//                nowPlayingFrag.setSharedElementReturnTransition(enterTransitionSet);


        nowPlayingFrag.setEnterTransition(new Slide(Gravity.BOTTOM).setDuration(1000));
        nowPlayingFrag.setExitTransition(new Slide(Gravity.BOTTOM).setDuration(1000));
//                ChangeBounds cb = new ChangeBounds();

        // Defines enter transition only for shared element
//                ChangeBounds changeBoundsTransition = (ChangeBounds)TransitionInflater.from(getContext()).inflateTransition(R.transition.change_bounds);
//                ChangeBounds changeBoundsTransition = new ChangeBounds();
//                nowPlayingFrag.setSharedElementEnterTransition(changeBoundsTransition);

        // Prevent transitions for overlapping
        nowPlayingFrag.setAllowEnterTransitionOverlap(true);
        nowPlayingFrag.setAllowReturnTransitionOverlap(true);


        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm
                .beginTransaction()
                .addSharedElement(smallPlayButton, getString(R.string.play_button_transition_name))
                .addSharedElement(smallName, getString(R.string.song_title_transition_name))
                .addSharedElement(smallArtist, getString(R.string.artist_name_transition_name))
                .addSharedElement(smallAlbumArt, getString(R.string.album_art_transition_name))
                .replace(R.id.fragment_container, nowPlayingFrag)
                .addToBackStack(null)
                .commit();
    } // end transitionToNowPlaying


    @Override
    public void onDestroy() {
        super.onDestroy();
        nowPlayingBar.removeSongEventListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(SPMainActivity.mPlayerService != null) {
            nowPlayingBar.update(SPMainActivity.mPlayerService.getCurrentSong());
        }
    }

    /**
     * Sets up the tab view at the top
     * of the library fragment.
     */
    private void setupTabs(){
        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        this.mlibraryPagerAdapter =
                new LibraryPagerAdapter(
                        getFragmentManager(), getContext());

        this.mViewPager = (ViewPager) mainView.findViewById(R.id.library_view_pager);
        this.mTabLayout = (TabLayout) mainView.findViewById(R.id.library_tab_layout);
        this.mTabLayout.setupWithViewPager(mViewPager, true);
        this.mViewPager.setAdapter(mlibraryPagerAdapter);
        this.mTabLayout.setSelectedTabIndicatorHeight(0);

        /* Set drawables for tab backgrounds */
        ((ViewGroup)mTabLayout.getChildAt(0)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.tab_background_left));
        ((ViewGroup)mTabLayout.getChildAt(0)).getChildAt(1).setBackground(getResources().getDrawable(R.drawable.tab_background_middle));
        ((ViewGroup)mTabLayout.getChildAt(0)).getChildAt(2).setBackground(getResources().getDrawable(R.drawable.tab_background_right));
    }
}



