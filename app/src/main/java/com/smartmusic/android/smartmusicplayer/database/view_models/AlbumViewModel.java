package com.smartmusic.android.smartmusicplayer.database.view_models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.smartmusic.android.smartmusicplayer.database.RoomSQLDatabase;
import com.smartmusic.android.smartmusicplayer.database.SPRepository;
import com.smartmusic.android.smartmusicplayer.database.entities.Song;

import java.util.List;

public class AlbumViewModel extends AndroidViewModel{
    private SPRepository mRepository;
    private LiveData<List<Song>> mSongs;

    public AlbumViewModel(Application application) {
        super(application);
        mRepository = new SPRepository(application);
    }

    public LiveData<List<Song>> getAllAlbumSongs() { return mSongs; }
    public void setAlbumUID(String albumUID){
        mSongs = mRepository.getSongsForAlbum(albumUID);
    }
}
