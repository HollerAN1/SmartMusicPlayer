package com.smartmusic.android.smartmusicplayer.library;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartmusic.android.smartmusicplayer.model.SongInfo;
import com.smartmusic.android.smartmusicplayer.R;
import com.squareup.picasso.Picasso;
import com.wnafee.vector.MorphButton;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Adapters provide a binding from an app-specific data set to views that are displayed within a RecyclerView.
 * SongAdapter extends the RecyclerView Adapter to link the songInfo class to the layout
 * THIS HANDLES DATA COLLECTION AND BINDS IT TO THE VIEW
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongHolder> {

    /**
     * ArrayList of all songs
     * */
    private ArrayList<SongInfo> _songs = new ArrayList<>();
    /**
     * Context is used to get an inflater to inflate the views in getView method.
     * An Inflater instantiates a layout XML file into its corresponding View objects
     */
    private Context context;

    /**
     * Interface definition for a callback to be invoked when an item in this AdapterView has been clicked.
     */
    private OnItemClickListener mOnItemClickListener;

    private OnItemClickListener backOnItemClickListener;

    Typeface songNameTypeface;
    Typeface artistNameTypeface;

    /**
     * Tells whether a song is currently playing.
     */
    Boolean songPlaying;

    SongHolder songHolder;

    /**
     * Constructor for SongAdapter
     * @param context context
     * @param songs list of songs
     */
    public SongAdapter(Context context, ArrayList<SongInfo> songs, Typeface typeface, Typeface aTypeface) {
        this.context = context;
        Collections.sort(songs);
        System.err.println("Songs size:" + songs.size());
        this._songs = songs;
        this.songNameTypeface = typeface;
        this.artistNameTypeface = aTypeface;
        this.songPlaying = false;
    }

    public void setSongPlaying(Boolean bool){
        this.songPlaying = bool;
    }

    public boolean getSongPlaying(){ return this.songPlaying; }

    public interface OnItemClickListener {
        void onItemClick(MorphButton b, View view, SongInfo obj, int position, ArrayList<SongInfo> songs, int i);
    }

    /**
     * Setter Method for OnItemClickListener
     * @param mItemClickListener the listener
     */
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public void setBackOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.backOnItemClickListener = mItemClickListener;
    }

    public SongHolder getSongHolder(){
        return this.songHolder;
    }

    /**
     * Called when RecyclerView needs a new RecyclerView.ViewHolder
     * of the given type to represent an item.

     This new ViewHolder should be constructed with a new View
     that can represent the items of the given type. You can
     either create a new View manually or inflate it from an
     XML layout file.

     The new ViewHolder will be used to display items of the
     adapter using onBindViewHolder(ViewHolder, int, List).
     Since it will be re-used to display different items in
     the data set, it is a good idea to cache references to
     sub views of the View to avoid unnecessary findViewById(int)
     calls.
     * @param viewGroup The ViewGroup into which the new View will
     *                  be added after it is bound to an adapter position.
     * @param i The view type of the new View.
     * @return 	A new ViewHolder that holds a View of the given view type.
     */
    @Override
    public SongHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        /*Inflate a new view hierarchy from the specified xml resource.
        .inflate(resource, root view, boolean attach to root)*/
        View myView = LayoutInflater.from(context).inflate(R.layout.row_song,viewGroup,false);
        return new SongHolder(myView);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method should update the contents of the itemView to reflect the
     * item at the given position.

     Note that unlike ListView, RecyclerView will not call this method again
     if the position of the item changes in the data set unless the item
     itself is invalidated or the new position cannot be determined. For
     this reason, you should only use the position parameter while acquiring
     the related data item inside this method and should not keep a copy of
     it. If you need the position of an item later on (e.g. in a click listener),
     use getAdapterPosition() which will have the updated adapter position.
     Override onBindViewHolder(ViewHolder, int, List) instead if Adapter
     can handle efficient partial bind.
     * @param songHolder The ViewHolder which should be updated to represent
     *                   the contents of the item at the given position in
     *                   the data set.
     * @param i          The position of the item within the adapter's data
     *                   set.
     */
    @Override
    public void onBindViewHolder(final SongHolder songHolder, final int i) {
        final SongInfo s = _songs.get(i);
        this.songHolder = songHolder;
        songHolder.tvSongName.setText(_songs.get(i).getSongname());
        songHolder.tvSongName.setTypeface(songNameTypeface);
        songHolder.tvSongArtist.setText(_songs.get(i).getArtistname());
        songHolder.tvSongArtist.setTypeface(artistNameTypeface);


        Uri uri = _songs.get(i).getAlbumArt();
        Picasso.with(context)
                .load(uri)
                .placeholder(R.drawable.temp_album_art)
                .error(R.drawable.temp_album_art)
                .into(songHolder.tvAlbumArt);

        songHolder.btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(songHolder.btnAction,v, s, songHolder.getAdapterPosition(), _songs, i);

                }

            }
        });

        if(!_songs.get(i).getSelected()){
            // Not playing
            songHolder.tvSongName.setTextColor(Color.BLACK);
            songHolder.tvSongName.setHorizontallyScrolling(false);
            songHolder.background.setBackgroundResource(R.drawable.ripple_effect);
            if(songHolder.btnAction.getState() != MorphButton.MorphState.START) {
                songHolder.btnAction.setState(MorphButton.MorphState.START, true);
            }
            songHolder.btnAction.setForegroundColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
            songHolder.btnAction.setSelected(false);
        } else {
            // Playing
            songHolder.background.setBackgroundColor(context.getResources().getColor(R.color.tintedBackground));
            songHolder.tvSongName.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            songHolder.tvSongName.setHorizontallyScrolling(true);
            if(songHolder.btnAction.getState() != MorphButton.MorphState.END) {
                songHolder.btnAction.setState(MorphButton.MorphState.END, true);
            }
            songHolder.btnAction.setSelected(true);
            songHolder.btnAction.setForegroundColorFilter(context.getResources().getColor(R.color.colorPrimaryDark),
                    PorterDuff.Mode.SRC_ATOP);
        }

        songHolder.background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(backOnItemClickListener != null){
                    backOnItemClickListener.onItemClick(songHolder.btnAction,v,s,songHolder.getAdapterPosition(),_songs,i);
                }
            }
        });
//        songHolder.background.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public void onClick(View v) {
//                if (backOnItemClickListener != null) {
//                    backOnItemClickListener.onTouch(songHolder.background);
//                }
//            }
//        });
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return _songs.size();
    }

    /**
     * A ViewHolder describes an item view and metadata about its place
     * within the RecyclerView.

     RecyclerView.Adapter implementations should subclass ViewHolder
     and add fields for caching potentially expensive findViewById(int)
     results.

     While RecyclerView.LayoutParams belong to the RecyclerView.LayoutManager,
     ViewHolders belong to the adapter. Adapters should feel free to use their
     own custom ViewHolder implementations to store data that makes binding
     view contents easier. Implementations should assume that individual item
     views will hold strong references to ViewHolder objects and that
     RecyclerView instances may hold strong references to extra off-screen
     item views for caching purposes
     */
    public class SongHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvSongName,tvSongArtist;
        ImageView tvAlbumArt;
        MorphButton btnAction;
        View background;
        Boolean selected = false;

        public SongHolder(View itemView) {
            super(itemView);
            tvSongName = (TextView) itemView.findViewById(R.id.tvSongName);
            tvSongArtist = (TextView) itemView.findViewById(R.id.tvArtistName);
            btnAction = (MorphButton) itemView.findViewById(R.id.btnPlay);
            background = (View)itemView.findViewById(R.id.list_background);
            tvAlbumArt = (ImageView)itemView.findViewById(R.id.list_albumArt);
        }
        public void setSelected(Boolean bool){this.selected = bool;}

        public Boolean isSelected(){return this.selected;}

        public void setSongName(String name){
            tvSongName.setText(name);
        }

        public void setArtistName(String name){
            tvSongArtist.setText(name);
        }

        public void onClick(View view){

        }
    }
}


