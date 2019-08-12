package com.smartmusic.android.smartmusicplayer.library;


import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;
import com.smartmusic.android.smartmusicplayer.SPMainActivity;
import com.smartmusic.android.smartmusicplayer.diff_callbacks.SongDiffCallback;
import com.smartmusic.android.smartmusicplayer.database.entities.Song;
import com.smartmusic.android.smartmusicplayer.R;
import com.smartmusic.android.smartmusicplayer.library.view_holders.SongHolder_Album;
import com.smartmusic.android.smartmusicplayer.library.view_holders.SongHolder_Library;
import com.smartmusic.android.smartmusicplayer.library.view_holders.SongListViewHolder;
import com.smartmusic.android.smartmusicplayer.utils.SPUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Adapters provide a binding from an app-specific data set to views that are displayed within a RecyclerView.
 * SongAdapter extends the RecyclerView Adapter to link the songInfo class to the layout
 * THIS HANDLES DATA COLLECTION AND BINDS IT TO THE VIEW
 */

public class SongAdapter extends RecyclerView.Adapter<SongListViewHolder> implements SectionTitleProvider {

    private List<Song> _songs;
    private Context context;
    private HolderType type;

    public enum HolderType {
        LIBRARY, ALBUM_LIST
    }

    /**
     * Constructor for SongAdapter
     * @param context context
     * @param songs list of songs
     */
    public SongAdapter(Context context, List<Song> songs, HolderType type) {
        this.context = context;
        this._songs = songs;
        this.type = type;
    }

    @Override
    public String getSectionTitle(int position) {
        // RecyclerView Fast Scroll
        // This String will be shown in a bubble for specified position
        if(_songs != null) {
            return _songs.get(position).getSongName().substring(0, 1);
        }
        return "";
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
    public SongListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        /*Inflate a new view hierarchy from the specified xml resource.
        .inflate(resource, root view, boolean attach to root)*/

        View holderView;
        switch(type){
            case LIBRARY:
                holderView = LayoutInflater.from(context).inflate(R.layout.row_song_flat,viewGroup,false);
                return new SongHolder_Library(context, holderView);
            case ALBUM_LIST:
                holderView = LayoutInflater.from(context).inflate(R.layout.row_song_default,viewGroup,false);
                return new SongHolder_Album(context, holderView);
            default:
                holderView = LayoutInflater.from(context).inflate(R.layout.row_song_flat,viewGroup,false);
                return new SongHolder_Library(context, holderView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return this.type.ordinal();
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
    public void onBindViewHolder(@NonNull final SongListViewHolder songHolder, final int i) {
        final Song s = _songs.get(i);
        songHolder.bind(s);
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

}


