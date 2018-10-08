package com.smartmusic.android.smartmusicplayer.library;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.smartmusic.android.smartmusicplayer.R;
import com.smartmusic.android.smartmusicplayer.album.AlbumActivity;
import com.smartmusic.android.smartmusicplayer.database.entities.Album;
import com.smartmusic.android.smartmusicplayer.database.entities.Song;
import com.smartmusic.android.smartmusicplayer.database.view_models.AlbumsViewModel;
import com.smartmusic.android.smartmusicplayer.database.view_models.SongsViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by holle on 3/7/2018.
 */

public class AlbumListFragment extends Fragment {

    private List<Album> _albums = null;

    private RecyclerView recyclerView = null;
    private AlbumAdapter albumAdapter = null;

    private AlbumsViewModel mModel;

    View rootView = null;

    public AlbumListFragment() {
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
        mModel = ViewModelProviders.of(this).get(AlbumsViewModel.class);

        // Create the observer which updates the UI.
        final Observer<List<Album>> albumObserver = new Observer<List<Album>>() {
            @Override
            public void onChanged(@Nullable final List<Album> newAlbumlist) {
                // Update the UI
                if(recyclerView != null) {
                    // Updating the songList will refresh the view
                    _albums = newAlbumlist;
                    ((AlbumAdapter)recyclerView.getAdapter()).setAlbums(newAlbumlist);
                }
            }
        };

        mModel.getAllAlbums().observe(this, albumObserver);
    }

    private void setUpRecyclerView(View fragView){

        recyclerView = (RecyclerView) fragView.findViewById(R.id.recyclerView);
        albumAdapter = new AlbumAdapter(getContext(), _albums);
        FastScroller fastScroller = (FastScroller) fragView.findViewById(R.id.fastscroll);

        recyclerView.setAdapter(albumAdapter);
        fastScroller.setRecyclerView(recyclerView);
        fastScroller.setBubbleColor(getResources().getColor(R.color.pastel_rose));
        fastScroller.setHandleColor(Color.WHITE);

        /*LLM extends Recycler view and specifies the layout*/
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);

        /*Links layout and divider to recyclerView*/
        recyclerView.setLayoutManager(gridLayoutManager);
    }

}
