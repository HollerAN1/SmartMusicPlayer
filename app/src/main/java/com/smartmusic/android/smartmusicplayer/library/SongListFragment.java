package com.smartmusic.android.smartmusicplayer.library;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smartmusic.android.smartmusicplayer.SPMainActivity;
import com.smartmusic.android.smartmusicplayer.SongEvent;
import com.smartmusic.android.smartmusicplayer.SongEventListener;
import com.smartmusic.android.smartmusicplayer.SongPlayerService;
import com.smartmusic.android.smartmusicplayer.comparators.songs.SongNameComparator;
import com.smartmusic.android.smartmusicplayer.model.SongInfo;
import com.smartmusic.android.smartmusicplayer.R;
import com.wnafee.vector.MorphButton;

import java.util.ArrayList;
import java.util.Comparator;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Displays the list of songs under the
 * "Songs" tab in the View Pager.
 *
 * Created by holle on 3/7/2018.
 */

public class SongListFragment extends Fragment implements SongEventListener {

    private ArrayList<SongInfo> _songs = null;

    /*Views*/
    private static RecyclerView recyclerView;

    /*Resources*/
    private static SongAdapter songAdapter;

    /*Fonts*/
    Typeface tvSongNameFont;
    Typeface tvArtistNameFont;
    Typeface nowPlayingFont;

    private static boolean eventHandled = false;


    /*Stores information about the last selected song*/
    Boolean prevExists = false;
    Integer prevPosition;


    /**
     * Listeners
     */
    View.OnClickListener mOnClickListener;
    SongAdapter.OnItemClickListener mOnItemClickLListener;

    private SongEventListener songEventListener;

    View mainView = null;

    private boolean playBarShowing = false;

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

    public SongListFragment() {
        // Required empty public constructor
    }


    private void initData(){
        this._songs = SPMainActivity.mDatabaseService.getSongs(new SongNameComparator());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);

        if( mainView == null ) { // Setup view
            // Inflate the layout for this fragment
            mainView = inflater.inflate(R.layout.recycler_view_layout, container, false);
            initData();
            setUpRecyclerView(mainView);

            SlideInLeftAnimator animator = new SlideInLeftAnimator();
            animator.setInterpolator(new OvershootInterpolator());
            // or recyclerView.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(1f)));

            // Uncommenting this causes flash on item
            // reload.
            //recyclerView.setItemAnimator(animator);


            SPMainActivity.getSongEventHandler().addSongEventListener(this);
        }

        return mainView;
    }

    private void setUpRecyclerView(final View fragView){

        /*Links objects on XML to javadoc*/
        tvSongNameFont
                = Typeface.createFromAsset(
                        getActivity().getAssets(),
                        getString(R.string.raleway_regular_font));
        tvArtistNameFont
                = Typeface.createFromAsset(
                        getActivity().getAssets(),
                        getString(R.string.high_tea_font));

        recyclerView = fragView.findViewById(R.id.recyclerView);
        songAdapter = new SongAdapter(getContext(), _songs);

        recyclerView.setAdapter(songAdapter);


        /*LLM extends Recycler view and specifies the layout*/
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        /*Links layout and divider to recyclerView*/
        recyclerView.setLayoutManager(linearLayoutManager);

        //Handles Play/Stop button clicks
        songAdapter.setOnItemClickListener(new SongAdapter.OnItemClickListener() {
            /*Implements interface method onItemClick*/
            @Override
            public void onItemClick(final MorphButton b, final View view, final SongInfo obj, final int position, final ArrayList<SongInfo> songs, final int i) {
                // Play the selected song
                if(!b.isSelected()){
                    SPMainActivity.mPlayerService.playSong(obj);
                }
                // Stop the selected song
                if (b.isSelected() || (SPMainActivity.mPlayerService.isSongPlaying())) {
                    SPMainActivity.mPlayerService.stop();
                }
            }
        });


        //Handles background clicks
//        songAdapter.setBackOnItemClickListener(new SongAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(ImageButton b, View view, SongInfo obj, int position, ArrayList<SongInfo> songs, int i) {
//
//
//            }
//        });
    }

    public void setRecyclerViewNormalTheme(int index){
        _songs.get(index).setSelected(false);
        songAdapter.notifyItemChanged(index);
    }

    public void setRecyclerViewPlayingTheme(int index){
        _songs.get(index).setSelected(true);
        songAdapter.notifyItemChanged(index);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SPMainActivity.getSongEventHandler().removeSongEventListener(this);
    }

    @Override
    public synchronized void onSongChangeEvent(SongEvent e) {
        // Reset the last song's view
        if(prevExists) {
            setRecyclerViewNormalTheme(prevPosition);
            prevExists = false;
        }


        if (!prevExists) {
            prevExists = true;
            prevPosition = e.getSongIndex();
        }

        // Update the currently playing song
        setRecyclerViewPlayingTheme(e.getSongIndex());
    }

    @Override
    public void onSongStopEvent(SongEvent e) {
        // If a song was playing, reset the view
        if(prevExists){
            setRecyclerViewNormalTheme(prevPosition);
            prevExists = false;
        }
    }

    @Override
    public void onSongAddedEvent(SongEvent e) {}
    @Override
    public void onSongRemovedEvent(SongEvent e) {}
    @Override
    public void onShuffleOnEvent(SongEvent e) {}
    @Override
    public void onShuffleOffEvent(SongEvent e) {}

}
