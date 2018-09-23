package com.smartmusic.android.smartmusicplayer.library;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.smartmusic.android.smartmusicplayer.SPMainActivity;
import com.smartmusic.android.smartmusicplayer.events.SongPlaybackEvent;
import com.smartmusic.android.smartmusicplayer.events.SongPlaybackEventListener;
import com.smartmusic.android.smartmusicplayer.database.entities.Song;
import com.smartmusic.android.smartmusicplayer.R;
import com.smartmusic.android.smartmusicplayer.database.view_models.SongsViewModel;

import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Displays the list of songs under the
 * "Songs" tab in the View Pager.
 *
 * Created by holle on 3/7/2018.
 */

public class SongListFragment extends Fragment implements SongPlaybackEventListener {

    private List<Song> _songs;

    private RecyclerView recyclerView;


    /*Stores information about the last selected song*/
    Boolean prevExists = false;
    Song prevSong;

    View mainView = null;

    private SongsViewModel mModel;

    public SongListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);

        if( mainView == null ) { // Setup view

            // Inflate the layout for this fragment
            mainView = inflater.inflate(R.layout.recycler_view_layout, container, false);
            setUpModel();
            setUpRecyclerView(mainView);

            SlideInLeftAnimator animator = new SlideInLeftAnimator();
            animator.setInterpolator(new OvershootInterpolator());
            // or recyclerView.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(1f)));

            // Uncommenting this causes flash on item
            // reload.
            //recyclerView.setItemAnimator(animator);


            SPMainActivity.getSongEventHandler().addSongPlaybackEventListener(this);
        }

        return mainView;
    }

    private void setUpModel(){
        // Get the ViewModel.
        mModel = ViewModelProviders.of(this).get(SongsViewModel.class);

        // Create the observer which updates the UI.
        final Observer<List<Song>> songObserver = new Observer<List<Song>>() {
            @Override
            public void onChanged(@Nullable final List<Song> newSonglist) {
                // Update the UI
                if(recyclerView != null) {
                    // Updating the songList will refresh the view
                    _songs = newSonglist;
                    if(SPMainActivity.mPlayerService != null) {
                        SPMainActivity.mPlayerService.setSongList(newSonglist);
                    }
                    ((SongAdapter)recyclerView.getAdapter()).setSongs(newSonglist);
            }
            }
        };

        mModel.getAllSongs().observe(this, songObserver);
    }

    private void setUpRecyclerView(final View fragView){
        recyclerView = fragView.findViewById(R.id.recyclerView);
        SongAdapter songAdapter = new SongAdapter(getContext(), mModel.getAllSongs().getValue());
        FastScroller fastScroller = fragView.findViewById(R.id.fastscroll);

        recyclerView.setAdapter(songAdapter);

        fastScroller.setRecyclerView(recyclerView);
        fastScroller.setBubbleColor(getResources().getColor(R.color.pastel_rose));
        fastScroller.setHandleColor(Color.WHITE);


        /*LLM extends Recycler view and specifies the layout*/
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        /*Links layout and divider to recyclerView*/
        recyclerView.setLayoutManager(linearLayoutManager);


//        songAdapter.setOnItemClickListener(new SongAdapter.OnItemClickListener() {
//            /*Implements interface method onItemClick*/
//            @Override
//            public void onItemClick(final MorphButton b, final View view, final Song obj, final int position, final List<Song> songs, final int i) {
//                // Play the selected song
//                if(!b.isSelected()){
//                    SPMainActivity.mPlayerService.playSong(obj);
//                }
//                // Stop the selected song
//                if (b.isSelected() || (SPMainActivity.mPlayerService.isSongPlaying())) {
//                    SPMainActivity.mPlayerService.stop();
//                }
//            }
//        });
    }

    public void setRecyclerViewNormalTheme(Song song){
        song.setSelected(false);
        if(_songs != null) {
            recyclerView.getAdapter().notifyItemChanged(_songs.indexOf(song));
        }
    }

    public void setRecyclerViewPlayingTheme(Song song){
        song.setSelected(true);
        if(_songs != null) {
            recyclerView.getAdapter().notifyItemChanged(_songs.indexOf(song));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SPMainActivity.getSongEventHandler().removeSongPlaybackEventListener(this);
    }

    @Override
    public synchronized void onSongChangeEvent(SongPlaybackEvent e) {
        Song songPlaying = e.getSource();

        // Reset the last song's view
        if(prevExists) {
            setRecyclerViewNormalTheme(prevSong);
            prevExists = false;
        }


        if (!prevExists) {
            prevExists = true;
            prevSong = songPlaying;
        }

        // Update the currently playing song
        setRecyclerViewPlayingTheme(songPlaying);
    }

    @Override
    public void onSongStopEvent(SongPlaybackEvent e) {
        // If a song was playing, reset the view
        if(prevExists){
            setRecyclerViewNormalTheme(prevSong);
            prevExists = false;
        }
    }
}
