package com.smartmusic.android.smartmusicplayer.database.daos;

        import android.arch.lifecycle.LiveData;
        import android.arch.persistence.room.Dao;
        import android.arch.persistence.room.Insert;
        import android.arch.persistence.room.Query;
        import com.smartmusic.android.smartmusicplayer.database.entities.Song;
        import com.smartmusic.android.smartmusicplayer.database.entities.SongPlaylistJoin;

        import java.util.List;

@Dao
public abstract class SongPlaylistJoinDao {
    @Insert
    public abstract void insert(SongPlaylistJoin songPlaylistJoin);

    @Query("SELECT * FROM song_table INNER JOIN" +
            " song_playlist_join ON song_table.songUID = song_playlist_join.songUID " +
            "WHERE song_playlist_join.playlistUID=:playlistUID")
    public abstract LiveData<List<Song>> getSongsForPlaylist(final String playlistUID);
}
