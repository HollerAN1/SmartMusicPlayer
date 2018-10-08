package com.smartmusic.android.smartmusicplayer.library;

import android.content.Context;
import android.content.Intent;
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
import com.smartmusic.android.smartmusicplayer.R;
import com.smartmusic.android.smartmusicplayer.album.AlbumActivity;
import com.smartmusic.android.smartmusicplayer.database.entities.Album;
import com.smartmusic.android.smartmusicplayer.database.entities.Artist;
import com.smartmusic.android.smartmusicplayer.database.entities.Song;
import com.smartmusic.android.smartmusicplayer.diff_callbacks.AlbumDiffCallback;
import com.smartmusic.android.smartmusicplayer.diff_callbacks.ArtistDiffCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumHolder> implements SectionTitleProvider {

    private List<Album> _albums;
    private Context context;

    /**
     * Constructor for SongAdapter
     * @param context context
     * @param albums list of albums
     */
    public AlbumAdapter(Context context, List<Album> albums) {
        this.context = context;
        this._albums = albums;
    }


    @Override
    public String getSectionTitle(int position) {
        //this String will be shown in a bubble for specified position
        if(_albums != null) {
            return _albums.get(position).getAlbumName().substring(0, 1);
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
    public AlbumHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        /*Inflate a new view hierarchy from the specified xml resource.
        .inflate(resource, root view, boolean attach to root)*/
        View myView = LayoutInflater.from(context).inflate(R.layout.tile_album_flat,viewGroup,false);
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
     Override onBindViewHolder(HeaderViewHolder, int, List) instead if Adapter
     can handle efficient partial bind.
     * @param albumHolder The HeaderViewHolder which should be updated to represent
     *                   the contents of the item at the given position in
     *                   the data set.
     * @param i          The position of the item within the adapter's data
     *                   set.
     */
    @Override
    public void onBindViewHolder(final AlbumHolder albumHolder, final int i) {
        final Album al = _albums.get(i);
        albumHolder.tvAlbumName.setText(al.getAlbumName());
        albumHolder.tvArtistName.setText(al.getArtistName());

        Uri uri = _albums.get(i).getAlbumArt();
        Picasso.with(context)
                .load(uri)
                .placeholder(R.drawable.temp_album_art)
                .error(R.drawable.temp_album_art)
                .into(albumHolder.tileAlbumArt);

        albumHolder.background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transitionToAlbumActivity(al);
            }
        });
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if(_albums != null) {
            return _albums.size();
        }
        return 0;
    }

    public void setAlbums(List<Album> albums){
        final AlbumDiffCallback callback = new AlbumDiffCallback(this._albums, albums);
        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        this._albums = albums;
        result.dispatchUpdatesTo(this);
    }

    public void addAlbum(Album album){
        List<Album> newAlbums = _albums;
        newAlbums.add(album);
        setAlbums(newAlbums);
    }

    private void transitionToAlbumActivity(Album a){
        Intent intent = new Intent(context, AlbumActivity.class);
        intent.putExtra(context.getString(R.string.EXTRA_ALBUM_UID), a.getAlbumUID());
        intent.putExtra(context.getString(R.string.EXTRA_ALBUM_NAME), a.getAlbumName());
        intent.putExtra(context.getString(R.string.EXTRA_ALBUM_ARTIST), a.getArtistName());
        intent.putExtra(context.getString(R.string.EXTRA_ALBUM_ART), a.getAlbumArt().toString());


//        final ImageView albumArt = (ImageView) view.findViewById(R.id.tile_albumArt);
//        final TextView artistName = (TextView) view.findViewById(R.id.tile_artist_name);
//                final TextView albumName = (TextView) view.findViewById(R.id.tile_album_name);
//
//                Pair<View, String> albumArtTransition = Pair.create((View)albumArt, getString(R.string.album_art_transition_name));
//                Pair<View, String> artistNameTransition = Pair.create((View)artistName, getString(R.string.artist_name_transition_name));
//                Pair<View, String> containerTransition = Pair.create(view, getString(R.string.album_container_transition_name));
//                ActivityOptionsCompat options = ActivityOptionsCompat
//                        .makeSceneTransitionAnimation(getActivity(), albumArtTransition, artistNameTransition, containerTransition);

        context.startActivity(intent);
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
    public class AlbumHolder extends RecyclerView.ViewHolder{
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
    }
}




