package com.smartmusic.android.smartmusicplayer.album;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.smartmusic.android.smartmusicplayer.database.entities.Song;
import com.smartmusic.android.smartmusicplayer.R;
import com.smartmusic.android.smartmusicplayer.database.view_models.AlbumViewModel;
import com.smartmusic.android.smartmusicplayer.library.SongAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by holle on 7/9/2018.
 */

public class AlbumActivity extends AppCompatActivity {

    private AlbumViewModel mModel;
    private RecyclerView recyclerView;
    private List<Song> mSongs = new ArrayList<>();

    private String albumName;
    private String artistName;
    private Uri albumArt;


    public AlbumActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_layout);

        String extraUID = getIntent().getStringExtra(getString(R.string.EXTRA_ALBUM_UID));
        albumName = getIntent().getStringExtra(getString(R.string.EXTRA_ALBUM_NAME));
        artistName = getIntent().getStringExtra(getString(R.string.EXTRA_ALBUM_ARTIST));
        albumArt = Uri.parse(getIntent().getStringExtra(getString(R.string.EXTRA_ALBUM_ART)));

        setUpModel(extraUID);
        setupAlbumActivity();
    }

    private void setUpModel(String extraUID){
        // Get the ViewModel.
        mModel = ViewModelProviders.of(this).get(AlbumViewModel.class);
        mModel.setAlbumUID(extraUID);

        // Create the observer which updates the UI.
        final Observer<List<Song>> songObserver = new Observer<List<Song>>() {
            @Override
            public void onChanged(@Nullable final List<Song> newSonglist) {
                // Update the UI
                if(recyclerView != null) {
                    // Updating the songList will refresh the view
                    mSongs = newSonglist;
                    ((SongAdapter)recyclerView.getAdapter()).setSongs(newSonglist);

                    final TextView songCountTV = (TextView) findViewById(R.id.album_song_count);
                    songCountTV.setText(mSongs.size() > 1 ? mSongs.size() + " songs" : mSongs.size() + " song");
                }
            }
        };

        mModel.getAllAlbumSongs().observe(this, songObserver);
    }

    private void setupAlbumActivity(){
        final Toolbar toolbar = (Toolbar) findViewById(R.id.album_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);


        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.album_collapsing_toolbar);
        collapsingToolbar.setTitle(albumName);
        collapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);
        collapsingToolbar.setExpandedTitleColor(Color.WHITE);


        final ImageView albumImage = (ImageView) findViewById(R.id.album_image);

        Picasso.with(this)
                .load(albumArt)
                .error(R.drawable.temp_album_art)
                .placeholder(R.drawable.temp_album_art)
                .into(albumImage);


        final TextView artistNameTV = (TextView) findViewById(R.id.album_artist_name);
        artistNameTV.setText(artistName);


        final ImageView artistLetter = (ImageView) findViewById(R.id.album_gmailitem_letter);
        String letter = String.valueOf(artistName.charAt(0));
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(letter, ColorGenerator.MATERIAL.getRandomColor());
        artistLetter.setImageDrawable(drawable);

        setUpAlbumRecyclerView();
    }

    private void setUpAlbumRecyclerView(){
        recyclerView = findViewById(R.id.recyclerView);
        FastScroller fastScroller = findViewById(R.id.fastscroll);
        final SongAdapter albumAdapter = new SongAdapter(this, mSongs, SongAdapter.HolderType.ALBUM_LIST);

        /*LLM extends Recycler view and specifies the layout*/
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        final DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL);

        /*Links layout and divider to recyclerView*/
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(albumAdapter);

        fastScroller.setRecyclerView(recyclerView);
        fastScroller.setBubbleColor(getResources().getColor(R.color.pastel_rose));
        fastScroller.setHandleColor(Color.WHITE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
//        return super.onOptionsItemSelected(item);
    }


    //    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            // Respond to the action bar's Up/Home button
//            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
//                Toast.makeText(this, "Up Button is clicked!", Toast.LENGTH_SHORT).show();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
