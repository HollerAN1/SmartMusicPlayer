package com.smartmusic.android.smartmusicplayer.events;

import java.util.EventListener;

/**
 *
 * Created by holle on 3/14/2018.
 */

public interface SongPlaybackEventListener extends EventListener{
    void onSongChangeEvent(SongPlaybackEvent e);
    void onSongStopEvent(SongPlaybackEvent e);
}
