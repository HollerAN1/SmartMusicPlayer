package com.smartmusic.android.smartmusicplayer.library;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartmusic.android.smartmusicplayer.SPMainActivity;
import com.smartmusic.android.smartmusicplayer.album.AlbumActivity;
import com.smartmusic.android.smartmusicplayer.comparators.albums.AlbumNameComparator;
import com.smartmusic.android.smartmusicplayer.model.AlbumInfo;
import com.smartmusic.android.smartmusicplayer.R;

import java.util.ArrayList;

/**
 * Created by holle on 3/7/2018.
 */

public class AlbumListFragment extends Fragment {

    private ArrayList<AlbumInfo> _albums = null;

    private RecyclerView recyclerView = null;
    private AlbumAdapter albumAdapter = null;

    View rootView = null;

    public AlbumListFragment() {
        // Required empty public constructor
    }

    private void initData(){
        this._albums = SPMainActivity.mDatabaseService.getAlbums(new AlbumNameComparator());
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

        recyclerView = (RecyclerView) fragView.findViewById(R.id.recyclerView);
        albumAdapter = new AlbumAdapter(getContext(), _albums);

        recyclerView.setAdapter(albumAdapter);

        /*LLM extends Recycler view and specifies the layout*/
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);

        /*Links layout and divider to recyclerView*/
        recyclerView.setLayoutManager(gridLayoutManager);


//        albumAdapter.setOnItemClickListener(new AlbumAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(ImageView b, View view, AlbumInfo obj, int position, ArrayList<AlbumInfo> albums, int i) {
//                Intent intent = new Intent(getContext(), AlbumActivity.class);
//                intent.putExtra("EXTRA_ALBUM_NAME", obj.getAlbumName());
//                intent.putExtra("EXTRA_ALBUM_ARTIST", obj.getArtistname());
//                intent.putExtra("EXTRA_ALBUM_ART", obj.getAlbumArt().toString());
//                intent.putExtra("EXTRA_ALBUM_SONGLIST", obj.getSongs());
//
//                final ImageView albumArt = (ImageView) view.findViewById(R.id.tile_albumArt);
//                final TextView artistName = (TextView) view.findViewById(R.id.tile_artist_name);
////                final TextView albumName = (TextView) view.findViewById(R.id.tile_album_name);
////
////                Pair<View, String> albumArtTransition = Pair.create((View)albumArt, getString(R.string.album_art_transition_name));
////                Pair<View, String> artistNameTransition = Pair.create((View)artistName, getString(R.string.artist_name_transition_name));
////                Pair<View, String> containerTransition = Pair.create(view, getString(R.string.album_container_transition_name));
////                ActivityOptionsCompat options = ActivityOptionsCompat
////                        .makeSceneTransitionAnimation(getActivity(), albumArtTransition, artistNameTransition, containerTransition);
//
//                startActivity(intent);
//            }
//        });

    }

}
