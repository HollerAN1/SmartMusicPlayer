package com.smartmusic.android.smartmusicplayer.library;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartmusic.android.smartmusicplayer.model.AlbumInfo;
import com.smartmusic.android.smartmusicplayer.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumHolder> {

    /**
     * ArrayList of all songs
     * */
    private ArrayList<AlbumInfo> _albums = new ArrayList<>();
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

    Typeface artistNameTypeface;
    Typeface songCountTypeface;

    /**
     * Tells whether a song is currently playing.
     */
    Boolean songPlaying;

    AlbumHolder albumHolder;

    /**
     * Constructor for SongAdapter
     * @param context context
     * @param albums list of albums
     */
    public AlbumAdapter(Context context, ArrayList<AlbumInfo> albums, Typeface typeface, Typeface aTypeface) {
        this.context = context;
        Collections.sort(albums);
        this._albums= albums;
        this.artistNameTypeface = typeface;
        this.songCountTypeface = aTypeface;
        this.songPlaying = false;
    }

    public void setSongPlaying(Boolean bool){
        this.songPlaying = bool;
    }

    public boolean getSongPlaying(){ return this.songPlaying; }

    public interface OnItemClickListener {
        void onItemClick(ImageView b, View view, AlbumInfo obj, int position, ArrayList<AlbumInfo> albums, int i);
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

    public AlbumHolder getArtistHolder(){
        return this.albumHolder;
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
    public AlbumHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        /*Inflate a new view hierarchy from the specified xml resource.
        .inflate(resource, root view, boolean attach to root)*/
        View myView = LayoutInflater.from(context).inflate(R.layout.tile_album,viewGroup,false);
        return new AlbumHolder(myView);
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
     * @param albumHolder The ViewHolder which should be updated to represent
     *                   the contents of the item at the given position in
     *                   the data set.
     * @param i          The position of the item within the adapter's data
     *                   set.
     */
    @Override
    public void onBindViewHolder(final AlbumHolder albumHolder, final int i) {
        final AlbumInfo al = _albums.get(i);
        this.albumHolder = albumHolder;
        albumHolder.tvAlbumName.setText(al.getAlbumName());
        albumHolder.tvArtistName.setText(al.getArtistname());
        albumHolder.tvAlbumName.setTypeface(artistNameTypeface);
        albumHolder.tvArtistName.setTypeface(songCountTypeface);

        Uri uri = _albums.get(i).getAlbumArt();
        Picasso.with(context)
                .load(uri)
                .placeholder(R.drawable.temp_album_art)
                .error(R.drawable.temp_album_art)
                .into(albumHolder.tileAlbumArt);

        albumHolder.background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(null,v, _albums.get(i), albumHolder.getAdapterPosition(), _albums, i);

                }

            }
        });

//        artistHolder.background.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(backOnItemClickListener != null){
//                    backOnItemClickListener.onItemClick(artistHolder.btnAction,v,s,artistHolder.getAdapterPosition(),_artists,i);
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
        return _albums.size();
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
    public class AlbumHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvAlbumName, tvArtistName;
        ImageView tileAlbumArt;
        View background;

        public AlbumHolder(View itemView) {
            super(itemView);
            tvAlbumName = (TextView) itemView.findViewById(R.id.tile_album_name);
            tvArtistName = (TextView) itemView.findViewById(R.id.tile_artist_name);
            tileAlbumArt = (ImageView) itemView.findViewById(R.id.tile_albumArt);
            background = (View) itemView.findViewById(R.id.tile_album_card);
        }

        public void onClick(View view){

        }
    }
}




