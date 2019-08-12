package com.smartmusic.android.smartmusicplayer.library.view_holders;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartmusic.android.smartmusicplayer.R;
import com.smartmusic.android.smartmusicplayer.SPMainActivity;
import com.smartmusic.android.smartmusicplayer.database.entities.Song;
import com.squareup.picasso.Picasso;

/**
 * Represents a song when displayed in the library view.
 */
public class SongHolder_Library extends SongListViewHolder {

    private TextView tvSongName,tvSongArtist;
    private ImageView tvAlbumArt;
    private View background;

    private Context context;

    public SongHolder_Library(Context context, View itemView) {
        super(itemView);

        this.context = context;

        tvSongName =            itemView.findViewById(R.id.tvSongName);
        tvSongArtist =          itemView.findViewById(R.id.tvArtistName);
        background =            itemView.findViewById(R.id.list_background);
        tvAlbumArt =            itemView.findViewById(R.id.list_albumArt);
    }

    public void bind(final Song s){
        tvSongName.setText(s.getSongName());
        tvSongArtist.setText(s.getArtistName());

        Uri uri = s.getAlbumArt();
        Picasso.with(context)
                .load(uri)
                .placeholder(R.drawable.temp_album_art)
                .error(R.drawable.temp_album_art)
                .into(tvAlbumArt);

        setPlayingState(!s.isSelected());

        background.setOnClickListener(new View.OnClickListener() {
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


    /**
     * Updates the view holder to the
     * song playing state.
     */
    private void setPlayingState(boolean isPlaying){
        tvSongName.setHorizontallyScrolling(isPlaying);

        if (isPlaying) {
            background.setBackgroundColor(context.getResources().getColor(R.color.cardview_shadow_start_color));
        } else {
            background.setBackground(null);
        }

        tvSongName.setSelected(isPlaying);
        tvSongArtist.setSelected(isPlaying);
    }
}