package com.smartmusic.android.smartmusicplayer.database.view_models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.smartmusic.android.smartmusicplayer.comparators.artists.ArtistNameComparator;
import com.smartmusic.android.smartmusicplayer.database.SPRepository;
import com.smartmusic.android.smartmusicplayer.database.entities.Artist;

import java.util.Comparator;
import java.util.List;

public class ArtistsViewModel extends AndroidViewModel {
    private SPRepository mRepository;
    private LiveData<List<Artist>> mAllArtists;
    private Comparator<Artist> sort;

    public ArtistsViewModel(Application application) {
        super(application);
        mRepository = new SPRepository(application);
        mAllArtists = mRepository.getAllArtistsNameSort();
        this.sort = new ArtistNameComparator();
    }

    public LiveData<List<Artist>> getAllArtists() { return mAllArtists; }

    public Comparator<Artist> getSort() {
        return sort;
    }

    public void setSort(Comparator<Artist> sort) {
        this.sort = sort;
    }
}
