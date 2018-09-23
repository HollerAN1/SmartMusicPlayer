package com.smartmusic.android.smartmusicplayer.album;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
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
import com.smartmusic.android.smartmusicplayer.database.entities.Song;
import com.smartmusic.android.smartmusicplayer.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by holle on 7/9/2018.
 */

public class AlbumActivity extends AppCompatActivity {

    private String albumName;
    private String artistName;
    private Uri albumArt;
    private ArrayList<Song> _albumSongs;

    public AlbumActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_layout);

        setUpAlbumValues();
        setupAlbumActivity();
    }

    private void setUpAlbumValues(){
        albumName = getIntent().getStringExtra("EXTRA_ALBUM_NAME");
        artistName = getIntent().getStringExtra("EXTRA_ALBUM_ARTIST");
        albumArt = Uri.parse(getIntent().getStringExtra("EXTRA_ALBUM_ART"));
        _albumSongs = (ArrayList<Song>) getIntent().getSerializableExtra("EXTRA_ALBUM_SONGLIST");
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

        final TextView songCountTV = (TextView) findViewById(R.id.album_song_count);
        songCountTV.setText(_albumSongs.size() > 1 ? _albumSongs.size() + " songs" : _albumSongs.size() + " song");

        final ImageView artistLetter = (ImageView) findViewById(R.id.album_gmailitem_letter);
        String letter = String.valueOf(artistName.charAt(0));
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(letter, ColorGenerator.MATERIAL.getRandomColor());
        artistLetter.setImageDrawable(drawable);

        setUpAlbumRecyclerView();
    }

    private void setUpAlbumRecyclerView(){
//        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//        Typeface tvSongNameFont = Typeface.createFromAsset(this.getAssets(),"fonts/Raleway-Regular.ttf");
//        Typeface tvArtistNameFont = Typeface.createFromAsset(this.getAssets(), "fonts/highTea.otf");
//        final SongDefaultAdapter albumAdapter = new SongDefaultAdapter(this, _albumSongs, tvSongNameFont, tvArtistNameFont);
//
//        /*LLM extends Recycler view and specifies the layout*/
//        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        final DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL);
//
//        /*Links layout and divider to recyclerView*/
//        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.addItemDecoration(dividerItemDecoration);
//        recyclerView.setAdapter(albumAdapter);
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
