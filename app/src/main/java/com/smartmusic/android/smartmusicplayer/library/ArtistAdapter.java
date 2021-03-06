package com.smartmusic.android.smartmusicplayer.library;

import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;
import com.smartmusic.android.smartmusicplayer.utils.SPUtils;
import com.smartmusic.android.smartmusicplayer.diff_callbacks.ArtistDiffCallback;
import com.smartmusic.android.smartmusicplayer.R;
import com.smartmusic.android.smartmusicplayer.database.entities.Artist;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistHolder> implements SectionTitleProvider {

    private List<Artist> _artists;
    private Context context;

    /**
     * Constructor for SongAdapter
     * @param context context
     * @param artists list of artists
     */
    public ArtistAdapter(Context context, List<Artist> artists) {
        this.context = context;
        this._artists = artists;
    }

    @Override
    public String getSectionTitle(int position) {
        //this String will be shown in a bubble for specified position
        if(_artists != null) {
            return _artists.get(position).getArtistName().substring(0, 1);
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
    public ArtistHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        /*Inflate a new view hierarchy from the specified xml resource.
        .inflate(resource, root view, boolean attach to root)*/
        View myView = LayoutInflater.from(context).inflate(R.layout.row_artist_flat,viewGroup,false);
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
     Override onBindViewHolder(HeaderViewHolder, int, List) instead if Adapter
     can handle efficient partial bind.
     * @param artistHolder The HeaderViewHolder which should be updated to represent
     *                   the contents of the item at the given position in
     *                   the data set.
     * @param i          The position of the item within the adapter's data
     *                   set.
     */
    @Override
    public void onBindViewHolder(final ArtistHolder artistHolder, final int i) {
        final Artist a = _artists.get(i);
        artistHolder.tvArtistName.setText(a.getArtistName());
        artistHolder.tvSongCount.setText(SPUtils.getFormattedSongsAndAlbumsForArtist(a));

        String letter = String.valueOf(a.getArtistName().charAt(0));

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(letter, ColorGenerator.MATERIAL.getRandomColor());

        artistHolder.letter.setImageDrawable(drawable);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if(_artists != null) {
            return _artists.size();
        }
        return 0;
    }


    public void setArtists(List<Artist> artists){
        final ArtistDiffCallback callback = new ArtistDiffCallback(this._artists, artists);
        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        this._artists = artists;
        result.dispatchUpdatesTo(this);
    }

    public void addArtist(Artist artist){
        List<Artist> newArtists = _artists;
        newArtists.add(artist);
        setArtists(newArtists);
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
    public class ArtistHolder extends RecyclerView.ViewHolder{
        TextView tvArtistName,tvSongCount;
        View background;
        ImageView letter;

        public ArtistHolder(View itemView) {
            super(itemView);
            tvArtistName = (TextView) itemView.findViewById(R.id.row_artist_artist_name);
            tvSongCount = (TextView) itemView.findViewById(R.id.row_artist_song_count);
            letter = (ImageView) itemView.findViewById(R.id.gmailitem_letter);
        }
    }
}



