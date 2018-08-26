package com.smartmusic.android.smartmusicplayer;

import android.support.v7.util.DiffUtil;

import com.smartmusic.android.smartmusicplayer.database.entities.Song;

import java.util.List;

public class SongDiffCallback extends DiffUtil.Callback {

    public final List<Song> mOldList;
    public final List<Song> mNewList;

    public SongDiffCallback(List<Song> oldSongList, List<Song> newSongList){
        this.mOldList = oldSongList;
        this.mNewList = newSongList;
    }

    @Override
    public int getOldListSize() {
        if(mOldList != null){
            return mOldList.size();
        }
        return 0;
    }

    @Override
    public int getNewListSize() {
        if(mNewList != null){
            return mNewList.size();
        }
        return 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.get(oldItemPosition).getUid().equals(mNewList.get(newItemPosition).getUid());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Song oldSong = mOldList.get(oldItemPosition);
        Song newSong = mNewList.get(newItemPosition);

        return (oldSong.getSongName().equals(newSong.getSongName())) &&
                (oldSong.getArtistName().equals(newSong.getArtistName())) &&
                (oldSong.getAlbumName().equals(newSong.getAlbumName())) &&
                (oldSong.getUid().equals(newSong.getUid())) &&
                (oldSong.isSelected() == newSong.isSelected());
    }
}
