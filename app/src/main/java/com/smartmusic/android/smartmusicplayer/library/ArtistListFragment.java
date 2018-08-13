package com.smartmusic.android.smartmusicplayer.library;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smartmusic.android.smartmusicplayer.SPMainActivity;
import com.smartmusic.android.smartmusicplayer.model.ArtistInfo;
import com.smartmusic.android.smartmusicplayer.R;

import java.util.ArrayList;

/**
 * Created by holle on 3/7/2018.
 */

public class ArtistListFragment extends Fragment {

    private ArrayList<ArtistInfo> _artists = null;

    private RecyclerView recyclerView = null;
    private ArtistAdapter artistAdapter = null;

    private Typeface artistNameFont = null;
    private Typeface songCountFont = null;

    public final static String LIBRARY_TAG = "library_tag";
    public final static String NOW_PLAYING_TAG = "now_playing_tag";
    public final static String PLAYLISTS_TAG = "playlists_tag";
    public final static String SETTINGS_TAG = "settings_tag";

    View rootView = null;

    public ArtistListFragment() {
        // Required empty public constructor
    }

    private void initData(){
//        Library libraryFragment = (Library) getFragmentManager().findFragmentByTag(LIBRARY_TAG);
        this._artists = SPMainActivity.getArtists();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);

        if( rootView == null ) {
            rootView = inflater.inflate(R.layout.recycler_view_layout, container, false);
            initData();
            setUpRecyclerView(rootView);
        }

        return rootView;
    }


    private void setUpRecyclerView(View fragView){

        /*Links objects on XML to javadoc*/
        artistNameFont = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Raleway-Regular.ttf");
        songCountFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/highTea.otf");

        recyclerView = (RecyclerView) fragView.findViewById(R.id.recyclerView);
        artistAdapter = new ArtistAdapter(getContext(), _artists, artistNameFont, songCountFont);

        recyclerView.setAdapter(artistAdapter);

        /*LLM extends Recycler view and specifies the layout*/
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        /*Adds dividers to the linear layout for recyclerView*/
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
//                linearLayoutManager.getOrientation());

        /*Links layout and divider to recyclerView*/
        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.addItemDecoration(dividerItemDecoration);

    }
}
