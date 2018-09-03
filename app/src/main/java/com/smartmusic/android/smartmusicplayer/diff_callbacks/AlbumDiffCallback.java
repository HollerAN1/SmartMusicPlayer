package com.smartmusic.android.smartmusicplayer.diff_callbacks;

import android.support.v7.util.DiffUtil;

import com.smartmusic.android.smartmusicplayer.database.entities.Album;

import java.util.List;

public class AlbumDiffCallback extends DiffUtil.Callback {

    public final List<Album> mOldList;
    public final List<Album> mNewList;

    public AlbumDiffCallback(List<Album> newAlbumList, List<Album> oldAlbumList){
        this.mNewList = newAlbumList;
        this.mOldList = oldAlbumList;
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
        return mOldList.get(oldItemPosition).getAlbumUID().equals(mNewList.get(newItemPosition).getAlbumUID());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Album oldAlbum = mOldList.get(oldItemPosition);
        Album newAlbum = mNewList.get(newItemPosition);

        return (oldAlbum.getAlbumUID().equals(newAlbum.getAlbumUID()));
    }
}
