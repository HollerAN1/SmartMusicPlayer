package com.smartmusic.android.smartmusicplayer.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartmusic.android.smartmusicplayer.R;
import com.smartmusic.android.smartmusicplayer.database.entities.Album;
import com.smartmusic.android.smartmusicplayer.database.entities.Artist;
import com.smartmusic.android.smartmusicplayer.database.entities.Playlist;
import com.smartmusic.android.smartmusicplayer.database.entities.Song;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class SearchResultsAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private LayoutInflater mInflater;
    private Context context;

    private final static int SONG_HEADER_ID = 0;
    private final static int ARTIST_HEADER_ID = 1;
    private final static int ALBUM_HEADER_ID = 2;
    private final static int PLAYLIST_HEADER_ID = 3;

    private List<Song> songs = new ArrayList<>();
    private List<Artist> artists = new ArrayList<>();
    private List<Album> albums = new ArrayList<>();
    private List<Playlist> playlists = new ArrayList<>();

    public SearchResultsAdapter(Context context){
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.header_view, parent, false);
            holder.headerText = (TextView) convertView.findViewById(R.id.header_text);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        int headerId = (int)getHeaderId(position);
        switch (headerId){
            case SONG_HEADER_ID:
                holder.headerText.setText(R.string.pager_title_songs);
                break;
            case ARTIST_HEADER_ID:
                holder.headerText.setText(R.string.pager_title_artists);
                break;
            case ALBUM_HEADER_ID:
                holder.headerText.setText(R.string.pager_title_albums);
                break;
            case PLAYLIST_HEADER_ID:
                holder.headerText.setText("Playlists"); //TODO: Change to R.string
                break;
            default:
                break;
        }

        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        Object item = getItem(position);
        if(item instanceof Song){
            return SONG_HEADER_ID;
        } else if (item instanceof Artist){
            return ARTIST_HEADER_ID;
        } else if (item instanceof Album) {
            return ALBUM_HEADER_ID;
        } else if (item instanceof Playlist){
            return PLAYLIST_HEADER_ID;
        } else {
            return 0;
        }
    }

    @Override
    public int getCount() {
        return songs.size() +
                artists.size() +
                albums.size() +
                playlists.size();
    }

    @Override
    public Object getItem(int i) {
        int pos = i;
        if( i < songs.size()){
            return songs.get(i);
        } else {
            i -= songs.size();
            if( i < artists.size() ){
                return artists.get(i);
            } else {
                i -= artists.size();
                if( i < albums.size()){
                    return albums.get(i);
                } else {
                    i -= albums.size();
                    if( i < playlists.size() ){
                        return playlists.get(i);
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ItemViewHolder holder;

        if (view == null) {
            holder = new ItemViewHolder();
            view = mInflater.inflate(R.layout.search_item_view, viewGroup, false);
            holder.itemName = (TextView) view.findViewById(R.id.search_item_name);
            holder.additionalInfo = (TextView) view.findViewById(R.id.search_item_additional_info);
            holder.image = (ImageView) view.findViewById(R.id.search_item_image);
            view.setTag(holder);
        } else {
            holder = (ItemViewHolder) view.getTag();
        }

        Object item = getItem(i);
        if(item instanceof Song){
            Song song = (Song)item;
            holder.itemName.setText(song.getSongName());
            holder.additionalInfo.setText(song.getArtistName());
            Picasso.with(context)
                    .load(song.getAlbumArtUri())
                    .placeholder(R.drawable.temp_album_art)
                    .error(R.drawable.temp_album_art)
                    .into(holder.image);
        } else if (item instanceof Artist){
            Artist artist = (Artist)item;
            holder.itemName.setText(artist.getArtistName());
            holder.additionalInfo.setText("");
        } else if (item instanceof Album) {
            Album album = (Album)item;
            holder.itemName.setText(album.getAlbumName());
            holder.additionalInfo.setText(album.getArtistName());
            Picasso.with(context)
                    .load(album.getAlbumArt())
                    .placeholder(R.drawable.temp_album_art)
                    .error(R.drawable.temp_album_art)
                    .into(holder.image);
        } else if (item instanceof Playlist){
            Playlist playlist = (Playlist)item;
            holder.itemName.setText(playlist.getName());
            holder.additionalInfo.setText(playlist.getDescription());
            Picasso.with(context)
                    .load(playlist.getAlbumArt())
                    .placeholder(R.drawable.temp_album_art)
                    .error(R.drawable.temp_album_art)
                    .into(holder.image);
        }

        return view;
    }

    public void setSongsResult(List<Song> songs){
        this.songs = songs;
    }

    public void setArtistsResult(List<Artist> artists){
        this.artists = artists;
    }

    public void setAlbumsResult(List<Album> albums){
        this.albums = albums;
    }

    public void setPlaylistsResult(List<Playlist> playlists){
        this.playlists = playlists;
    }


    public static class HeaderViewHolder{
        public TextView headerText;
    }

    public static class ItemViewHolder{
        public TextView itemName;
        public TextView additionalInfo;
        public ImageView image;
    }
}
