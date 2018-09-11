package com.smartmusic.android.smartmusicplayer.events;

import java.util.EventListener;

public interface SongDatabaseChangedListener extends EventListener {
    void onSongAddedEvent(SongEvent e);
    void onSongRemovedEvent(SongEvent e);
}
