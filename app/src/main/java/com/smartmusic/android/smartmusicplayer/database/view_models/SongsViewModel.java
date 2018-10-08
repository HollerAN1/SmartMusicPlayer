package com.smartmusic.android.smartmusicplayer.database.view_models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.smartmusic.android.smartmusicplayer.database.RoomSQLDatabase;
import com.smartmusic.android.smartmusicplayer.database.SPRepository;
import com.smartmusic.android.smartmusicplayer.database.entities.Song;

import java.util.List;

public class SongsViewModel extends AndroidViewModel {
    private SPRepository mRepository;
    private LiveData<List<Song>> mAllSongs;
    private Sort sort;

    public enum Sort {
        NAME, ARTIST, ALBUM
    }

    public SongsViewModel(Application application) {
        super(application);
        mRepository = new SPRepository(RoomSQLDatabase.getDatabase(application));
        mAllSongs = mRepository.getAllSongsNameSort();
    }

    public LiveData<List<Song>> getAllSongs() {
        return mAllSongs;
    }

    public void setSort(Sort newSort){
        switch(newSort){
            case NAME:
                mAllSongs = mRepository.getAllSongsNameSort();
                break;
            case ALBUM:
                mAllSongs = mRepository.getAllSongsAlbumSort();
                break;
            case ARTIST:
                mAllSongs = mRepository.getAllSongsArtistSort();
                break;
            default:
                mAllSongs = mRepository.getAllSongsUnsorted();
                break;
        }
    }

    public Sort getCurrentSort(){
        return this.sort;
    }

    public void updateSong(Song s){
        this.mRepository.insert(s);
    }
}
