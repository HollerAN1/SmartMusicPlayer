package com.smartmusic.android.smartmusicplayer.diff_callbacks;

import android.support.v7.util.DiffUtil;

import com.smartmusic.android.smartmusicplayer.database.entities.Artist;

import java.util.List;

public class ArtistDiffCallback extends DiffUtil.Callback {

    public final List<Artist> mOldList;
    public final List<Artist> mNewList;

    public ArtistDiffCallback(List<Artist> newArtistList, List<Artist> oldArtistList){
        this.mNewList = newArtistList;
        this.mOldList = oldArtistList;
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
        return mOldList.get(oldItemPosition).getArtistUID().equals(mNewList.get(newItemPosition).getArtistUID());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Artist oldArtist = mOldList.get(oldItemPosition);
        Artist newArtist = mNewList.get(newItemPosition);

        return (oldArtist.getArtistUID().equals(newArtist.getArtistUID()));
    }
}
