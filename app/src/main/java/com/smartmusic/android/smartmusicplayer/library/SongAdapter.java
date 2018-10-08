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
import com.smartmusic.android.smartmusicplayer.utils.SPUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Adapters provide a binding from an app-specific data set to views that are displayed within a RecyclerView.
 * SongAdapter extends the RecyclerView Adapter to link the songInfo class to the layout
 * THIS HANDLES DATA COLLECTION AND BINDS IT TO THE VIEW
 */

public class SongAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SectionTitleProvider {

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
        //this String will be shown in a bubble for specified position
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        /*Inflate a new view hierarchy from the specified xml resource.
        .inflate(resource, root view, boolean attach to root)*/

        View holderView;
        switch(type){
            case LIBRARY:
                holderView = LayoutInflater.from(context).inflate(R.layout.row_song_flat,viewGroup,false);
                return new LibrarySongHolder(holderView);
            case ALBUM_LIST:
                holderView = LayoutInflater.from(context).inflate(R.layout.row_song_default,viewGroup,false);
                return new AlbumListSongHolder(holderView);
            default:
                holderView = LayoutInflater.from(context).inflate(R.layout.row_song_flat,viewGroup,false);
                return new LibrarySongHolder(holderView);
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
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder songHolder, final int i) {
        final Song s = _songs.get(i);
        switch (type){
            case LIBRARY:
                bindLibraryViewHolder((LibrarySongHolder)songHolder, s);
                break;
            case ALBUM_LIST:
                bindAlbumListViewHolder((AlbumListSongHolder)songHolder, s);
                break;
        }
    }


    private void bindLibraryViewHolder(LibrarySongHolder songHolder, final Song s){
        songHolder.tvSongName.setText(s.getSongName());
        songHolder.tvSongArtist.setText(s.getArtistName());

        Uri uri = s.getAlbumArt();
        Picasso.with(context)
                .load(uri)
                .placeholder(R.drawable.temp_album_art)
                .error(R.drawable.temp_album_art)
                .into(songHolder.tvAlbumArt);

        if(!s.isSelected()){
            setSongNotPlayingView(songHolder);
        } else {
            setSongPlayingView(songHolder);
        }

        songHolder.background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!s.isSelected()){
                    SPMainActivity.mPlayerService.playSong(s);
                    return;
                }
                // Stop the selected song
                if (s.isSelected() || (SPMainActivity.mPlayerService.isSongPlaying())) {
                    SPMainActivity.mPlayerService.stop();
                }
            }
        });
    }

    private void bindAlbumListViewHolder(AlbumListSongHolder songHolder, Song s){
        songHolder.songName.setText(s.getSongName());
        songHolder.songDuration.setText(SPUtils.milliToTime((int)s.getDuration()));
        songHolder.albumNumber.setText(""); // TODO: Get album number from song.
    }

    /**
     * Updates the view holder to the
     * song playing state.
     * @param songHolder the song holder to change
     */
    private void setSongPlayingView(LibrarySongHolder songHolder){
        songHolder.tvSongName.setHorizontallyScrolling(true);
        songHolder.background.setBackgroundColor(context.getResources().getColor(R.color.cardview_shadow_start_color));
        songHolder.tvSongName.setSelected(true);
        songHolder.tvSongArtist.setSelected(true);
    }

    /**
     * Updates the view holder to the song
     * not playing state.
     * @param songHolder the song holder to change
     */
    private void setSongNotPlayingView(LibrarySongHolder songHolder){
        songHolder.tvSongName.setHorizontallyScrolling(false);
        songHolder.background.setBackground(null);
        songHolder.tvSongName.setSelected(false);
        songHolder.tvSongArtist.setSelected(false);
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
    public class LibrarySongHolder extends RecyclerView.ViewHolder{
        private TextView tvSongName,tvSongArtist;
        private ImageView tvAlbumArt;
        private View background;

        public LibrarySongHolder(View itemView) {
            super(itemView);
            tvSongName =            itemView.findViewById(R.id.tvSongName);
            tvSongArtist =          itemView.findViewById(R.id.tvArtistName);
            background =            itemView.findViewById(R.id.list_background);
            tvAlbumArt =            itemView.findViewById(R.id.list_albumArt);
        }
    }

    public class AlbumListSongHolder extends RecyclerView.ViewHolder{
        private TextView songName,songDuration;
        private View background;
        private TextView albumNumber;

        public AlbumListSongHolder(View itemView) {
            super(itemView);
            songName =              itemView.findViewById(R.id.song_title);
            songDuration =          itemView.findViewById(R.id.song_duration);
            background =            itemView.findViewById(R.id.song_background);
            albumNumber =           itemView.findViewById(R.id.album_number);
        }
    }
}


