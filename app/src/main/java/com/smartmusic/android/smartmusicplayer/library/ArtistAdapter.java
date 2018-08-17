package com.smartmusic.android.smartmusicplayer.library;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.smartmusic.android.smartmusicplayer.model.ArtistInfo;
import com.smartmusic.android.smartmusicplayer.model.SongInfo;
import com.smartmusic.android.smartmusicplayer.R;

import java.util.ArrayList;
import java.util.Collections;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistHolder> {

    /**
     * ArrayList of all songs
     * */
    private ArrayList<ArtistInfo> _artists = new ArrayList<>();
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

    ArtistHolder artistHolder;

    /**
     * Constructor for SongAdapter
     * @param context context
     * @param artists list of artists
     */
    public ArtistAdapter(Context context, ArrayList<ArtistInfo> artists) {
        this.context = context;
        this._artists = artists;
    }

    public interface OnItemClickListener {
        void onItemClick(ImageButton b, View view, SongInfo obj, int position, ArrayList<SongInfo> songs, int i);
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

    public ArtistHolder getArtistHolder(){
        return this.artistHolder;
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
    public ArtistHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        /*Inflate a new view hierarchy from the specified xml resource.
        .inflate(resource, root view, boolean attach to root)*/
        View myView = LayoutInflater.from(context).inflate(R.layout.row_artist,viewGroup,false);
        return new ArtistHolder(myView);
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
     * @param artistHolder The ViewHolder which should be updated to represent
     *                   the contents of the item at the given position in
     *                   the data set.
     * @param i          The position of the item within the adapter's data
     *                   set.
     */
    @Override
    public void onBindViewHolder(final ArtistHolder artistHolder, final int i) {
        final ArtistInfo a = _artists.get(i);
        this.artistHolder = artistHolder;
        artistHolder.tvArtistName.setText(a.getArtistname());
        artistHolder.tvSongCount.setText(a.getSongCount() + " songs");
        artistHolder.tvArtistName.setTypeface(Typeface.createFromAsset(
                                                        context.getAssets(),
                                                        context.getString(R.string.raleway_regular_font)));
        artistHolder.tvSongCount.setTypeface(Typeface.createFromAsset(
                                                        context.getAssets(),
                                                        context.getString(R.string.high_tea_font)));

        String letter = String.valueOf(a.getArtistname().charAt(0));

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(letter, ColorGenerator.MATERIAL.getRandomColor());

        artistHolder.letter.setImageDrawable(drawable);


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
        return _artists.size();
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
    public class ArtistHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvArtistName,tvSongCount;
        View background;
        ImageView letter;

        public ArtistHolder(View itemView) {
            super(itemView);
            tvArtistName = (TextView) itemView.findViewById(R.id.row_artist_artist_name);
            tvSongCount = (TextView) itemView.findViewById(R.id.row_artist_song_count);
            letter = (ImageView) itemView.findViewById(R.id.gmailitem_letter);
        }

        public void onClick(View view){

        }
    }
}



