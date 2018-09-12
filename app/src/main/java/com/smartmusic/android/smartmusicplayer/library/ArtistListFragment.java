package com.smartmusic.android.smartmusicplayer.library;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.smartmusic.android.smartmusicplayer.R;
import com.smartmusic.android.smartmusicplayer.database.entities.Artist;
import com.smartmusic.android.smartmusicplayer.database.entities.Song;
import com.smartmusic.android.smartmusicplayer.database.view_models.ArtistsViewModel;
import com.smartmusic.android.smartmusicplayer.database.view_models.SongsViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by holle on 3/7/2018.
 */

public class ArtistListFragment extends Fragment {

    private List<Artist> _artists = null;

    private RecyclerView recyclerView = null;
    private ArtistAdapter artistAdapter = null;

    private ArtistsViewModel mModel;

    View rootView = null;

    public ArtistListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);

        if( rootView == null ) {
            rootView = inflater.inflate(R.layout.recycler_view_layout, container, false);
            setUpModel();
            setUpRecyclerView(rootView);
        }

        return rootView;
    }

    private void setUpModel(){
        // Get the ViewModel.
        mModel = ViewModelProviders.of(this).get(ArtistsViewModel.class);

        // Create the observer which updates the UI.
        final Observer<List<Artist>> artistObserver = new Observer<List<Artist>>() {
            @Override
            public void onChanged(@Nullable final List<Artist> newArtistlist) {
                // Update the UI
                if(recyclerView != null) {
                    // Updating the songList will refresh the view
                    _artists = newArtistlist;
                    ((ArtistAdapter)recyclerView.getAdapter()).setArtists(newArtistlist);
                }
            }
        };

        mModel.getAllArtists().observe(this, artistObserver);
    }

    private void setUpRecyclerView(View fragView){
        recyclerView = fragView.findViewById(R.id.recyclerView);
        artistAdapter = new ArtistAdapter(getContext(), _artists);
        FastScroller fastScroller = (FastScroller) fragView.findViewById(R.id.fastscroll);

        recyclerView.setAdapter(artistAdapter);
        fastScroller.setRecyclerView(recyclerView);
        fastScroller.setBubbleColor(getResources().getColor(R.color.pastel_rose));
        fastScroller.setHandleColor(Color.WHITE);

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
