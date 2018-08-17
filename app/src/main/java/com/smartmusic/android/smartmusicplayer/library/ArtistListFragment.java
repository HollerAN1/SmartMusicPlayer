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
import com.smartmusic.android.smartmusicplayer.comparators.artists.ArtistNameComparator;
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

    View rootView = null;

    public ArtistListFragment() {
        // Required empty public constructor
    }

    private void initData(){
        this._artists = SPMainActivity.mDatabaseService.getArtists(new ArtistNameComparator());
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
        recyclerView = fragView.findViewById(R.id.recyclerView);
        artistAdapter = new ArtistAdapter(getContext(), _artists);

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
