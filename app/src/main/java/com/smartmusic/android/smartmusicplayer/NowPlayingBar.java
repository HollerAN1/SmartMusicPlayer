package com.smartmusic.android.smartmusicplayer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartmusic.android.smartmusicplayer.database.entities.Song;
import com.squareup.picasso.Picasso;

import be.rijckaert.tim.animatedvector.FloatingMusicActionButton;

/**
 * Class that contros the "Now Playing" bar found
 * in multiple screens. In order to use this class,
 * view/Activity must include it in their layout.
 */
public class NowPlayingBar implements SongEventListener {

    private View nowPlayingBar; // Top level view
    private TextView songName;
    private TextView artistName;
    private ImageView albumArt;
    private FloatingMusicActionButton actionButton;

    private Context context;

    public NowPlayingBar(Context context, View parentView){
        this.context = context;
        this.nowPlayingBar = parentView.findViewById(R.id.includedNowPlayingLayout);
        this.songName = (TextView) nowPlayingBar.findViewById(R.id.now_playing_small_songName);
        this.artistName = (TextView) nowPlayingBar.findViewById(R.id.now_playing_small_artistName);
        this.albumArt = (ImageView) nowPlayingBar.findViewById(R.id.image_album_art);
        this.actionButton = (FloatingMusicActionButton) nowPlayingBar.findViewById(R.id.now_playing_small_play_button);

        initNowPlayingBar();
        SPMainActivity.getSongEventHandler().addSongEventListener(this);
    }

    /**
     * Initializes the elements.
     */
    private void initNowPlayingBar(){
        if(SPMainActivity.mPlayerService == null || !SPMainActivity.mPlayerService.isSongPlaying()) {
            nowPlayingBar.animate().translationY(nowPlayingBar.getHeight())
                    .alpha(0.0f)
                    .setDuration(0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            nowPlayingBar.setVisibility(View.GONE);
                        }
                    });
        } else {
            nowPlayingBar.setVisibility(View.VISIBLE);
        }

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMediaButtonPress(v);
            }
        });

    }


    /**
     * Hides the "Now Playing" bar by animating
     * it out of the screen.
     */
    public void hide(){
        nowPlayingBar.animate().translationY(nowPlayingBar.getHeight())
                .alpha(0.0f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        nowPlayingBar.setVisibility(View.GONE);
                    }
                });
    }


    /**
     * Shows the "Now Playing" bar by animating
     * it onto the screen.
     */
    public void show(){
        nowPlayingBar.animate().translationY(0)
                .alpha(1.0f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationEnd(animation);
                        nowPlayingBar.setVisibility(View.VISIBLE);
                    }
                });
    }


    /**
     * Updates the views in the "Now Playing" bar.
     * @param s The song that is currently playing.
     */
    public void update(Song s){
        if(s != null && nowPlayingBar != null) {
            songName.setText(s.getSongName());
            artistName.setText(s.getArtistName());

            Uri uri = s.getAlbumArt();
            Picasso.with(context)
                    .load(uri)
                    .placeholder(R.drawable.temp_album_art)
                    .error(R.drawable.temp_album_art)
                    .into(albumArt);

            if(SPMainActivity.mPlayerService.isSongPlaying()){
                actionButton.changeMode(FloatingMusicActionButton.Mode.PLAY_TO_PAUSE);
            } else {
                actionButton.changeMode(FloatingMusicActionButton.Mode.PAUSE_TO_PLAY);
            }
        }
    }

    /**
     * Controls the media action button on
     * the right side of the "Now Playing" bar.
     * @param v The view
     */
    public void onMediaButtonPress(View v){
        if(SPMainActivity.mPlayerService.isMediaPlayerSet()){
            if(SPMainActivity.mPlayerService.isSongPlaying()){
                SPMainActivity.mPlayerService.pause();
            }
            else{
                SPMainActivity.mPlayerService.resume();
            }
            actionButton.changeMode(actionButton.getOppositeMode());
        }
    }

    public void removeSongEventListener(){
        SPMainActivity.getSongEventHandler().removeSongEventListener(this);
    }

    /**
     * Sets an onClickListener to the view
     * @param listener the listener to set
     */
    public void setOnClickListener(View.OnClickListener listener){
        this.nowPlayingBar.setOnClickListener(listener);
    }

    @Override
    public void onSongChangeEvent(SongEvent e) {
        update(e.getSource());
        show();
    }
    @Override
    public void onSongStopEvent(SongEvent e) {
        hide();
    }

    @Override
    public void onShuffleOnEvent(SongEvent e) {}
    @Override
    public void onShuffleOffEvent(SongEvent e) {}
    @Override
    public void onSongAddedEvent(SongEvent e) {}
    @Override
    public void onSongRemovedEvent(SongEvent e) {}
}
