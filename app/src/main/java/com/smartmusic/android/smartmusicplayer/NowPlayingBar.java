package com.smartmusic.android.smartmusicplayer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartmusic.android.smartmusicplayer.database.entities.Song;
import com.smartmusic.android.smartmusicplayer.events.SongPlaybackEvent;
import com.smartmusic.android.smartmusicplayer.events.SongPlaybackEventListener;
import com.smartmusic.android.smartmusicplayer.nowplaying.NowPlaying;
import com.squareup.picasso.Picasso;

import be.rijckaert.tim.animatedvector.FloatingMusicActionButton;

/**
 * Class that controls the "Now Playing" bar found
 * in multiple screens. In order to use this class,
 * the referencing view/Activity must include it
 * in their layout.
 */
public class NowPlayingBar implements SongPlaybackEventListener {

    private View nowPlayingBar; // Top level view
    private TextView songName;
    private TextView artistName;
    private ImageView albumArt;
    private FloatingMusicActionButton actionButton;

    private Context context;
    private FragmentManager supportFragmentManager;

    public NowPlayingBar(Context context, View parentView, FragmentManager supportFragmentManager){
        this.context = context;
        this.supportFragmentManager = supportFragmentManager;

        initNowPlayingBar(parentView);
        SPMainActivity.getSongEventHandler().addSongPlaybackEventListener(this);
    }

    /**
     * Initializes the elements.
     */
    private void initNowPlayingBar(View parentView){
        if( parentView == null ){ return; }

        // Reference views
        this.nowPlayingBar =    parentView.findViewById(R.id.includedNowPlayingLayout);
        this.songName =         nowPlayingBar.findViewById(R.id.now_playing_small_songName);
        this.artistName =       nowPlayingBar.findViewById(R.id.now_playing_small_artistName);
        this.albumArt =         nowPlayingBar.findViewById(R.id.image_album_art);
        this.actionButton =     nowPlayingBar.findViewById(R.id.now_playing_small_play_button);

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
                onMediaButtonPress();
            }
        });
        nowPlayingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transitionToNowPlaying();
            }
        });

    }


    /**
     * Hides the "Now Playing" bar by animating
     * it out of the screen.
     */
    private void hide(){
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
    private void show(){
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
     */
    private void onMediaButtonPress(){
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
        SPMainActivity.getSongEventHandler().removeSongPlaybackEventListener(this);
    }

    /**
     * Transitions to Now Playing fragment.
     */
    private void transitionToNowPlaying(){
        // TODO: Cleanup transition
        NowPlaying nowPlayingFrag
                = (NowPlaying)supportFragmentManager.findFragmentByTag(context.getString(R.string.NOW_PLAYING_TAG));

        //Make sure Now Playing is instantiated
        if(nowPlayingFrag == null){
            nowPlayingFrag = new NowPlaying();
        }

        // Fade out the old fragment
//                Fade exitFade = new Fade();
//                exitFade.setDuration(3000);
//                setExitTransition(exitFade);

//        TransitionSet enterTransitionSet = new TransitionSet();
//        enterTransitionSet.addTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.move));
//        enterTransitionSet.addTransition(TransitionInflater.from(context).inflateTransition(R.transition.change_bounds));
//        enterTransitionSet.setDuration(1000);
//                enterTransitionSet.setStartDelay(FADE_DEFAULT_TIME);
//        nowPlayingFrag.setSharedElementEnterTransition(enterTransitionSet);
//                nowPlayingFrag.setSharedElementReturnTransition(enterTransitionSet);


        nowPlayingFrag.setEnterTransition(new Slide(Gravity.BOTTOM).setDuration(1000));
        nowPlayingFrag.setExitTransition(new Slide(Gravity.BOTTOM).setDuration(1000));
//                ChangeBounds cb = new ChangeBounds();

        // Defines enter transition only for shared element
//                ChangeBounds changeBoundsTransition = (ChangeBounds)TransitionInflater.from(getContext()).inflateTransition(R.transition.change_bounds);
//                ChangeBounds changeBoundsTransition = new ChangeBounds();
//                nowPlayingFrag.setSharedElementEnterTransition(changeBoundsTransition);

        // Prevent transitions for overlapping
//        nowPlayingFrag.setAllowEnterTransitionOverlap(true);
//        nowPlayingFrag.setAllowReturnTransitionOverlap(true);

        supportFragmentManager
                .beginTransaction()
                .addSharedElement(actionButton, context.getString(R.string.play_button_transition_name))
                .addSharedElement(songName, context.getString(R.string.song_title_transition_name))
                .addSharedElement(artistName, context.getString(R.string.artist_name_transition_name))
                .addSharedElement(albumArt, context.getString(R.string.album_art_transition_name))
                .replace(R.id.fragment_container, nowPlayingFrag)
                .addToBackStack(null)
                .commit();
    } // end transitionToNowPlaying

    @Override
    public void onSongChangeEvent(SongPlaybackEvent e) {
        update(e.getSource());
        show();
    }
    @Override
    public void onSongStopEvent(SongPlaybackEvent e) {
        hide();
    }
}
