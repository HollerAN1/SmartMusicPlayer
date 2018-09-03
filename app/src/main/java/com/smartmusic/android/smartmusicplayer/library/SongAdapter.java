package com.smartmusic.android.smartmusicplayer.library;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;
import com.smartmusic.android.smartmusicplayer.diff_callbacks.SongDiffCallback;
import com.smartmusic.android.smartmusicplayer.database.entities.Song;
import com.smartmusic.android.smartmusicplayer.R;
import com.squareup.picasso.Picasso;
import com.wnafee.vector.MorphButton;

import java.util.List;

/**
 * Adapters provide a binding from an app-specific data set to views that are displayed within a RecyclerView.
 * SongAdapter extends the RecyclerView Adapter to link the songInfo class to the layout
 * THIS HANDLES DATA COLLECTION AND BINDS IT TO THE VIEW
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongHolder> implements SectionTitleProvider {

    /**
     * ArrayList of all songs
     * */
    private List<Song> _songs;

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

    SongHolder songHolder;

    /**
     * Constructor for SongAdapter
     * @param context context
     * @param songs list of songs
     */
    public SongAdapter(Context context, List<Song> songs) {
        this.context = context;
        this._songs = songs;
    }

    public interface OnItemClickListener {
        void onItemClick(MorphButton b, View view, Song obj, int position, List<Song> songs, int i);
    }

    @Override
    public String getSectionTitle(int position) {
        //this String will be shown in a bubble for specified position
        if(_songs != null) {
            return _songs.get(position).getSongName().substring(0, 1);
        }
        return "";
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
     * Called when RecyclerView needs a new RecyclerView.HeaderViewHolder
     * of the given type to represent an item.

     This new HeaderViewHolder should be constructed with a new View
     that can represent the items of the given type. You can
     either create a new View manually or inflate it from an
     XML layout file.

     The new HeaderViewHolder will be used to display items of the
     adapter using onBindViewHolder(HeaderViewHolder, int, List).
     Since it will be re-used to display different items in
     the data set, it is a good idea to cache references to
     sub views of the View to avoid unnecessary findViewById(int)
     calls.
     * @param viewGroup The ViewGroup into which the new View will
     *                  be added after it is bound to an adapter position.
     * @param i The view type of the new View.
     * @return 	A new HeaderViewHolder that holds a View of the given view type.
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
     Override onBindViewHolder(HeaderViewHolder, int, List) instead if Adapter
     can handle efficient partial bind.
     * @param songHolder The HeaderViewHolder which should be updated to represent
     *                   the contents of the item at the given position in
     *                   the data set.
     * @param i          The position of the item within the adapter's data
     *                   set.
     */
    @Override
    public void onBindViewHolder(final SongHolder songHolder, final int i) {
        final Song s = _songs.get(i);
        this.songHolder = songHolder;
        songHolder.tvSongName.setText(_songs.get(i).getSongName());
        songHolder.tvSongArtist.setText(_songs.get(i).getArtistName());

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

        if(!_songs.get(i).isSelected()){
            // Not playing
            setSongNotPlayingView(songHolder);
        } else {
            // Playing
            setSongPlayingView(songHolder);
        }

        songHolder.background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(backOnItemClickListener != null){
                    backOnItemClickListener.onItemClick(songHolder.btnAction,v,s,songHolder.getAdapterPosition(),_songs,i);
                }
            }
        });
    }

    /**
     * Updates the view holder to the
     * song playing state.
     * @param songHolder
     */
    private void setSongPlayingView(SongHolder songHolder){
        // Playing
        songHolder.tvSongName.setHorizontallyScrolling(true);
//            songHolder.background.setBackgroundColor(context.getResources().getColor(R.color.tintedBackground));
        songHolder.background.setBackgroundResource(R.drawable.orange_glow_gradient);
//            songHolder.tvSongName.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        if(songHolder.btnAction.getState() != MorphButton.MorphState.END) {
            songHolder.btnAction.setState(MorphButton.MorphState.END, true);
        }
        songHolder.btnAction.setSelected(true);
        songHolder.btnAction.setForegroundColorFilter(Color.WHITE,
                PorterDuff.Mode.SRC_ATOP);
        songHolder.tvSongName.setSelected(true);
        songHolder.tvSongArtist.setSelected(true);
    }

    /**
     * Updates the view holder to the song
     * not playing state.
     * @param songHolder
     */
    private void setSongNotPlayingView(SongHolder songHolder){
        // Not playing
//            songHolder.tvSongName.setTextColor(Color.WHITE);
        songHolder.tvSongName.setHorizontallyScrolling(false);
        songHolder.background.setBackgroundResource(R.drawable.ripple_effect);
        if(songHolder.btnAction.getState() != MorphButton.MorphState.START) {
            songHolder.btnAction.setState(MorphButton.MorphState.START, true);
        }
        songHolder.btnAction.setForegroundColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        songHolder.btnAction.setSelected(false);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if( _songs != null) {
            return _songs.size();
        }
        return 0;
    }


    public void setSongs(List<Song> songs){
        final SongDiffCallback callback = new SongDiffCallback(this._songs, songs);
        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        this._songs = songs;
        result.dispatchUpdatesTo(this);
    }

    public void addSong(Song song){
        List<Song> newSongs = _songs;
        newSongs.add(song);
        setSongs(newSongs);
    }


    /**
     * A HeaderViewHolder describes an item view and metadata about its place
     * within the RecyclerView.

     RecyclerView.Adapter implementations should subclass HeaderViewHolder
     and add fields for caching potentially expensive findViewById(int)
     results.

     While RecyclerView.LayoutParams belong to the RecyclerView.LayoutManager,
     ViewHolders belong to the adapter. Adapters should feel free to use their
     own custom HeaderViewHolder implementations to store data that makes binding
     view contents easier. Implementations should assume that individual item
     views will hold strong references to HeaderViewHolder objects and that
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

        public void onClick(View view){

        }
    }
}


