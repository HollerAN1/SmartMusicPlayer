package com.smartmusic.android.smartmusicplayer.database.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.support.annotation.NonNull;

@Entity(tableName = "song_playlist_join",
                primaryKeys = {"songUID", "playlistUID"},
                foreignKeys = {
                            @ForeignKey(entity = Song.class,
                                        parentColumns = "songUID",
                                        childColumns = "songUID"),
                            @ForeignKey(entity = Playlist.class,
                                        parentColumns = "playlistUID",
                                        childColumns = "playlistUID")
                })
public class SongPlaylistJoin {
    @NonNull
    public final String songUID;
    @NonNull
    public final String playlistUID;

    public SongPlaylistJoin(final String songUID, final String playlistUID){
        this.songUID = songUID;
        this.playlistUID = playlistUID;
    }
}
