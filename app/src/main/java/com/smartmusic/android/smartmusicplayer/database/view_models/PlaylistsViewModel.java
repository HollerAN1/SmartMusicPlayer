package com.smartmusic.android.smartmusicplayer.database.view_models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.smartmusic.android.smartmusicplayer.database.RoomSQLDatabase;
import com.smartmusic.android.smartmusicplayer.database.SPRepository;
import com.smartmusic.android.smartmusicplayer.database.entities.Playlist;

import java.util.List;

public class PlaylistsViewModel extends AndroidViewModel {
    private SPRepository mRepository;
    private LiveData<List<Playlist>> mAllPlaylists;

    public PlaylistsViewModel(Application application) {
        super(application);
        mRepository = new SPRepository(application);
        mAllPlaylists = mRepository.getAllPlaylistsNameSort();
    }

    public LiveData<List<Playlist>> getAllPlaylists() { return mAllPlaylists; }
}
