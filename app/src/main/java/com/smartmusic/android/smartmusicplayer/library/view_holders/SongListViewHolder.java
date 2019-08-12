package com.smartmusic.android.smartmusicplayer.library.view_holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.smartmusic.android.smartmusicplayer.database.entities.Song;

public abstract class SongListViewHolder extends RecyclerView.ViewHolder {
    SongListViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bind(Song s);
}
