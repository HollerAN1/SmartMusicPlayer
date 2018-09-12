package com.smartmusic.android.smartmusicplayer.events;

import java.util.EventListener;

public interface SongDatabaseChangedListener extends EventListener {
    void onSongAddedEvent(SongDatabaseEvent e);
    void onSongRemovedEvent(SongDatabaseEvent e);
}
