package com.smartmusic.android.smartmusicplayer.database.view_models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.smartmusic.android.smartmusicplayer.comparators.albums.AlbumNameComparator;
import com.smartmusic.android.smartmusicplayer.database.SPRepository;
import com.smartmusic.android.smartmusicplayer.database.entities.Album;
import com.smartmusic.android.smartmusicplayer.database.entities.AlbumWithSongs;

import java.util.Comparator;
import java.util.List;

public class AlbumsViewModel extends AndroidViewModel {
    private SPRepository mRepository;
    private LiveData<List<Album>> mAllAlbums;
    private Comparator<Album> sort;

    public AlbumsViewModel(Application application) {
        super(application);
        mRepository = new SPRepository(application);
        mAllAlbums = mRepository.getAllAlbums();
        this.sort = new AlbumNameComparator();
    }

    public LiveData<List<Album>> getAllAlbums() { return mAllAlbums; }

    public Comparator<Album> getSort() {
        return sort;
    }

    public void setSort(Comparator<Album> sort) {
        this.sort = sort;
    }
}