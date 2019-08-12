package com.smartmusic.android.smartmusicplayer.library.view_holders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.smartmusic.android.smartmusicplayer.R;
import com.smartmusic.android.smartmusicplayer.database.entities.Song;
import com.smartmusic.android.smartmusicplayer.utils.SPUtils;

/**
 * Represents a song when displayed under an album.
 */
public class SongHolder_Album extends SongListViewHolder {
    private TextView songName,songDuration;
    private View background;
    private TextView albumNumber;

    private Context context;

    public SongHolder_Album(Context context, View itemView) {
        super(itemView);

        this.context = context;

        songName =              itemView.findViewById(R.id.song_title);
        songDuration =          itemView.findViewById(R.id.song_duration);
        background =            itemView.findViewById(R.id.song_background);
        albumNumber =           itemView.findViewById(R.id.album_number);
    }


    public void bind(Song s){
        songName.setText(s.getSongName());
        songDuration.setText(SPUtils.milliToTime((int)s.getDuration()));
        albumNumber.setText(""); // TODO: Get album number from song.
    }
}
